package dev.emi.trinkets.poly;

import dev.emi.trinkets.TrinketsMain;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GuiModels {
    private static final Set<Identifier> MODELS = new HashSet<>();
    private static final Map<Item, Map<Identifier, PolymerModelData>> MODEL_MAP = new HashMap<>();
    private static boolean init = true;

    public static PolymerModelData getOrCreate(Identifier icon, Item item) {
        if (init) {
            init = false;
            PolymerResourcePackUtils.addModAssets(TrinketsMain.MOD_ID);

            PolymerResourcePackUtils.RESOURCE_PACK_CREATION_EVENT.register(GuiModels::createFiles);
        }

        MODELS.add(icon);
        return MODEL_MAP.computeIfAbsent(item, i -> new HashMap<>()).computeIfAbsent(icon, i -> PolymerResourcePackUtils.requestModel(item, icon));
    }

    private static void createFiles(ResourcePackBuilder polymerRPBuilder) {
        for (var id : MODELS) {
            var json = """
                    {
                      "parent": "minecraft:item/handheld",
                      "textures": {
                        "layer0": "{ID}"
                      }
                    }
                    """.replace("{ID}", id.toString());


            polymerRPBuilder.addData("assets/" + id.getNamespace() + "/models/" + id.getPath() + ".json", json.getBytes(StandardCharsets.UTF_8));
        }
        {
            var json = """
              {
              "parent": "minecraft:item/handheld",
              "textures": {
                "layer0": "trinkets:gui/polybuttons/filler"
              },
              "display": {
                "gui": {
                    "rotation": [ 0, 0, 0 ],
                    "translation": [ 0, 0, 0 ],
                    "scale": [ 1.12, 1.12, 1.12 ]
                }
              }
            }
            """;
            polymerRPBuilder.addData("assets/trinkets/models/gui/polybuttons/filler.json", json.getBytes(StandardCharsets.UTF_8));
        }
    }
}
