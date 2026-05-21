package dev.emi.trinkets;

import dev.emi.trinkets.payload.BreakPayload;
import dev.emi.trinkets.payload.SyncInventoryPayload;
import dev.emi.trinkets.payload.SyncSlotsPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

@Deprecated
public class TrinketsNetwork {

  public static final CustomPacketPayload.Type<SyncSlotsPayload> SYNC_SLOTS = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(TrinketsMain.MOD_ID, "sync_slots"));
  public static final CustomPacketPayload.Type<SyncInventoryPayload> SYNC_INVENTORY = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(TrinketsMain.MOD_ID, "sync_inventory"));
  public static final CustomPacketPayload.Type<BreakPayload> BREAK = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(TrinketsMain.MOD_ID, "break"));

  private TrinketsNetwork() {
  }
}