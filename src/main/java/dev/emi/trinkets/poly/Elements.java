package dev.emi.trinkets.poly;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class Elements {
    public static final GuiElementBuilder FILLER = new GuiElementBuilder(Items.WHITE_STAINED_GLASS_PANE).setName(Text.empty());
    public static final GuiElementBuilder FILLER_NAVBAR = new GuiElementBuilder(Items.BLACK_STAINED_GLASS_PANE).setName(Text.empty());
}
