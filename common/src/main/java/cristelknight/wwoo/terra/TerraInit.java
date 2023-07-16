package cristelknight.wwoo.terra;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import cristelknight.wwoo.WWOO;
import cristelknight.wwoo.WWOORL;
import cristelknight.wwoo.config.configs.BannedBiomesConfig;
import net.cristellib.CristelLibExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cristelknight.wwoo.WWOO.Mode.COMPATIBLE;


public class TerraInit {

    private static final String OVERWORLD = "resources/wwoo_default/data/minecraft/dimension/overworld.json";

    private static final String NOISE = "resources/wwoo_default/data/minecraft/worldgen/noise_settings/overworld.json";


    public static void onTerraBlenderInitialized() {

    }

    public static <T> T readConfig(JsonElement load, Codec<T> codec, DynamicOps<JsonElement> ops) {
        DataResult<Pair<T, JsonElement>> decode = codec.decode(ops, load);
        Optional<DataResult.PartialResult<Pair<T, JsonElement>>> error = decode.error();
        if (error.isPresent()) {
            throw new IllegalArgumentException("Couldn't read " + load.toString() + ", crashing instead.");
        }
        return decode.result().orElseThrow().getFirst();
    }



    public static List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> readParameterPoints() {
        InputStream im;
        try {
            Path path = CristelLibExpectPlatform.getResourceDirectory(WWOO.MODID, OVERWORLD);
            if(path == null) throw new RuntimeException();
            im = Files.newInputStream(path);
        } catch (IOException e) {
            WWOO.LOGGER.error("Couldn't read " + OVERWORLD + ", crashing instead");
            throw new RuntimeException(e);
        }

        try (InputStreamReader reader = new InputStreamReader(im)) {
            JsonElement el = JsonParser.parseReader(reader);
            if (!el.isJsonObject()) throw new RuntimeException("Input stream is on JsonElement");
            List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> list = new ArrayList<>();
            JsonObject o = el.getAsJsonObject();
            JsonArray jsonArray = o.get("generator").getAsJsonObject().get("biome_source").getAsJsonObject().get("biomes").getAsJsonArray();
            for(int i = 0; i < jsonArray.size(); i++){
                JsonObject e = jsonArray.get(i).getAsJsonObject();
                String b = e.get("biome").getAsString();
                if(b.contains("wythers:")){
                    BannedBiomesConfig config = BannedBiomesConfig.DEFAULT.getConfig();
                    if(config.bannedBiomes().contains(b.replace("wythers:", ""))) continue;
                }
                ResourceKey<Biome> r = ResourceKey.create(Registries.BIOME, new ResourceLocation(b));


                JsonObject jo = e.get("parameters").getAsJsonObject();

                Climate.ParameterPoint point = readConfig(jo, Climate.ParameterPoint.CODEC, JsonOps.INSTANCE);
                Pair<Climate.ParameterPoint, ResourceKey<Biome>> pair = new Pair<>(point, r);
                list.add(pair);
            }

            return list;
        } catch (FileNotFoundException e) {
            WWOO.LOGGER.error("Couldn't find " + OVERWORLD + ", crashing instead");
            throw new RuntimeException(e);
        } catch (IOException | JsonSyntaxException e) {
            WWOO.LOGGER.error("Couldn't parse " + OVERWORLD + ", crashing instead");
            throw new RuntimeException(e);
        }
    }




    public static SurfaceRules.RuleSource readSurfaceRulesFromNoise() {
        InputStream im;
        try {
            Path path = CristelLibExpectPlatform.getResourceDirectory(WWOO.MODID, NOISE);
            if(path == null) throw new RuntimeException();
            im = Files.newInputStream(path);
        } catch (IOException e) {
            WWOO.LOGGER.error("Couldn't read " + NOISE + ", crashing instead");
            throw new RuntimeException(e);
        }
        try(InputStreamReader reader = new InputStreamReader(im)) {
            JsonElement load = JsonParser.parseReader(reader);
            JsonElement element = load.getAsJsonObject().get("surface_rule");

            return readConfig(element, SurfaceRules.RuleSource.CODEC, JsonOps.INSTANCE);
        } catch (Exception errorMsg) {
            throw new IllegalArgumentException("Couldn't parse " + NOISE + ", crashing instead.");
        }
    }

    public static void readOverworldSurfaceRules() {
        SurfaceRules.RuleSource s = readSurfaceRulesFromNoise();
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, "wythers", s);
    }

    public static void terraEnableDisable(){
        if(WWOO.currentMode.equals(COMPATIBLE)){
            registerRegions();
            readOverworldSurfaceRules();
        }
        else {
            removeRegions();
            SurfaceRuleManager.removeSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, "wythers");
        }
    }

    public static void registerRegions(){
        Regions.register(new WWOORegion(new WWOORL("overworld"), 4));

    }

    private static void removeRegions(){
        Regions.remove(RegionType.OVERWORLD, new WWOORL("overworld"));
    }
}
