package de.cristelknight.wwee.config.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cristelknight.wwee.config.jankson.config.CommentedConfig;
import net.minecraft.Util;

import java.util.HashMap;
import java.util.Map;


public record ReplaceBiomesConfig(boolean enableBiomes, Map<String, String> bannedBiomes)
        implements CommentedConfig<ReplaceBiomesConfig> {

    private static ReplaceBiomesConfig INSTANCE = null;

    public static final ReplaceBiomesConfig DEFAULT = new ReplaceBiomesConfig(false, Map.of());

    public static final Codec<ReplaceBiomesConfig> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    Codec.BOOL.fieldOf("enableBiomes").forGetter(c -> c.enableBiomes),
                    Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("replace").forGetter(c -> c.bannedBiomes)
            ).apply(builder, ReplaceBiomesConfig::new)
    );


    @Override
    public String getSubPath() {
        return "expanded_ecosphere/replace_biomes";
    }

    @Override
    public ReplaceBiomesConfig getInstance() {
        return INSTANCE;
    }

    @Override
    public ReplaceBiomesConfig getDefault() {
        return DEFAULT;
    }

    @Override
    public Codec<ReplaceBiomesConfig> getCodec() {
        return CODEC;
    }

    @Override
    public HashMap<String, String> getComments() {
        return Util.make(new HashMap<>(), map -> {
            map.put("enableBiomes", """
                Enable biome replacing""");
            map.put("replace", """
                Enter all Expanded Ecosphere biomes that should be replaced into this map.
                As the key put the biome you want to replace and as value the biome you want to replace it with.
                Here is an example:
                "replace": {
                    "minecraft:plains": "minecraft:basalt_deltas",
                    "wythers:bayou": "minecraft:river"
                }
                Only biomes which are added trough Expanded Ecosphere will work.
                Also on compatible mode you can only replace Expanded Ecosphere biomes and only replace them with non vanilla biomes.
                If a biome doesn't exist, the game will crash!""");
        });
    }

    @Override
    public String getHeader() {
        return """
               Expanded Ecosphere Replace Config
               
               ===========
               This config is only for replacing biomes, the main Expanded Ecosphere config is in the other file.""";
    }

    @Override
    public boolean isSorted() {
        return false;
    }

    @Override
    public void setInstance(ReplaceBiomesConfig instance) {
        INSTANCE = instance;
    }
}
