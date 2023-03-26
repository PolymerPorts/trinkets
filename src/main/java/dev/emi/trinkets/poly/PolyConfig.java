package dev.emi.trinkets.poly;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import dev.emi.trinkets.TrinketsMain;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class PolyConfig {
    private final static Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("trinkets_polymer.json");

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping().setLenient().setPrettyPrinting()
            .create();

    @SerializedName("use_compact_ui_as_default")
    public boolean compactUi = true;

    @SerializedName("enable_helmet_slot_opening")
    public boolean helmetSlot = true;
    @SerializedName("enable_chestplate_slot_opening")
    public boolean chestplateSlot = true;
    @SerializedName("enable_leggings_slot_opening")
    public boolean leggingsSlot = true;
    @SerializedName("enable_boots_slot_opening")
    public boolean bootsSlot = true;


    public static PolyConfig loadOrCreateConfig() {
        try {
            PolyConfig config;

            if (Files.exists(CONFIG_PATH)) {
                config = GSON.fromJson(Files.readString(CONFIG_PATH), PolyConfig.class);
            } else {
                config = new PolyConfig();
            }

            saveConfig(config);
            return config;
        } catch (IOException exception) {
            TrinketsMain.LOGGER.error("Something went wrong while reading config!");
            exception.printStackTrace();
            return new PolyConfig();
        }
    }

    public static void saveConfig(PolyConfig config) {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(config), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            TrinketsMain.LOGGER.error("Something went wrong while saving config!");
            e.printStackTrace();
        }
    }
}
