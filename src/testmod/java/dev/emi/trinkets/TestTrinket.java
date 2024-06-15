package dev.emi.trinkets;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotAttributes;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TestTrinket extends TrinketItem implements PolymerItem {

    private static final Identifier TEXTURE = Identifier.of(TrinketsTest.MOD_ID, "textures/entity/trinket/hat.png");

    public TestTrinket(Settings settings) {
        super(settings);
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
		/*stack.damage(1, entity, e -> {
			TrinketsApi.onTrinketBroken(stack, slot, entity);
		});*/
    }

	@Override
	public Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, Identifier id) {
		Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiers = super.getModifiers(stack, slot, entity, id);
		EntityAttributeModifier speedModifier = new EntityAttributeModifier(id.withSuffixedPath("trinkets-testmod/movement_speed"),
				0.4, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		modifiers.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, speedModifier);
		SlotAttributes.addSlotModifier(modifiers, "offhand/ring", id.withSuffixedPath("trinkets-testmod/ring_slot"), 6, EntityAttributeModifier.Operation.ADD_VALUE);
		SlotAttributes.addSlotModifier(modifiers, "hand/glove", id.withSuffixedPath("trinkets-testmod/glove_slot"), 1, EntityAttributeModifier.Operation.ADD_VALUE);
		return modifiers;
	}

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.STICK;
    }
}
