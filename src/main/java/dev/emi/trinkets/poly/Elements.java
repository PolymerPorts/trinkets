package dev.emi.trinkets.poly;

import dev.emi.trinkets.TrinketsMain;
import eu.pb4.polymer.api.resourcepack.PolymerModelData;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Elements {
    public static final GuiElementBuilder FILLER = new GuiElementBuilder(Items.WHITE_STAINED_GLASS_PANE).setName(Text.empty())
            .setCustomModelData(GuiModels.getOrCreate(new Identifier(TrinketsMain.MOD_ID, "gui/filler"), Items.WHITE_STAINED_GLASS_PANE).value());
    public static final GuiElementBuilder FILLER_NAVBAR = new GuiElementBuilder(Items.BLACK_STAINED_GLASS_PANE).setName(Text.empty());


    public static final PolymerModelData PREVIOUS = GuiModels.getOrCreate(new Identifier(TrinketsMain.MOD_ID, "gui/previous"), Items.GREEN_STAINED_GLASS_PANE);
    public static final PolymerModelData NEXT = GuiModels.getOrCreate(new Identifier(TrinketsMain.MOD_ID, "gui/next"), Items.GREEN_STAINED_GLASS_PANE);
    public static final PolymerModelData SUBPAGE = GuiModels.getOrCreate(new Identifier(TrinketsMain.MOD_ID, "gui/subpage"), Items.LIGHT_BLUE_STAINED_GLASS_PANE);

}
