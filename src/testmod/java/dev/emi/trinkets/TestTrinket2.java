package dev.emi.trinkets;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class TestTrinket2 extends TrinketItem implements PolymerItem {

	public TestTrinket2(Settings settings) {
		super(settings);
	}

	@Override
	public Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, Identifier id) {
		// un-comment to check - testing the composition of the new attribute suffix
		// TrinketsTest.LOGGER.info(TrinketModifiers.toSlotReferencedModifier(new EntityAttributeModifier(id.withSuffixedPath("trinkets-testmod/movement_speed"),
		//		0.4, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), slot));
		Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiers = Multimaps.newMultimap(Maps.newLinkedHashMap(), ArrayList::new);
		EntityAttributeModifier speedModifier = new EntityAttributeModifier(id.withSuffixedPath("trinkets-testmod/movement_speed"),
				0.1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		modifiers.put(EntityAttributes.MOVEMENT_SPEED, speedModifier);
		return modifiers;
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
		return Items.FEATHER;
	}
}