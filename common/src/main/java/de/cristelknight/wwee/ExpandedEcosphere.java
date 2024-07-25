package de.cristelknight.wwee;

import de.cristelknight.cristellib.ModLoadingUtil;
import de.cristelknight.cristellib.builtinpacks.BuiltInDataPacks;
import de.cristelknight.wwee.config.configs.EEConfig;
import de.cristelknight.wwee.config.configs.ReplaceBiomesConfig;
import de.cristelknight.wwee.utils.BiomeReplace;
import de.cristelknight.wwee.utils.Updater;
import de.cristelknight.wwee.utils.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ExpandedEcosphere {
    public static final String MODID = "expanded_ecosphere";
    public static final Logger LOGGER = LogManager.getLogger("Expanded Ecosphere");
    public static final String WWOOVersion = EEExpectPlatform.getVersionForMod(MODID);
    private static final Updater updater = new Updater(WWOOVersion);
    public static Mode currentMode = Util.getMode(EEConfig.DEFAULT.getConfig().mode());
    public static final String LINK_DC = "https://discord.com/invite/yJng7sC44x";
    public static final String LINK_MODRINTH = "https://modrinth.com/mod/expanded-ecosphere";
    public static final String LINK_CF = "https://www.curseforge.com/minecraft/mc-mods/expanded-ecosphere";
    public static final String backupVersionNumber = "1.21";
    public static final String minTerraBlenderVersion = "4.0.0.1";


    public static void init() {
        LOGGER.info("Loading Expanded Ecosphere");
        updater.checkForUpdates();

        ReplaceBiomesConfig config2 = ReplaceBiomesConfig.DEFAULT.getConfig();
        if(config2.enableBiomes() && currentMode.equals(Mode.DEFAULT)) BiomeReplace.replace();

        ResourceLocation.withDefaultNamespace("lol");

        BuiltInDataPacks.registerPack(EERL.create("resources/ee_default"), MODID, Component.literal("Expanded Ecosphere Default World Gen"), () -> currentMode.equals(Mode.DEFAULT));
        BuiltInDataPacks.registerPack(EERL.create("resources/ee_remove_blobs"), MODID, Component.literal("Disables granit, etc."), () -> EEConfig.DEFAULT.getConfig().removeOreBlobs());
        BuiltInDataPacks.registerPack(EERL.create("resources/ee_force_large_biomes"), MODID, Component.literal("Forcing LARGE biomes"), () -> EEConfig.DEFAULT.getConfig().forceLargeBiomes());
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
