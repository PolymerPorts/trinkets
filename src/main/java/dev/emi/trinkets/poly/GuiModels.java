package dev.emi.trinkets.poly;

import dev.emi.trinkets.TrinketsMain;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import eu.pb4.polymer.resourcepack.extras.api.format.item.ItemAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.BasicItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.ItemModel;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class GuiModels {
    private static final Set<Identifier> MODELS = new HashSet<>();
    private static boolean init = true;

    public static Identifier createModel(Identifier icon) {
        if (icon.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
            return icon;
        }

        if (init) {
            init = false;
            PolymerResourcePackUtils.addModAssets(TrinketsMain.MOD_ID);
            PolymerResourcePackUtils.RESOURCE_PACK_CREATION_EVENT.register(GuiModels::createFiles);
        }

        MODELS.add(icon);
        return icon;
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

            polymerRPBuilder.addData("assets/" + id.getNamespace() + "/items/" + id.getPath() + ".json",
                    new ItemAsset(new BasicItemModel(id), ItemAsset.Properties.DEFAULT));
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
