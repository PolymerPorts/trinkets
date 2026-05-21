package dev.emi.trinkets.poly;

import dev.emi.trinkets.TrinketsMain;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class Elements {
    public static final Getter FILLER = new Getter(Items.WHITE_STAINED_GLASS_PANE, Identifier.fromNamespaceAndPath(TrinketsMain.MOD_ID, "gui/polybuttons/filler"));
    public static final Getter FILLER_NAVBAR =  new Getter(Items.BLACK_STAINED_GLASS_PANE, Identifier.withDefaultNamespace("air"));


    public static final Getter PREVIOUS = new Getter(Items.GREEN_STAINED_GLASS_PANE, Identifier.fromNamespaceAndPath(TrinketsMain.MOD_ID, "gui/polybuttons/previous"));
    public static final Getter NEXT = new Getter(Items.GREEN_STAINED_GLASS_PANE, Identifier.fromNamespaceAndPath(TrinketsMain.MOD_ID, "gui/polybuttons/next"));
    public static final Getter SUBPAGE = new Getter(Items.LIGHT_BLUE_STAINED_GLASS_PANE, Identifier.fromNamespaceAndPath(TrinketsMain.MOD_ID, "gui/polybuttons/subpage"));


    public record Getter(Item item, Identifier modelId) {

        public Getter {
            if (modelId != null) {
                GuiModels.createModel(modelId);
            }
        }

        public GuiElementBuilder get(boolean hasPack) {
            var b = new GuiElementBuilder(this.item);
            if (hasPack && modelId != null) {
                b.model(modelId);
            }
            return b;
        }
    }
}
