package de.cristelknight999.wwoo.config.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cristelknight999.wwoo.config.r.ConfigUtil;
import de.cristelknight999.wwoo.config.r.JanksonOps;
import net.minecraft.Util;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;


public record BannedBiomesConfig(List<String> bannedBiomes) {


    public static final Path CONFIG_PATH = ConfigUtil.CONFIG_WWOO.resolve("banned_biomes.json5");

    private static BannedBiomesConfig INSTANCE = null;

    private static final BannedBiomesConfig DEFAULT = new BannedBiomesConfig(List.of());

    public static final Codec<BannedBiomesConfig> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    Codec.list(Codec.STRING).fieldOf("banned_biomes").orElse(List.of()).forGetter(config -> config.bannedBiomes)
            ).apply(builder, BannedBiomesConfig::new)
    );

    public static BannedBiomesConfig getConfig() {
        return getConfig(false, false);
    }

    public static BannedBiomesConfig getConfig(boolean serialize, boolean recreate) {
        if (INSTANCE == null || serialize || recreate) {
            INSTANCE = readConfig(recreate);
        }

        return INSTANCE;
    }


    private static BannedBiomesConfig readConfig(boolean recreate) {
        final Path path = CONFIG_PATH;

        if (!path.toFile().exists() || recreate) {
            createConfig(path);
        }

        return ConfigUtil.readConfig(path, CODEC, JanksonOps.INSTANCE);
    }

    private static void createConfig(Path path) {
        HashMap<String, String> comments = Util.make(new HashMap<>(), map -> {
            map.put("banned_biomes", """
                Enter all WWOO biomes that should be deactivated here
                (only works when compatible mode is activated in wwoo.json5)
                Here is an example: ["andesite_crags", "bayou", "snowy_thermal_taiga"]""");
        }
        );
        ConfigUtil.createConfig(path, CODEC, comments, JanksonOps.INSTANCE, DEFAULT);
    }
}
