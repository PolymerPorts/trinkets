package dev.emi.trinkets.poly;

import dev.emi.trinkets.SurvivalTrinketSlot;
import dev.emi.trinkets.TrinketsMain;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
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

import java.util.*;
import java.util.stream.Collectors;

public class TrinketsFlatUI extends SimpleGui {


    private final List<TrinketInventory> inventories;
    private final TrinketComponent component;
    private final int displayPerPage;


    private int page = 0;
    private final int[] subPage;
    private final int[] cachedSize;
    private final TrinketInventory[] currentlyDisplayed;

    private final boolean compact;


    public TrinketsFlatUI(ServerPlayerEntity player, boolean compact) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);
        this.compact = compact;
        this.component = TrinketsApi.getTrinketComponent(player).get();

        this.displayPerPage = compact ? 10 : 5;

        this.subPage = new int[this.displayPerPage];
        this.cachedSize = new int[this.displayPerPage];
        this.currentlyDisplayed = new TrinketInventory[this.displayPerPage];

        this.inventories = this.component.getInventory().entrySet().stream()
                .sorted(Comparator.comparingInt(e -> this.component.getGroups().get(e.getKey()).getOrder()))
                .map(Map.Entry::getValue)
                .flatMap((x) -> x.values().stream().sorted(Comparator.comparingInt(a -> a.getSlotType().getOrder())))
                .collect(Collectors.toList());

        this.setTitle(PolymerResourcePackUtils.hasMainPack(player)
                ? Text.empty().append(Text.literal(compact ? "-1." : "-0.")
                        .setStyle(Style.EMPTY.withColor(Formatting.WHITE).withFont(new Identifier(TrinketsMain.MOD_ID, "gui"))))
                        .append(Text.translatable("trinkets.name"))
                : Text.translatable("trinkets.name")
        );
        this.drawLines();
        this.drawNavbar();

        this.open();
    }

    public static int open(ServerPlayerEntity player) {
        playClickSound(player);
        new TrinketsFlatUI(player, TrinketsPoly.getIsCompact(player));
        return 1;
    }

    public void drawLines() {
        if (this.compact && !PolymerResourcePackUtils.hasMainPack(this.player)) {
            for (int x = 0; x < 5; x++) {
                this.setSlot(9 * x + 4, Elements.FILLER);
            }
        }

        for (int i = 0; i < this.displayPerPage; i++) {
            var y = page * this.displayPerPage + i;
            if (y < this.inventories.size()) {
                drawLine(i, this.inventories.get(y), subPage[i]);
            } else {
                if (this.compact) {
                    int base = i / 2 * 9 + ((i % 2) * 5);
                    for (int x = 0; x < 4; x++) {
                        this.setSlot(base + x, Elements.FILLER);
                    }
                } else {
                    for (int x = 0; x < 9; x++) {
                        this.setSlot(i * 9 + x, Elements.FILLER);
                    }
                }
                this.cachedSize[i] = 0;
                this.currentlyDisplayed[i] = null;
            }
        }
    }

    @Override
    public void onTick() {
        for (int i = 0; i < this.displayPerPage; i++) {
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

        var base = this.compact ? index / 2 * 9 + ((index % 2) * 5) : index * 9;
        var invSize = this.compact ? 2 : 6;

        int slot = 0;
        this.setSlot(base + slot++, GuiElementBuilder.from(type.getIconItem()).setName(type.getTranslation().formatted(Formatting.WHITE)).hideDefaultTooltip());

        if (!this.compact) {
            this.setSlot(base + slot++, Elements.FILLER);
        }

        boolean hasPack = PolymerResourcePackUtils.hasMainPack(player);

        if (trinketInventory.size() <= invSize) {
            for (int i = 0; i < invSize; i++) {
                if (i < trinketInventory.size()) {
                    this.setSlotRedirect(base + slot++, new SurvivalTrinketSlot(trinketInventory, i, 0, 0, this.component.getGroups().get(type.getGroup()), type, 0, true));
                } else {
                    this.setSlot(base + slot++, Elements.FILLER);
                }
            }

            if (hasPack) {
                this.setSlot(base + slot++, ItemStack.EMPTY);
            } else {
                this.setSlot(base + slot++, Elements.FILLER);
            }
        } else {
            for (int i = 0; i < invSize; i++) {
                if (subPage * invSize + i < trinketInventory.size()) {
                    this.setSlotRedirect(base + slot++, new SurvivalTrinketSlot(trinketInventory, subPage * invSize + i, 0, 0, this.component.getGroups().get(type.getGroup()), type, 0, true));
                } else {
                    this.setSlot(base + slot++, Elements.FILLER);
                }
            }

            this.setSlot(base + slot++,
                    new GuiElementBuilder(Elements.SUBPAGE.item())
                            .setCustomModelData(Elements.SUBPAGE.value())
                            .setName(Text.empty()
                                    .append(Text.literal("« ").formatted(Formatting.GRAY))
                                    .append((this.subPage[index] + 1) + "/" + ((trinketInventory.size() - 1) / invSize + 1))
                                    .append(Text.literal(" »").formatted(Formatting.GRAY))
                            )
                            .setCallback((x, y, z) -> {
                        if (y.isLeft) {
                            this.subPage[index] = this.subPage[index] - 1;

                            if (this.subPage[index] < 0) {
                                this.subPage[index] = (trinketInventory.size() - 1) / invSize;
                            }
                            this.drawLine(index, trinketInventory, this.subPage[index]);

                            playClickSound(this.player);
                        } else if (y.isRight) {
                            this.subPage[index] = this.subPage[index] + 1;

                            if (this.subPage[index] > (trinketInventory.size() - 1) / invSize) {
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
        boolean addNavbarFiller = !PolymerResourcePackUtils.hasMainPack(this.player);

        if (this.inventories.size() > this.displayPerPage) {

            if (addNavbarFiller) {
                this.setSlot(5 * 9 + 0, Elements.FILLER_NAVBAR);
                this.setSlot(5 * 9 + 1, Elements.FILLER_NAVBAR);
            }

            this.setSlot(5 * 9 + 2, new GuiElementBuilder(Elements.PREVIOUS.item())
                    .setName(Text.translatable("createWorld.customize.custom.prev"))
                    .setCustomModelData(Elements.PREVIOUS.value())
                    .hideDefaultTooltip()
                    .setCallback((x, y, z) -> {
                        this.page -= 1;

                        if (this.page < 0) {
                            this.page = (this.inventories.size() - 1) / this.displayPerPage;
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
                    .hideDefaultTooltip()
                    .setCustomModelData(Elements.NEXT.value())
                    .setCallback((x, y, z) -> {
                        this.page += 1;

                        if (this.page > (this.inventories.size() - 1) / this.displayPerPage) {
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


    public static void playClickSound(ServerPlayerEntity player) {
        player.playSoundToPlayer(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 0.7f, 1);
    }
}
