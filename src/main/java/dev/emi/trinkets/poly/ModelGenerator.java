package dev.emi.trinkets.poly;

import dev.emi.trinkets.TrinketsMain;
import eu.pb4.polymer.api.resourcepack.PolymerRPBuilder;
import eu.pb4.polymer.api.resourcepack.PolymerRPUtils;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class ModelGenerator {
    private static final Set<Identifier> MODELS = new HashSet<>();
    private static boolean init = true;

    public static void generate(Identifier icon) {
        MODELS.add(icon);

        if (init) {
            init = false;
            PolymerRPUtils.addAssetSource(TrinketsMain.MOD_ID);

            PolymerRPUtils.RESOURCE_PACK_CREATION_EVENT.register(ModelGenerator::createFiles);
        }
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
    }
}
