package de.cristelknight999.wwoo.config.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cristelknight999.wwoo.config.r.ConfigUtil;
import de.cristelknight999.wwoo.config.r.JanksonOps;
import net.minecraft.Util;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.nio.file.Path;
import java.util.HashMap;


public record CommentedConfig(String mode, boolean onlyVanillaBiomes, boolean forceLargeBiomes, boolean showUpdates, boolean showBigUpdates, BlockState backGroundBlock) {

    public static final Path CONFIG_PATH = ConfigUtil.CONFIG_WWOO.resolve("wwoo.json5");

    private static CommentedConfig INSTANCE = null;

    public static final CommentedConfig DEFAULT = new CommentedConfig("DEFAULT", false, false,true, true, Blocks.CALCITE.defaultBlockState());

    public static final Codec<CommentedConfig> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    Codec.STRING.fieldOf("mode").orElse("DEFAULT").forGetter(config -> config.mode),
                    Codec.BOOL.fieldOf("onlyVanillaBiomes").orElse(false).forGetter(config -> config.onlyVanillaBiomes),
                    Codec.BOOL.fieldOf("forceLargeBiomes").orElse(false).forGetter(config -> config.forceLargeBiomes),
                    Codec.BOOL.fieldOf("showUpdates").orElse(true).forGetter(config -> config.showUpdates),
                    Codec.BOOL.fieldOf("showBigUpdates").orElse(true).forGetter(config -> config.showBigUpdates),
                    BlockState.CODEC.fieldOf("backGroundBlock").orElse(Blocks.CALCITE.defaultBlockState()).forGetter(config -> config.backGroundBlock)
            ).apply(builder, CommentedConfig::new)
    );

    public static CommentedConfig getConfig() {
        return getConfig(false, false);
    }

    public static CommentedConfig getConfig(boolean serialize, boolean recreate) {
        if (INSTANCE == null || serialize || recreate) {
            final Path path = CONFIG_PATH;
            if (!path.toFile().exists() || recreate) {
                createConfig(path);
            }
            INSTANCE = ConfigUtil.readConfig(path, CODEC, JanksonOps.INSTANCE);
        }
        return INSTANCE;
    }

    public static void setINSTANCE(CommentedConfig config){
        INSTANCE = config;
    }


    private static void createConfig(Path path) {
        HashMap<String, String> comments = Util.make(new HashMap<>(), map -> {
                    map.put("mode", """
                    Type DEFAULT or COMPATIBLE, for changing modes.
                    COMPATIBLE allows compat with Terralith and disabling biomes in banned_biomes.json5,
                    but it requires Terrablender and is a bit unstable.""");
                    map.put("onlyVanillaBiomes", """        
                    Whether to only change Vanilla biomes or not (DEFAULT mode only).""");
                    map.put("showUpdates", """
                    Whether updates should be announced in chat when entering a world""");
                    map.put("forceLargeBiomes", """        
                    Whether to force Minecraft to generate Large Biomes or not.
                    This option forces Minecraft to generate Large Biomes, this setting was introduced,
                    because the default presets do not work (correctly).
                    This option will probably not work with Compatible mode, or other biome mods.""");
                    map.put("showBigUpdates", """
                   Whether to announce important updates even if the above setting is disabled.""");
                    map.put("backGroundBlock", """
                   Selects the background block in the config screen. Only makes sense in-game.""");
                }
        );
        if(INSTANCE == null){
            INSTANCE = DEFAULT;
        }
        ConfigUtil.createConfig(path, CODEC, comments, JanksonOps.INSTANCE, INSTANCE);
    }
}
