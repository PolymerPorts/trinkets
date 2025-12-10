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
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
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
                        .setStyle(Style.EMPTY.withColor(Formatting.WHITE).withFont(new StyleSpriteSource.Font(Identifier.of(TrinketsMain.MOD_ID, "gui")))))
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
                this.setSlot(9 * x + 4, Elements.FILLER.get(false).hideTooltip());
            }
        }

        var hasPack = PolymerResourcePackUtils.hasMainPack(this.player);

        for (int i = 0; i < this.displayPerPage; i++) {
            var y = page * this.displayPerPage + i;
            if (y < this.inventories.size()) {
                drawLine(i, this.inventories.get(y), subPage[i]);
            } else {
                if (this.compact) {
                    int base = i / 2 * 9 + ((i % 2) * 5);
                    for (int x = 0; x < 4; x++) {
                        this.setSlot(base + x, Elements.FILLER.get(hasPack).hideTooltip());
                    }
                } else {
                    for (int x = 0; x < 9; x++) {
                        this.setSlot(i * 9 + x, Elements.FILLER.get(hasPack).hideTooltip());
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
        boolean hasPack = PolymerResourcePackUtils.hasMainPack(player);

        var base = this.compact ? index / 2 * 9 + ((index % 2) * 5) : index * 9;
        var invSize = this.compact ? 2 : 6;

        int slot = 0;
        var icon = GuiElementBuilder.from(type.getIconItem())
                .setName(type.getTranslation().formatted(Formatting.WHITE)).hideDefaultTooltip();
        if (hasPack) {
            icon.model(type.getIcon());
        }
        this.setSlot(base + slot++, icon);

        if (!this.compact) {
            this.setSlot(base + slot++, Elements.FILLER.get(hasPack).hideTooltip());
        }


        if (trinketInventory.size() <= invSize) {
            for (int i = 0; i < invSize; i++) {
                if (i < trinketInventory.size()) {
                    this.setSlotRedirect(base + slot++, new SurvivalTrinketSlot(trinketInventory, i, 0, 0, this.component.getGroups().get(type.getGroup()), type, 0, true));
                } else {
                    this.setSlot(base + slot++, Elements.FILLER.get(hasPack).hideTooltip());
                }
            }

            if (hasPack) {
                this.setSlot(base + slot++, ItemStack.EMPTY);
            } else {
                this.setSlot(base + slot++, Elements.FILLER.get(hasPack).hideTooltip());
            }
        } else {
            for (int i = 0; i < invSize; i++) {
                if (subPage * invSize + i < trinketInventory.size()) {
                    this.setSlotRedirect(base + slot++, new SurvivalTrinketSlot(trinketInventory, subPage * invSize + i, 0, 0, this.component.getGroups().get(type.getGroup()), type, 0, true));
                } else {
                    this.setSlot(base + slot++, Elements.FILLER.get(hasPack).hideTooltip());
                }
            }

            this.setSlot(base + slot++,
                    Elements.SUBPAGE.get(hasPack)
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
        boolean hasPack = PolymerResourcePackUtils.hasMainPack(this.player);
        var navabarFiller = Elements.FILLER_NAVBAR.get(hasPack).hideTooltip();

        if (this.inventories.size() > this.displayPerPage) {

            this.setSlot(5 * 9 + 0, navabarFiller);
            this.setSlot(5 * 9 + 1, navabarFiller);

            this.setSlot(5 * 9 + 2, Elements.PREVIOUS.get(hasPack)
                    .setName(Text.translatable("spectatorMenu.previous_page"))
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

            this.setSlot(5 * 9 + 3, navabarFiller);
            this.setSlot(5 * 9 + 4, navabarFiller);
            this.setSlot(5 * 9 + 5, navabarFiller);


            this.setSlot(5 * 9 + 6, Elements.NEXT.get(hasPack)
                    .setName(Text.translatable("spectatorMenu.next_page"))
                    .hideDefaultTooltip()
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


            this.setSlot(5 * 9 + 7, navabarFiller);
            this.setSlot(5 * 9 + 8, navabarFiller);
        } else {
            for (int i = 0; i < 9; i++) {
                this.setSlot(5 * 9 + i, navabarFiller);
            }
        }
    }


    public static void playClickSound(ServerPlayerEntity player) {
        player.networkHandler.sendPacket(new PlaySoundFromEntityS2CPacket(
                SoundEvents.UI_BUTTON_CLICK, SoundCategory.UI, player, 0.7f, 1, player.getRandom().nextLong()
        ));
    }
}
