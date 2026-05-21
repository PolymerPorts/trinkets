package dev.emi.trinkets;

import dev.emi.trinkets.api.SlotReference;
import java.util.List;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;

public interface TrinketEntityRenderState {
    void trinkets$setState(List<Tuple<ItemStack, SlotReference>> items);
    List<Tuple<ItemStack, SlotReference>> trinkets$getState();
}
