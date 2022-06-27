package dev.emi.trinkets.poly;

import dev.emi.trinkets.SurvivalTrinketSlot;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrinketsFlatUI extends SimpleGui {
    private final List<TrinketInventory> inventories;
    private final TrinketComponent component;


    private int page = 0;
    private final int[] subPage = new int[5];
    private final int[] cachedSize = new int[5];
    private final TrinketInventory[] currentlyDisplayed = new TrinketInventory[5];


    public TrinketsFlatUI(ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);
        this.component = TrinketsApi.getTrinketComponent(player).get();

        this.inventories = new ArrayList<>();
        this.component.getInventory().values().stream().flatMap((x) -> x.values().stream()).forEachOrdered((x) -> this.inventories.add(0, x));
        this.setTitle(Text.translatable("trinkets.name"));
        this.drawLines();
        this.drawNavbar();

        this.open();
    }

    public static int open(ServerPlayerEntity playerOrThrow) {
        new TrinketsFlatUI(playerOrThrow);
        return 1;
    }

    public void drawLines() {
        for (int i = 0; i < 5; i++) {
            var y = page * 5 + i;
            if (y < this.inventories.size()) {
                drawLine(i, this.inventories.get(y), subPage[i]);
            } else {
                for (int x = 0; x < 9; x++) {
                    this.setSlot(i * 9 + x, Elements.FILLER);
                }
                this.cachedSize[i] = 0;
                this.currentlyDisplayed[i] = null;
            }
        }
    }

    @Override
    public void onTick() {
        for (int i = 0; i < 5; i++) {
            if (this.currentlyDisplayed[i] != null && this.currentlyDisplayed[i].size() != this.cachedSize[i]) {
                this.drawLine(i, this.currentlyDisplayed[i], this.subPage[i]);
            }
        }

        super.onTick();
    }

    private void drawLine(int index, TrinketInventory trinketInventory, int subPage) {
        var type = trinketInventory.getSlotType();
        this.cachedSize[index] = trinketInventory.size();
        this.currentlyDisplayed[index] = trinketInventory;
        this.setSlot(index * 9 + 0, GuiElementBuilder.from(type.getIconItem()).setName(type.getTranslation()));
        this.setSlot(index * 9 + 1, Elements.FILLER);

        if (trinketInventory.size() < 7) {
            for (int i = 0; i < 6; i++) {
                if (i < trinketInventory.size()) {
                    this.setSlotRedirect(index * 9 + 2 + i, new SurvivalTrinketSlot(trinketInventory, i, 0, 0, this.component.getGroups().get(type.getGroup()), type, 0, true));
                } else {
                    this.setSlot(index * 9 + 2 + i, Elements.FILLER);
                }
            }

            this.setSlot(index * 9 + 8, Elements.FILLER);
        } else {
            for (int i = 0; i < 6; i++) {
                if (subPage * 6 + i < trinketInventory.size()) {
                    this.setSlotRedirect(index * 9 + 2 + i, new SurvivalTrinketSlot(trinketInventory, subPage * 6 + i, 0, 0, this.component.getGroups().get(type.getGroup()), type, 0, true));
                } else {
                    this.setSlot(index * 9 + 2 + i, Elements.FILLER);
                }
            }

            this.setSlot(index * 9 + 8,
                    new GuiElementBuilder(Items.LIGHT_BLUE_STAINED_GLASS_PANE).setName(Text.literal((this.subPage[index] + 1) + "/" + ((trinketInventory.size() - 1) / 6 + 1))).setCallback((x, y, z) -> {
                        if (y.isLeft) {
                            this.subPage[index] = this.subPage[index] - 1;

                            if (this.subPage[index] < 0) {
                                this.subPage[index] = (trinketInventory.size() - 1) / 6;
                            }
                            this.drawLine(index, trinketInventory, this.subPage[index]);
                        } else if (y.isRight) {
                            this.subPage[index] = this.subPage[index] + 1;

                            if (this.subPage[index] > (trinketInventory.size() - 1) / 6) {
                                this.subPage[index] = 0;
                            }
                            this.drawLine(index, trinketInventory, this.subPage[index]);

                        }
                    })
            );
        }
    }

    private void drawNavbar() {
        this.setSlot(5 * 9 + 0, Elements.FILLER_NAVBAR);
        this.setSlot(5 * 9 + 1, Elements.FILLER_NAVBAR);

        this.setSlot(5 * 9 + 2, new GuiElementBuilder(Items.GREEN_STAINED_GLASS_PANE)
                .setName(Text.translatable("createWorld.customize.custom.prev"))
                .setCallback((x, y, z) -> {
                    this.page -= 1;

                    if (this.page < 0) {
                        this.page = (this.inventories.size() - 1) / 5;
                    }

                    Arrays.fill(this.subPage, 0);
                    this.drawLines();
                })
        );
        this.setSlot(5 * 9 + 3, Elements.FILLER_NAVBAR);
        this.setSlot(5 * 9 + 4, Elements.FILLER_NAVBAR);
        this.setSlot(5 * 9 + 5, Elements.FILLER_NAVBAR);

        this.setSlot(5 * 9 + 6, new GuiElementBuilder(Items.GREEN_STAINED_GLASS_PANE)
                .setName(Text.translatable("createWorld.customize.custom.next"))
                .setCallback((x, y, z) -> {
                    this.page += 1;

                    if (this.page > (this.inventories.size() - 1) / 5) {
                        this.page = 0;
                    }

                    Arrays.fill(this.subPage, 0);
                    this.drawLines();
                })
        );

        this.setSlot(5 * 9 + 7, Elements.FILLER_NAVBAR);
        this.setSlot(5 * 9 + 8, Elements.FILLER_NAVBAR);

    }
}
