package dev.emi.trinkets;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import dev.emi.trinkets.api.*;
import dev.emi.trinkets.payload.BreakPayload;
import dev.emi.trinkets.payload.SyncInventoryPayload;
import dev.emi.trinkets.payload.SyncSlotsPayload;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import dev.emi.trinkets.data.EntitySlotLoader;
import dev.emi.trinkets.data.SlotLoader;
import dev.emi.trinkets.poly.TrinketsFlatUI;
import dev.emi.trinkets.poly.TrinketsPoly;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;

import java.util.Map;

public class TrinketsMain implements ModInitializer, EntityComponentInitializer {

	public static final String MOD_ID = "trinkets";
	public static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize() {
		ResourceManagerHelper resourceManagerHelper = ResourceManagerHelper.get(PackType.SERVER_DATA);
		resourceManagerHelper.registerReloadListener(SlotLoader.INSTANCE);
		resourceManagerHelper.registerReloadListener(EntitySlotLoader.SERVER);
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success)
				-> EntitySlotLoader.SERVER.sync(server.getPlayerList().getPlayers()));

		UseItemCallback.EVENT.register((player, world, hand) -> {
			ItemStack stack = player.getItemInHand(hand);
			Trinket trinket = TrinketsApi.getTrinket(stack.getItem());
			if (trinket.canEquipFromUse(stack, player)) {
				if (TrinketItem.equipItem(player, stack)) {
					return InteractionResult.SUCCESS;
				}
			}
			return InteractionResult.PASS;
		});
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, "attribute_modifiers"), TrinketsAttributeModifiersComponent.TYPE);

		CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) ->
			dispatcher.register(literal("trinkets")
                .executes(ctx -> TrinketsFlatUI.open(ctx.getSource().getPlayerOrException()))
                .then(Commands.literal("compact").executes(TrinketsPoly::toggleCompactCommand))
				.then(
					literal("set")
                    .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
					.then(
						argument("group", string())
						.then(
							argument("slot", string())
							.then(
								argument("offset", integer(0))
								.then(
									argument("stack", ItemArgument.item(registry))
									.executes(context -> {
										try {
										return trinketsCommand(context, 1);

										} catch (Exception e) {
											e.printStackTrace();
											return -1;
										}
									})
									.then(
										argument("count", integer(1))
										.executes(context -> {
											int amount = context.getArgument("amount", Integer.class);
											return trinketsCommand(context, amount);
										})
									)
								)
							)
						)
					)
				)
				.then(
					literal("clear")
                    .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                    .executes(context -> {
						try {
							return clearCommand(context);
						} catch (Exception e){
							e.printStackTrace();
							return -1;
						}
					})
				)
			));

		TrinketsPoly.init();
	}

	private static int clearCommand(CommandContext<CommandSourceStack> context){
		ServerPlayer player = context.getSource().getPlayer();
		if (player != null) {
			TrinketComponent comp = TrinketsApi.getTrinketComponent(player).get();
			for (Map.Entry<String, Map<String, TrinketInventory>> entry : comp.getInventory().entrySet()){
				for (TrinketInventory inv : entry.getValue().values()){
					inv.clearContent();
				}
			}
		}
		return 1;
	}

	private static int trinketsCommand(CommandContext<CommandSourceStack> context, int amount) {
		try {
			String group = context.getArgument("group", String.class);
			String slot = context.getArgument("slot", String.class);
			int offset = context.getArgument("offset", Integer.class);
			ItemInput stack = context.getArgument("stack", ItemInput.class);
			ServerPlayer player = context.getSource().getPlayer();
			if (player != null) {
				TrinketComponent comp = TrinketsApi.getTrinketComponent(player).get();
				SlotGroup slotGroup = comp.getGroups().getOrDefault(group, null);
				if (slotGroup != null) {
					SlotType slotType = slotGroup.getSlots().getOrDefault(slot, null);
					if (slotType != null) {
						if (offset >= 0 && offset < slotType.getAmount()) {
							comp.getInventory().get(group).get(slot).setItem(offset, stack.createItemStack(amount, true));
							return Command.SINGLE_SUCCESS;
						} else {
							context.getSource().sendFailure(Component.literal(offset + " offset does not exist for slot"));
						}
					} else {
						context.getSource().sendFailure(Component.literal(slot + " does not exist"));
					}
				} else {
					context.getSource().sendFailure(Component.literal(group + " does not exist"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerFor(LivingEntity.class, TrinketsApi.TRINKET_COMPONENT, LivingEntityTrinketComponent::new);
		registry.registerForPlayers(TrinketsApi.TRINKET_COMPONENT, LivingEntityTrinketComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
	}
}