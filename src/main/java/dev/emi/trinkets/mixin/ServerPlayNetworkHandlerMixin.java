package dev.emi.trinkets.mixin;

import dev.emi.trinkets.poly.TrinketsFlatUI;
import dev.emi.trinkets.poly.TrinketsPoly;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {
	@Shadow
	public ServerPlayer player;
	
	@Inject(method = "handleContainerClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;suppressRemoteUpdates()V", shift = At.Shift.BEFORE), cancellable = true)
	private void polyport_trinkets_handleClick(ServerboundContainerClickPacket packet, CallbackInfo ci) {
		if (packet.containerId() == this.player.inventoryMenu.containerId && packet.slotNum() >= 5 && packet.slotNum() <= 8 && packet.clickType() == ClickType.PICKUP && packet.buttonNum() == 1) {
			if (switch (packet.slotNum()) {
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
