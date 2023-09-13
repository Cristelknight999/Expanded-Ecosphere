package cristelknight.wwoo.config.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cristelknight.wwoo.WWOO;
import cristelknight.wwoo.config.jankson.config.CommentedConfig;
import net.minecraft.Util;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;


public record WWOOConfig(String mode, boolean onlyVanillaBiomes, boolean forceLargeBiomes, boolean removeOreBlobs, boolean showUpdates, boolean showBigUpdates, BlockState backGroundBlock)
        implements CommentedConfig<WWOOConfig> {

    private static WWOOConfig INSTANCE = null;

    public static final WWOOConfig DEFAULT = new WWOOConfig("DEFAULT", false, false,true, true, true, Blocks.CALCITE.defaultBlockState());

    public static final Codec<WWOOConfig> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    Codec.STRING.fieldOf("mode").orElse("DEFAULT").forGetter(config -> config.mode),
                    Codec.BOOL.fieldOf("onlyVanillaBiomes").orElse(false).forGetter(config -> config.onlyVanillaBiomes),
                    Codec.BOOL.fieldOf("forceLargeBiomes").orElse(false).forGetter(config -> config.forceLargeBiomes),
                    Codec.BOOL.fieldOf("removeOreBlobs").orElse(true).forGetter(config -> config.removeOreBlobs),
                    Codec.BOOL.fieldOf("showUpdates").orElse(true).forGetter(config -> config.showUpdates),
                    Codec.BOOL.fieldOf("showBigUpdates").orElse(true).forGetter(config -> config.showBigUpdates),
                    BlockState.CODEC.fieldOf("backGroundBlock").orElse(Blocks.CALCITE.defaultBlockState()).forGetter(config -> config.backGroundBlock)
            ).apply(builder, WWOOConfig::new)
    );

    @Override
    public String getSubPath() {
        return "wwoo/wwoo";
    }

    @Override
    public WWOOConfig getInstance() {
        return INSTANCE;
    }

    @Override
    public WWOOConfig getDefault() {
        return DEFAULT;
    }


    @Override
    public Codec<WWOOConfig> getCodec() {
        return CODEC;
    }

    @Override
    public @NotNull HashMap<String, String> getComments() {
        return Util.make(new HashMap<>(), map -> {
            map.put("removeOreBlobs", """
                    Removes underground ores (andesite, diorite, granite, gravel and dirt)""");
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
        });
    }

    @Override
    public @Nullable String getHeader() {
        return String.format("""
               WWOO Main Config
               
               ===========
               Discord: %s
               Modrinth: %s
               CurseForge: %s""", WWOO.LINK_DC, WWOO.LINK_MODRINTH, WWOO.LINK_CF);
    }

    @Override
    public boolean isSorted() {
        return false;
    }

    @Override
    public void setInstance(WWOOConfig instance) {
        INSTANCE = instance;
    }
}
