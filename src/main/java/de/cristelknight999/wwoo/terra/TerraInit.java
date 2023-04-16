package de.cristelknight999.wwoo.terra;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import de.cristelknight999.wwoo.WWOO;
import de.cristelknight999.wwoo.config.configs.BannedBiomesConfig;
import de.cristelknight999.wwoo.utils.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.TerraBlenderApi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.cristelknight999.wwoo.WWOO.Mode.COMPATIBLE;
import static de.cristelknight999.wwoo.WWOO.currentMode;
import static de.cristelknight999.wwoo.config.r.ConfigUtil.MODID;


public class TerraInit implements TerraBlenderApi {

    private static final String OVERWORLD = "wwoo_default/data/minecraft/dimension/overworld.json";
    @Override
    public void onTerraBlenderInitialized() {
        if(currentMode.equals(COMPATIBLE)){
            registerRegions();
            readOverworldSurfaceRules();
        }
    }

    public static <T> T readConfig(String pathInRes, Codec<T> codec, DynamicOps<JsonElement> ops) {
        InputStream im;
        try {
            Path path = Util.getResourceDirectory(WWOO.MODID, pathInRes);
            if(path == null) throw new RuntimeException();
            im = Files.newInputStream(path);
        } catch (IOException e) {
            WWOO.LOGGER.error("["+MODID+"] Couldn't read " + pathInRes + ", crashing instead");
            throw new RuntimeException(e);
        }
        try(InputStreamReader reader = new InputStreamReader(im)) {
            JsonElement load = JsonParser.parseReader(reader);

            DataResult<Pair<T, JsonElement>> decode = codec.decode(ops, load);
            Optional<DataResult.PartialResult<Pair<T, JsonElement>>> error = decode.error();
            if (error.isPresent()) {
                throw new IllegalArgumentException("["+MODID+"] Couldn't read " + pathInRes + ", crashing instead.");
            }
            return decode.result().orElseThrow().getFirst();
        } catch (Exception errorMsg) {
            throw new IllegalArgumentException("["+MODID+"] Couldn't read " + pathInRes + ", crashing instead.");
        }
    }


    public static List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> readParameterPoints() {
        InputStream im;
        try {
            Path path = Util.getResourceDirectory(WWOO.MODID, "resources/" + OVERWORLD);
            if(path == null) throw new RuntimeException();
            im = Files.newInputStream(path);
        } catch (IOException e) {
            WWOO.LOGGER.error("["+MODID+"] Couldn't read " + OVERWORLD + ", crashing instead");
            throw new RuntimeException(e);
        }

        try (InputStreamReader reader = new InputStreamReader(im)) {
            JsonElement el = JsonParser.parseReader(reader);
            if (!el.isJsonObject()) return null;
            List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> list = new ArrayList<>();
            JsonObject o = el.getAsJsonObject();
            JsonArray jsonArray = o.get("generator").getAsJsonObject().get("biome_source").getAsJsonObject().get("biomes").getAsJsonArray();
            for(int i = 0; i < jsonArray.size(); i++){
                JsonObject e = jsonArray.get(i).getAsJsonObject();
                String b = e.get("biome").getAsString();
                if(b.contains("wythers:")){
                    BannedBiomesConfig config = BannedBiomesConfig.getConfig();
                    if(config.bannedBiomes().contains(b.replace("wythers:", ""))) continue;
                }
                ResourceKey<Biome> r = ResourceKey.create(Registries.BIOME, new ResourceLocation(b));
                JsonObject jo = e.get("parameters").getAsJsonObject();
                JsonArray temperature = jo.get("temperature").getAsJsonArray();
                JsonArray humidity = jo.get("humidity").getAsJsonArray();
                JsonArray continentalness = jo.get("continentalness").getAsJsonArray();
                JsonArray erosion = jo.get("erosion").getAsJsonArray();
                JsonArray weirdness = jo.get("weirdness").getAsJsonArray();
                JsonArray depth = jo.get("depth").getAsJsonArray();
                Climate.ParameterPoint p = new Climate.ParameterPoint(Climate.Parameter.span(temperature.get(0).getAsFloat(), temperature.get(1).getAsFloat()), Climate.Parameter.span(humidity.get(0).getAsFloat(), humidity.get(1).getAsFloat()), Climate.Parameter.span(continentalness.get(0).getAsFloat(), continentalness.get(1).getAsFloat()), Climate.Parameter.span(erosion.get(0).getAsFloat(), erosion.get(0).getAsFloat()), Climate.Parameter.span(depth.get(0).getAsFloat(), depth.get(1).getAsFloat()), Climate.Parameter.span(weirdness.get(0).getAsFloat(), weirdness.get(1).getAsFloat()), 0);
                Pair<Climate.ParameterPoint, ResourceKey<Biome>> pair = new Pair<>(p, r);
                list.add(pair);
            }

            return list;
        } catch (FileNotFoundException e) {
            WWOO.LOGGER.error("["+MODID+"] Couldn't read " + OVERWORLD + ", crashing instead");
            throw new RuntimeException(e);
        } catch (IOException | JsonSyntaxException e) {
            WWOO.LOGGER.error("["+MODID+"] Couldn't read " + OVERWORLD + ", crashing instead");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public static void readOverworldSurfaceRules() {
        SurfaceRules.RuleSource s = readConfig("resources/surface_rules.json", SurfaceRules.RuleSource.CODEC, JsonOps.INSTANCE);
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, "wythers", s);
    }

    public static void terraEnableDisable(){
        if(currentMode.equals(COMPATIBLE)){
            registerRegions();
            readOverworldSurfaceRules();
        }
        else {
            removeRegions();
            SurfaceRuleManager.removeSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, "wythers");
        }
    }

    public static void registerRegions(){
        Regions.register(new WWOORegion(Util.wwooRes("overworld_1"), 4));
        Regions.register(new WWOORegion(Util.wwooRes("overworld_2"), 3));
        Regions.register(new WWOORegion(Util.wwooRes("overworld_3"), 3));

    }

    private static void removeRegions(){
        Regions.remove(RegionType.OVERWORLD, Util.wwooRes("overworld_1"));
        Regions.remove(RegionType.OVERWORLD, Util.wwooRes("overworld_2"));
        Regions.remove(RegionType.OVERWORLD, Util.wwooRes("overworld_3"));
    }
}
