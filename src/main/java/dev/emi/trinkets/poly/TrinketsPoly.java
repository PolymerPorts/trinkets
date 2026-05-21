package dev.emi.trinkets.poly;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.emi.trinkets.TrinketsMain;
import dev.emi.trinkets.api.TrinketsAttributeModifiersComponent;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.ByteTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class TrinketsPoly {
    public static final PolyConfig CONFIG = PolyConfig.loadOrCreateConfig();
    public static final Identifier COMPACT_SETTING = Identifier.fromNamespaceAndPath(TrinketsMain.MOD_ID, "compact_ui");

    public static void init() {
        Elements.FILLER.hashCode();
        PolymerComponent.registerDataComponent(TrinketsAttributeModifiersComponent.TYPE);
    }

    public static int toggleCompactCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var player = ctx.getSource().getPlayerOrException();

        var isCompact = !getIsCompact(player);

        ctx.getSource().sendSuccess(() -> Component.translatable("trinkets.command.compact." + isCompact), false);

        PlayerDataApi.setGlobalDataFor(player, COMPACT_SETTING, ByteTag.valueOf(isCompact));

        return 0;
    }

    public static boolean getIsCompact(ServerPlayer player) {
        var data = PlayerDataApi.getGlobalDataFor(player, COMPACT_SETTING, ByteTag.TYPE);

        if (data == null) {
            return CONFIG.compactUi;
        } else {
            return data.byteValue() > 0;
        }
    }
}
