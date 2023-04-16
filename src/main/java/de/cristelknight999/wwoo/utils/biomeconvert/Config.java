package de.cristelknight999.wwoo.utils.biomeconvert;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import de.cristelknight999.wwoo.WWOO;
import net.cristellib.CristelLib;
import net.cristellib.config.ConfigUtil;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static net.cristellib.config.ConfigUtil.*;

public class Config {

    public static Map<String, BiomeConfig> configs = new HashMap<>();

    public static Path BIOME_CONFIG = CONFIG_DIR.resolve("wwoo/biome_config.json5");

    public static void init(){
        createFromDefault("", BIOME_CONFIG, false, new HashMap<>());
        readConfig(BIOME_CONFIG);
        applyConfigs();
    }

    public static void readConfig(Path path){
        try {
            JsonObject load = JANKSON.load(path.toFile());
            if(load.get("wythers") instanceof JsonObject wythers){
                for(Map.Entry<String, JsonElement> e : wythers.entrySet()){
                    if(e.getValue() instanceof JsonObject object){
                        BiomeConfig config = JANKSON.fromJson(object, BiomeConfig.class);
                        configs.put("wythers:" + e.getKey(), config);
                    }
                }
            }
            else {
                WWOO.LOGGER.error("Couldn't read wythers config biomes");
            }
            if(load.get("minecraft") instanceof JsonObject minecraft){
                for(Map.Entry<String, JsonElement> e : minecraft.entrySet()){
                    if(e.getValue() instanceof JsonObject object){
                        BiomeConfig config = JANKSON.fromJson(object, BiomeConfig.class);
                        configs.put("minecraft:" + e.getKey(), config);
                    }
                }
            }
            else {
                WWOO.LOGGER.error("Couldn't read minecraft config biomes");
            }
        } catch (IOException | SyntaxError e) {
            throw new RuntimeException(e);
        }
    }

    public static void applyConfigs(){
        Map<String, BiomeConfig> map = getDefaultBiomeConfig();
        if(map == null){
            WWOO.LOGGER.error("Couldn't read default config");
            return;
        }

        for(String biomeName : configs.keySet()){
            BiomeConfig config = configs.get(biomeName);
            if(config == null || config.equals(map.get(biomeName))) continue;
            String[] biomeSplit = biomeName.split(":");
            com.google.gson.@Nullable JsonElement element = ConfigUtil.getElement(WWOO.MODID, "data/" + biomeSplit[0] + "/worldgen/biome/" + biomeSplit[1] + ".json");
            if(element instanceof com.google.gson.JsonObject object) {
                com.google.gson.JsonObject effect = object.get("effects").getAsJsonObject();
                effect.addProperty("sky_color", config.skyColor);
                effect.addProperty("water_color", config.waterColor);
                effect.addProperty("water_fog_color", config.waterFogColor);

                JsonArray features = object.get("features").getAsJsonArray();
                for(com.google.gson.JsonElement array : features){
                    if(array instanceof JsonArray a){
                        for(String feature : config.features){
                            JsonPrimitive primitive = new JsonPrimitive(feature);
                            if(a.contains(primitive)){
                                a.remove(primitive);
                            }
                        }
                    }
                }
                CristelLib.DATA_PACK.addBiome(new ResourceLocation(biomeSplit[0], biomeSplit[1]), object);
            }
        }
    }

    public static @Nullable Map<String, BiomeConfig> getDefaultBiomeConfig(){
        com.google.gson.@Nullable JsonElement element = ConfigUtil.getElement(WWOO.MODID, "resources/default_biome_config.json");

        if(element instanceof com.google.gson.JsonObject o){
            Map<String, BiomeConfig> configMap = new HashMap<>();

            com.google.gson.JsonObject biomes = o.get("biomes").getAsJsonObject();
            JsonArray colors = o.get("all_colors").getAsJsonArray();

            for(String biome : biomes.asMap().keySet()){
                com.google.gson.JsonObject biomeOld = biomes.get(biome).getAsJsonObject();
                BiomeConfig biomeO = new BiomeConfig();

                biomeO.skyColor = colors.get(biomeOld.get("sC").getAsInt()).getAsInt();
                biomeO.waterColor = colors.get(biomeOld.get("wC").getAsInt()).getAsInt();
                biomeO.waterFogColor = colors.get(biomeOld.get("wFC").getAsInt()).getAsInt();
                biomeO.features = new ArrayList<>();
                if(biome.contains("minecraft:")) configMap.put(biome, biomeO);
                else configMap.put("wythers:" + biome, biomeO);


            }
            return configMap;
        }
        return null;
    }

    public static void createFromConfig(){
        JsonObject object = new JsonObject();
        for(String biomeName : configs.keySet()){
            BiomeConfig config = configs.get(biomeName);
        }
    }

    public static void createFromDefault(String header, Path path, boolean serialize, Map<String, String> comments) {
        if(!serialize && path.toFile().exists()) return;

        Map<String, BiomeConfig> map = getDefaultBiomeConfig();
        if(map == null){
            WWOO.LOGGER.error("Couldn't create biome config from default config");
            return;
        }

        JsonObject object = new JsonObject();
        JsonObject minecraft = new JsonObject();
        JsonObject wythers = new JsonObject();

        for(String identifier : map.keySet()){
            BiomeConfig config = map.get(identifier);
            JsonElement element = JANKSON.toJson(config);
            String biomeName = identifier.split(":")[1];
            if(identifier.contains("minecraft:")) minecraft.put(biomeName, element);
            else wythers.put(biomeName, element);
        }
        object.put("wythers", wythers);
        object.put("minecraft", minecraft);

        addComments(comments, object, "");

        try {
            Files.createDirectories(path.getParent());
            String output = header + "\n" + object.toJson(JSON_GRAMMAR);
            Files.write(path, output.getBytes());
        } catch (IOException e) {
            WWOO.LOGGER.error(e.toString());
        }

    }



}
