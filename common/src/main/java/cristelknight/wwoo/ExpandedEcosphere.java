package cristelknight.wwoo;

import blue.endless.jankson.api.SyntaxError;
import cristelknight.wwoo.config.configs.ReplaceBiomesConfig;
import cristelknight.wwoo.config.configs.EEConfig;
import cristelknight.wwoo.utils.BiomeReplace;
import cristelknight.wwoo.utils.Updater;
import cristelknight.wwoo.utils.Util;
import net.cristellib.CristelLibExpectPlatform;
import net.cristellib.ModLoadingUtil;
import net.cristellib.builtinpacks.BuiltInDataPacks;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


public class ExpandedEcosphere {
    public static final String MODID = "expanded_ecosphere";
    public static final Logger LOGGER = LogManager.getLogger("Expanded Ecosphere");
    public static final String WWOOVersion = EEExpectPlatform.getVersionForMod(MODID);
    private static final Updater updater = new Updater(WWOOVersion);
    public static Mode currentMode = Util.getMode(EEConfig.DEFAULT.getConfig().mode());
    public static final String LINK_DC = "https://discord.com/invite/yJng7sC44x";
    public static final String LINK_MODRINTH = "https://modrinth.com/mod/expanded-ecosphere";
    public static final String LINK_CF = "https://www.curseforge.com/minecraft/mc-mods/expanded-ecosphere";
    public static final String backupVersionNumber = "1.20.2";
    public static final String minTerraBlenderVersion = "3.0.0.170";


    public static void init() {
        LOGGER.info("Loading Expanded Ecosphere");
        updater.checkForUpdates();

        ReplaceBiomesConfig config2 = ReplaceBiomesConfig.DEFAULT.getConfig();
        if(config2.enableBiomes() && currentMode.equals(Mode.DEFAULT)) BiomeReplace.replace();

        BuiltInDataPacks.registerPack(new EERL("resources/ee_default"), MODID, Component.literal("Expanded Ecosphere Default World Gen"), () -> currentMode.equals(Mode.DEFAULT));
        BuiltInDataPacks.registerPack(new EERL("resources/ee_remove_blobs"), MODID, Component.literal("Disables granit, etc."), () -> EEConfig.DEFAULT.getConfig().removeOreBlobs());
        BuiltInDataPacks.registerPack(new EERL("resources/ee_force_large_biomes"), MODID, Component.literal("Forcing LARGE biomes"), () -> EEConfig.DEFAULT.getConfig().forceLargeBiomes() && currentMode.equals(Mode.DEFAULT));
    }

    public static Updater getUpdater(){
        return updater;
    }


    public static boolean isTerraBlenderLoaded(){
        return ModLoadingUtil.isModLoadedWithVersion("terrablender", minTerraBlenderVersion);
    }

    public enum Mode {
        COMPATIBLE,
        DEFAULT
    }
}
