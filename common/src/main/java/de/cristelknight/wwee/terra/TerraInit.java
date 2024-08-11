package de.cristelknight.wwee.terra;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import de.cristelknight.cristellib.CristelLibExpectPlatform;
import de.cristelknight.cristellib.util.TerrablenderUtil;
import de.cristelknight.wwee.EERL;
import de.cristelknight.wwee.ExpandedEcosphere;
import de.cristelknight.wwee.config.configs.ReplaceBiomesConfig;
import de.cristelknight.wwee.utils.BiomeReplace;
import de.cristelknight.wwee.utils.Util;
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



public class TerraInit {

    private static final String OVERWORLD = "resources/ee_default/data/minecraft/dimension/overworld.json";
    private static final String NOISE = "resources/ee_default/data/minecraft/worldgen/noise_settings/overworld.json";


    public static List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> readParameterPoints() {
        InputStream im;
        try {
            Path path = CristelLibExpectPlatform.getResourceDirectory(ExpandedEcosphere.MODID, OVERWORLD);
            if(path == null) throw new RuntimeException();
            im = Files.newInputStream(path);
        } catch (IOException e) {
            ExpandedEcosphere.LOGGER.error("Couldn't read " + OVERWORLD + ", crashing instead");
            throw new RuntimeException(e);
        }

        try (InputStreamReader reader = new InputStreamReader(im)) {
            JsonElement el = JsonParser.parseReader(reader);
            if (!el.isJsonObject()) throw new RuntimeException("Input stream is on JsonElement");
            List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> list = new ArrayList<>();
            JsonObject o = el.getAsJsonObject();

            if(ReplaceBiomesConfig.DEFAULT.getConfig().enableBiomes()){
                BiomeReplace.replaceObject(o, true);
            }

            JsonArray jsonArray = o.get("generator").getAsJsonObject().get("biome_source").getAsJsonObject().get("biomes").getAsJsonArray();
            for(int i = 0; i < jsonArray.size(); i++){
                JsonObject e = jsonArray.get(i).getAsJsonObject();
                String b = e.get("biome").getAsString();
                if(b.contains("minecraft:")) continue;

                JsonObject jo = e.get("parameters").getAsJsonObject();
                Climate.ParameterPoint point = Util.readConfig(jo, Climate.ParameterPoint.CODEC, JsonOps.INSTANCE);
                Pair<Climate.ParameterPoint, ResourceKey<Biome>> pair = new Pair<>(point, ResourceKey.create(Registries.BIOME, ResourceLocation.parse(b)));
                list.add(pair);
            }

            return list;
        } catch (FileNotFoundException e) {
            ExpandedEcosphere.LOGGER.error("Couldn't find " + OVERWORLD + ", crashing instead");
            throw new RuntimeException(e);
        } catch (IOException | JsonSyntaxException e) {
            ExpandedEcosphere.LOGGER.error("Couldn't parse " + OVERWORLD + ", crashing instead");
            throw new RuntimeException(e);
        }
    }




    public static SurfaceRules.RuleSource readSurfaceRulesFromNoise() {
        InputStream im;
        try {
            Path path = CristelLibExpectPlatform.getResourceDirectory(ExpandedEcosphere.MODID, NOISE);
            if(path == null) throw new RuntimeException();
            im = Files.newInputStream(path);
        } catch (IOException e) {
            ExpandedEcosphere.LOGGER.error("Couldn't read " + NOISE + ", crashing instead");
            throw new RuntimeException(e);
        }
        try(InputStreamReader reader = new InputStreamReader(im)) {
            JsonElement load = JsonParser.parseReader(reader);
            JsonElement element = load.getAsJsonObject().get("surface_rule");

            return Util.readConfig(element, SurfaceRules.RuleSource.CODEC, JsonOps.INSTANCE);
        } catch (Exception errorMsg) {
            throw new IllegalArgumentException("Couldn't parse " + NOISE + ", crashing instead.");
        }
    }



    public static void terraEnableDisable(){
        if(ExpandedEcosphere.currentMode.equals(ExpandedEcosphere.Mode.COMPATIBLE)){
            terraEnable();
        }
        else {
            TerrablenderUtil.setMixinEnabled(false);
            Regions.remove(RegionType.OVERWORLD, EERL.create("overworld"));
            SurfaceRuleManager.removeSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, "wythers");
        }
    }

    public static void terraEnable(){
        TerrablenderUtil.setMixinEnabled(true);
        registerRegions();
        readOverworldSurfaceRules();
    }
    private static void registerRegions(){
        Regions.register(new WWOORegion(EERL.create("overworld"), 10));
    }

    private static void readOverworldSurfaceRules() {
        SurfaceRules.RuleSource s = readSurfaceRulesFromNoise();
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, "wythers", s);
    }

}
