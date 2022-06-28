package dev.emi.trinkets.poly;

import dev.emi.trinkets.TrinketsMain;
import eu.pb4.polymer.api.resourcepack.PolymerModelData;
import eu.pb4.polymer.api.resourcepack.PolymerRPBuilder;
import eu.pb4.polymer.api.resourcepack.PolymerRPUtils;
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
            PolymerRPUtils.addAssetSource(TrinketsMain.MOD_ID);

            PolymerRPUtils.RESOURCE_PACK_CREATION_EVENT.register(GuiModels::createFiles);
        }

        MODELS.add(icon);
        return MODEL_MAP.computeIfAbsent(item, i -> new HashMap<>()).computeIfAbsent(icon, i -> PolymerRPUtils.requestModel(item, icon));
    }

    private static void createFiles(PolymerRPBuilder polymerRPBuilder) {
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
                "layer0": "trinkets:gui/filler"
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
            polymerRPBuilder.addData("assets/trinkets/models/gui/filler.json", json.getBytes(StandardCharsets.UTF_8));
        }
    }
}
