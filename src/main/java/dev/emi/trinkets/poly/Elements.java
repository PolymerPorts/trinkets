package dev.emi.trinkets.poly;

import dev.emi.trinkets.TrinketsMain;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class Elements {
    public static final Getter FILLER = new Getter(Items.WHITE_STAINED_GLASS_PANE, Identifier.of(TrinketsMain.MOD_ID, "gui/polybuttons/filler"));
    public static final Getter FILLER_NAVBAR =  new Getter(Items.BLACK_STAINED_GLASS_PANE, Identifier.ofVanilla("air"));


    public static final Getter PREVIOUS = new Getter(Items.GREEN_STAINED_GLASS_PANE, Identifier.of(TrinketsMain.MOD_ID, "gui/polybuttons/previous"));
    public static final Getter NEXT = new Getter(Items.GREEN_STAINED_GLASS_PANE, Identifier.of(TrinketsMain.MOD_ID, "gui/polybuttons/next"));
    public static final Getter SUBPAGE = new Getter(Items.LIGHT_BLUE_STAINED_GLASS_PANE, Identifier.of(TrinketsMain.MOD_ID, "gui/polybuttons/subpage"));


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
