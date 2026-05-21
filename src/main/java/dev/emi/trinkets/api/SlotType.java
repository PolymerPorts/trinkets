package dev.emi.trinkets.api;

import dev.emi.trinkets.api.TrinketEnums.DropRule;
import dev.emi.trinkets.poly.GuiModels;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SlotType {

	private final String group;
	private final String name;
	private final int order;
	private final int amount;
	private final Identifier icon;
	private final Set<Identifier> quickMovePredicates;
	private final Set<Identifier> validatorPredicates;
	private final Set<Identifier> tooltipPredicates;
	private final DropRule dropRule;

	private final ItemStack itemIcon;

	public SlotType(String group, String name, int order, int amount, Identifier icon, Set<Identifier> quickMovePredicates,
					Set<Identifier> validatorPredicates, Set<Identifier> tooltipPredicates, DropRule dropRule) {
		this(group, name, order, amount, icon, Items.IRON_CHESTPLATE.getDefaultInstance(), quickMovePredicates, validatorPredicates, tooltipPredicates, dropRule);
	}

	public SlotType(String group, String name, int order, int amount, Identifier icon, ItemStack itemIcon, Set<Identifier> quickMovePredicates,
					Set<Identifier> validatorPredicates, Set<Identifier> tooltipPredicates, DropRule dropRule) {
		this.group = group;
		this.name = name;
		this.order = order;
		this.amount = amount;
		this.icon = icon;
		this.quickMovePredicates = quickMovePredicates;
		this.validatorPredicates = validatorPredicates;
		this.tooltipPredicates = tooltipPredicates;
		this.dropRule = dropRule;

		this.itemIcon = itemIcon.copy();
		GuiModels.createModel(this.icon);
	}

	public String getGroup() {
		return group;
	}

	public String getName() {
		return name;
	}

	public int getOrder() {
		return order;
	}

	public int getAmount() {
		return amount;
	}

	public Identifier getIcon() {
		return icon;
	}

	public Set<Identifier> getQuickMovePredicates() {
		return quickMovePredicates;
	}

	public Set<Identifier> getValidatorPredicates() {
		return validatorPredicates;
	}

	public Set<Identifier> getTooltipPredicates() {
		return tooltipPredicates;
	}

	public DropRule getDropRule() {
		return dropRule;
	}

	public MutableComponent getTranslation() {
		return Component.translatable("trinkets.slot." + this.group + "." + this.name);
	}

	public void write(CompoundTag data) {
		CompoundTag tag = new CompoundTag();
		tag.putString("Group", group);
		tag.putString("Name", name);
		tag.putInt("Order", order);
		tag.putInt("Amount", amount);
		tag.putString("Icon", icon.toString());
		ListTag quickMovePredicateList = new ListTag();

		for (Identifier id : quickMovePredicates) {
			quickMovePredicateList.add(StringTag.valueOf(id.toString()));
		}
		tag.put("QuickMovePredicates", quickMovePredicateList);

		ListTag validatorPredicateList = new ListTag();

		for (Identifier id : validatorPredicates) {
			validatorPredicateList.add(StringTag.valueOf(id.toString()));
		}
		tag.put("ValidatorPredicates", validatorPredicateList);

		ListTag tooltipPredicateList = new ListTag();

		for (Identifier id : tooltipPredicates) {
			tooltipPredicateList.add(StringTag.valueOf(id.toString()));
		}
		tag.put("TooltipPredicates", tooltipPredicateList);
		tag.putString("DropRule", dropRule.toString());

		if (!this.itemIcon.isEmpty()) {
			tag.store("PolyPort$icon", ItemStack.OPTIONAL_CODEC, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY).createSerializationContext(NbtOps.INSTANCE), this.itemIcon);
		}
		data.put("SlotData", tag);
	}

	public static SlotType read(CompoundTag data) {
		CompoundTag slotData = data.getCompoundOrEmpty("SlotData");
		String group = slotData.getStringOr("Group", "");
		String name = slotData.getStringOr("Name", "");
		int order = slotData.getIntOr("Order", 0);
		int amount = slotData.getIntOr("Amount", 0);
		Identifier icon = Identifier.parse(slotData.getStringOr("Icon", ""));
		ListTag quickMoveList = slotData.getListOrEmpty("QuickMovePredicates");
		Set<Identifier> quickMovePredicates = new HashSet<>();

		for (Tag tag : quickMoveList) {
			if (tag instanceof StringTag string) {
				quickMovePredicates.add(Identifier.parse(string.value()));
			}
		}
		ListTag validatorList = slotData.getListOrEmpty("ValidatorPredicates");
		Set<Identifier> validatorPredicates = new HashSet<>();

		for (Tag tag : validatorList) {
			if (tag instanceof StringTag string) {
				validatorPredicates.add(Identifier.parse(string.value()));
			}
		}
		ListTag tooltipList = slotData.getListOrEmpty("TooltipPredicates");
		Set<Identifier> tooltipPredicates = new HashSet<>();

		for (Tag tag : tooltipList) {
			if (tag instanceof StringTag string) {
				tooltipPredicates.add(Identifier.parse(string.value()));
			}
		}
		String dropRuleName = slotData.getStringOr("DropRule", "");
		DropRule dropRule = DropRule.DEFAULT;

		if (TrinketEnums.DropRule.has(dropRuleName)) {
			dropRule = TrinketEnums.DropRule.valueOf(dropRuleName);
		}

		var itemIcon = slotData.read("PolyPort$icon", ItemStack.OPTIONAL_CODEC, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY).createSerializationContext(NbtOps.INSTANCE)).orElse(ItemStack.EMPTY);
		if (itemIcon == ItemStack.EMPTY) {
			itemIcon = Items.IRON_CHESTPLATE.getDefaultInstance();
		}

		return new SlotType(group, name, order, amount, icon, itemIcon, quickMovePredicates, validatorPredicates, tooltipPredicates, dropRule);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SlotType slotType = (SlotType) o;
		return group.equals(slotType.group) && name.equals(slotType.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(group, name);
	}

	public ItemStack getIconItem() {
		return this.itemIcon.copy();
	}

	public String getId() {
		return this.group + "/" + this.name;
	}
}
