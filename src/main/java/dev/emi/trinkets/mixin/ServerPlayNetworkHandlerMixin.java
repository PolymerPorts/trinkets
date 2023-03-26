package dev.emi.trinkets.mixin;

import dev.emi.trinkets.poly.TrinketsFlatUI;
import dev.emi.trinkets.poly.TrinketsPoly;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	@Shadow
    public ServerPlayerEntity player;
	
	@Inject(method = "onClickSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;disableSyncing()V", shift = At.Shift.BEFORE), cancellable = true)
	private void polyport_trinkets_handleClick(ClickSlotC2SPacket packet, CallbackInfo ci) {
		if (packet.getSyncId() == this.player.playerScreenHandler.syncId && packet.getSlot() >= 5 && packet.getSlot() <= 8 && packet.getActionType() == SlotActionType.PICKUP && packet.getButton() == 1) {
			if (switch (packet.getSlot()) {
				case 5 -> TrinketsPoly.CONFIG.helmetSlot;
				case 6 -> TrinketsPoly.CONFIG.chestplateSlot;
				case 7 -> TrinketsPoly.CONFIG.leggingsSlot;
				case 8 -> TrinketsPoly.CONFIG.bootsSlot;
				default -> false;
			}) {
				TrinketsFlatUI.open(this.player);
			ci.cancel();

		}
		}
	}
}
