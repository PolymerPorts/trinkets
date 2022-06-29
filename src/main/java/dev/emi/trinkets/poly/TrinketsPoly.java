package dev.emi.trinkets.poly;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.emi.trinkets.TrinketsMain;
import eu.pb4.playerdata.api.PlayerDataApi;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TrinketsPoly {
    public static final Identifier COMPACT_SETTING = new Identifier(TrinketsMain.MOD_ID, "compact_ui");

    public static void init() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("trinkets")
                    .executes(ctx -> TrinketsFlatUI.open(ctx.getSource().getPlayerOrThrow()))
                    .then(CommandManager.literal("compact").executes(TrinketsPoly::toggleCompactCommand))
            );
        }));

        Elements.FILLER.hashCode();
    }

    private static int toggleCompactCommand(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var player = ctx.getSource().getPlayerOrThrow();

        var isCompact = !getIsCompact(player);

        ctx.getSource().sendFeedback(Text.translatable("trinkets.command.compact." + isCompact), false);

        PlayerDataApi.setGlobalDataFor(player, COMPACT_SETTING, NbtByte.of(isCompact));

        return 0;
    }

    public static boolean getIsCompact(ServerPlayerEntity player) {
        var data = PlayerDataApi.getGlobalDataFor(player, COMPACT_SETTING, NbtByte.TYPE);

        if (data == null) {
            return true;
        } else {
            return data.byteValue() > 0;
        }
    }
}
