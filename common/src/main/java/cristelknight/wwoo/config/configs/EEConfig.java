package cristelknight.wwoo.config.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cristelknight.wwoo.ExpandedEcosphere;
import cristelknight.wwoo.config.jankson.config.CommentedConfig;
import net.minecraft.Util;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;


public record EEConfig(String mode, boolean forceLargeBiomes, boolean removeOreBlobs, boolean showUpdates, boolean showBigUpdates, BlockState backGroundBlock)
        implements CommentedConfig<EEConfig> {

    private static EEConfig INSTANCE = null;

    public static final EEConfig DEFAULT = new EEConfig("DEFAULT",false,true, true, true, Blocks.CALCITE.defaultBlockState());

    public static final Codec<EEConfig> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    Codec.STRING.fieldOf("mode").orElse("DEFAULT").forGetter(config -> config.mode),
                    Codec.BOOL.fieldOf("forceLargeBiomes").orElse(false).forGetter(config -> config.forceLargeBiomes),
                    Codec.BOOL.fieldOf("removeOreBlobs").orElse(true).forGetter(config -> config.removeOreBlobs),
                    Codec.BOOL.fieldOf("showUpdates").orElse(true).forGetter(config -> config.showUpdates),
                    Codec.BOOL.fieldOf("showBigUpdates").orElse(true).forGetter(config -> config.showBigUpdates),
                    BlockState.CODEC.fieldOf("backGroundBlock").orElse(Blocks.CALCITE.defaultBlockState()).forGetter(config -> config.backGroundBlock)
            ).apply(builder, EEConfig::new)
    );

    @Override
    public String getSubPath() {
        return "expanded_ecosphere/config";
    }

    @Override
    public EEConfig getInstance() {
        return INSTANCE;
    }

    @Override
    public EEConfig getDefault() {
        return DEFAULT;
    }


    @Override
    public Codec<EEConfig> getCodec() {
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
               Expanded Ecosphere Main Config
               
               ===========
               Discord: %s
               Modrinth: %s
               CurseForge: %s""", ExpandedEcosphere.LINK_DC, ExpandedEcosphere.LINK_MODRINTH, ExpandedEcosphere.LINK_CF);
    }

    @Override
    public boolean isSorted() {
        return false;
    }

    @Override
    public void setInstance(EEConfig instance) {
        INSTANCE = instance;
    }
}
