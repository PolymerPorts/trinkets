package dev.emi.trinkets.api;

import dev.emi.trinkets.api.TrinketEnums.DropRule;
import dev.emi.trinkets.poly.GuiModels;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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
		this(group, name, order, amount, icon, Items.IRON_CHESTPLATE.getDefaultStack(), quickMovePredicates, validatorPredicates, tooltipPredicates, dropRule);
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

	public MutableText getTranslation() {
		return Text.translatable("trinkets.slot." + this.group + "." + this.name);
	}

	public void write(NbtCompound data) {
		NbtCompound tag = new NbtCompound();
		tag.putString("Group", group);
		tag.putString("Name", name);
		tag.putInt("Order", order);
		tag.putInt("Amount", amount);
		tag.putString("Icon", icon.toString());
		NbtList quickMovePredicateList = new NbtList();

		for (Identifier id : quickMovePredicates) {
			quickMovePredicateList.add(NbtString.of(id.toString()));
		}
		tag.put("QuickMovePredicates", quickMovePredicateList);

		NbtList validatorPredicateList = new NbtList();

		for (Identifier id : validatorPredicates) {
			validatorPredicateList.add(NbtString.of(id.toString()));
		}
		tag.put("ValidatorPredicates", validatorPredicateList);

		NbtList tooltipPredicateList = new NbtList();

		for (Identifier id : tooltipPredicates) {
			tooltipPredicateList.add(NbtString.of(id.toString()));
		}
		tag.put("TooltipPredicates", tooltipPredicateList);
		tag.putString("DropRule", dropRule.toString());

		if (!this.itemIcon.isEmpty()) {
			tag.put("PolyPort$icon", ItemStack.OPTIONAL_CODEC, DynamicRegistryManager.of(Registries.REGISTRIES).getOps(NbtOps.INSTANCE), this.itemIcon);
		}
		data.put("SlotData", tag);
	}

	public static SlotType read(NbtCompound data) {
		NbtCompound slotData = data.getCompoundOrEmpty("SlotData");
		String group = slotData.getString("Group", "");
		String name = slotData.getString("Name", "");
		int order = slotData.getInt("Order", 0);
		int amount = slotData.getInt("Amount", 0);
		Identifier icon = Identifier.of(slotData.getString("Icon", ""));
		NbtList quickMoveList = slotData.getListOrEmpty("QuickMovePredicates");
		Set<Identifier> quickMovePredicates = new HashSet<>();

		for (NbtElement tag : quickMoveList) {
			if (tag instanceof NbtString string) {
				quickMovePredicates.add(Identifier.of(string.value()));
			}
		}
		NbtList validatorList = slotData.getListOrEmpty("ValidatorPredicates");
		Set<Identifier> validatorPredicates = new HashSet<>();

		for (NbtElement tag : validatorList) {
			if (tag instanceof NbtString string) {
				validatorPredicates.add(Identifier.of(string.value()));
			}
		}
		NbtList tooltipList = slotData.getListOrEmpty("TooltipPredicates");
		Set<Identifier> tooltipPredicates = new HashSet<>();

		for (NbtElement tag : tooltipList) {
			if (tag instanceof NbtString string) {
				tooltipPredicates.add(Identifier.of(string.value()));
			}
		}
		String dropRuleName = slotData.getString("DropRule", "");
		DropRule dropRule = DropRule.DEFAULT;

		if (TrinketEnums.DropRule.has(dropRuleName)) {
			dropRule = TrinketEnums.DropRule.valueOf(dropRuleName);
		}

		var itemIcon = slotData.get("PolyPort$icon", ItemStack.OPTIONAL_CODEC, DynamicRegistryManager.of(Registries.REGISTRIES).getOps(NbtOps.INSTANCE)).orElse(ItemStack.EMPTY);
		if (itemIcon == ItemStack.EMPTY) {
			itemIcon = Items.IRON_CHESTPLATE.getDefaultStack();
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
