package dev.emi.trinkets.poly;

import dev.emi.trinkets.SurvivalTrinketSlot;
import dev.emi.trinkets.TrinketsMain;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import eu.pb4.polymer.api.resourcepack.PolymerRPUtils;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

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
        this.setTitle(PolymerRPUtils.hasPack(player)
                ? Text.empty().append(Text.literal("-0.")
                        .setStyle(Style.EMPTY.withColor(Formatting.WHITE).withFont(new Identifier(TrinketsMain.MOD_ID, "gui"))))
                        .append(Text.translatable("trinkets.name"))
                : Text.translatable("trinkets.name")
        );
        this.drawLines();
        this.drawNavbar();

        this.open();
    }

    public static int open(ServerPlayerEntity playerOrThrow) {
        playClickSound(playerOrThrow);
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
        this.setSlot(index * 9 + 0, GuiElementBuilder.from(type.getIconItem()).setName(type.getTranslation().formatted(Formatting.WHITE)).hideFlags());
        this.setSlot(index * 9 + 1, Elements.FILLER);

        boolean hasPack = PolymerRPUtils.hasPack(player);

        if (trinketInventory.size() < 7) {
            for (int i = 0; i < 6; i++) {
                if (i < trinketInventory.size()) {
                    this.setSlotRedirect(index * 9 + 2 + i, new SurvivalTrinketSlot(trinketInventory, i, 0, 0, this.component.getGroups().get(type.getGroup()), type, 0, true));
                } else {
                    this.setSlot(index * 9 + 2 + i, Elements.FILLER);
                }
            }

            if (hasPack) {
                this.setSlot(index * 9 + 8, ItemStack.EMPTY);
            } else {
                this.setSlot(index * 9 + 8, Elements.FILLER);
            }
        } else {
            for (int i = 0; i < 6; i++) {
                if (subPage * 6 + i < trinketInventory.size()) {
                    this.setSlotRedirect(index * 9 + 2 + i, new SurvivalTrinketSlot(trinketInventory, subPage * 6 + i, 0, 0, this.component.getGroups().get(type.getGroup()), type, 0, true));
                } else {
                    this.setSlot(index * 9 + 2 + i, Elements.FILLER);
                }
            }

            this.setSlot(index * 9 + 8,
                    new GuiElementBuilder(Elements.SUBPAGE.item())
                            .setCustomModelData(Elements.SUBPAGE.value())
                            .setName(Text.empty()
                                    .append(Text.literal("« ").formatted(Formatting.GRAY))
                                    .append((this.subPage[index] + 1) + "/" + ((trinketInventory.size() - 1) / 6 + 1))
                                    .append(Text.literal(" »").formatted(Formatting.GRAY))
                            )
                            .setCallback((x, y, z) -> {
                        if (y.isLeft) {
                            this.subPage[index] = this.subPage[index] - 1;

                            if (this.subPage[index] < 0) {
                                this.subPage[index] = (trinketInventory.size() - 1) / 6;
                            }
                            this.drawLine(index, trinketInventory, this.subPage[index]);

                            playClickSound(this.player);
                        } else if (y.isRight) {
                            this.subPage[index] = this.subPage[index] + 1;

                            if (this.subPage[index] > (trinketInventory.size() - 1) / 6) {
                                this.subPage[index] = 0;
                            }
                            this.drawLine(index, trinketInventory, this.subPage[index]);

                            playClickSound(this.player);
                        }
                    })
            );
        }
    }

    private void drawNavbar() {
        boolean addNavbarFiller = !PolymerRPUtils.hasPack(this.player);

        if (this.inventories.size() > 5) {

            if (addNavbarFiller) {
                this.setSlot(5 * 9 + 0, Elements.FILLER_NAVBAR);
                this.setSlot(5 * 9 + 1, Elements.FILLER_NAVBAR);
            }

            this.setSlot(5 * 9 + 2, new GuiElementBuilder(Elements.PREVIOUS.item())
                    .setName(Text.translatable("createWorld.customize.custom.prev"))
                    .setCustomModelData(Elements.PREVIOUS.value())
                    .setCallback((x, y, z) -> {
                        this.page -= 1;

                        if (this.page < 0) {
                            this.page = (this.inventories.size() - 1) / 5;
                        }

                        Arrays.fill(this.subPage, 0);
                        this.drawLines();
                        playClickSound(this.player);
                    })
            );
            if (addNavbarFiller) {
                this.setSlot(5 * 9 + 3, Elements.FILLER_NAVBAR);
                this.setSlot(5 * 9 + 4, Elements.FILLER_NAVBAR);
                this.setSlot(5 * 9 + 5, Elements.FILLER_NAVBAR);
            }

            this.setSlot(5 * 9 + 6, new GuiElementBuilder(Elements.NEXT.item())
                    .setName(Text.translatable("createWorld.customize.custom.next"))
                    .setCustomModelData(Elements.NEXT.value())
                    .setCallback((x, y, z) -> {
                        this.page += 1;

                        if (this.page > (this.inventories.size() - 1) / 5) {
                            this.page = 0;
                        }

                        Arrays.fill(this.subPage, 0);
                        this.drawLines();
                        playClickSound(this.player);
                    })
            );


            if (addNavbarFiller) {
                this.setSlot(5 * 9 + 7, Elements.FILLER_NAVBAR);
                this.setSlot(5 * 9 + 8, Elements.FILLER_NAVBAR);
            }
        } else if (addNavbarFiller) {
            for (int i = 0; i < 9; i++) {
                this.setSlot(5 * 9 + i, Elements.FILLER_NAVBAR);
            }
        }
    }


    public static final void playClickSound(ServerPlayerEntity player) {
        player.playSound(SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 0.7f, 1);
    }
}
