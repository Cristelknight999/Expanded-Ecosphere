package de.cristelknight999.wwoo;

import de.cristelknight999.wwoo.config.configs.CommentedConfig;
import de.cristelknight999.wwoo.config.r.ConfigUtil;
import de.cristelknight999.wwoo.utils.Updater;
import de.cristelknight999.wwoo.utils.Util;
import de.cristelknight999.wwoo.utils.biomeconvert.Config;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static de.cristelknight999.wwoo.WWOO.Mode.COMPATIBLE;
import static de.cristelknight999.wwoo.WWOO.Mode.DEFAULT;

public class WWOO implements ModInitializer {

    public static final String MODID = "wwoo";
    public static final Logger LOGGER = LogManager.getLogger(ConfigUtil.MODID);
    public static final Version WWOOVersion = FabricLoader.getInstance().getModContainer(MODID).get().getMetadata().getVersion();
    private static final Updater updater = new Updater(WWOOVersion);
    public static Mode currentMode = Util.getMode(CommentedConfig.getConfig().mode());
    public static final Component selectedD = Component.translatable("%1$s %2$s", Component.literal(DEFAULT.toString()).withStyle(ChatFormatting.GREEN), Util.translatableText("s").withStyle(ChatFormatting.DARK_RED));
    public static final Component selectedC = Component.translatable("%1$s %2$s", Component.literal(COMPATIBLE.toString()).withStyle(ChatFormatting.GREEN), Util.translatableText("s").withStyle(ChatFormatting.DARK_RED));
    public static final String LINK_H = "https://www.curseforge.com/minecraft/mc-mods/william-wythers-overhauled-overworld";
    public static final String LINK_DC = "https://discord.com/invite/yJng7sC44x";

    public static final String backupVersionNumber = "1.19.4";

    public static final String minTerraBlenderVersion = "2.2.0.154";


    @Override
    public void onInitialize() {
        LOGGER.info("Loading William Wythers' Overhauled Overworld");
        updater.checkForUpdates();
        //Config.init();
    }

    public static Updater getUpdater(){
        return updater;
    }


    public static boolean isTerraBlenderLoaded(){
        if(FabricLoader.getInstance().isModLoaded("terrablender")){
            Version version = FabricLoader.getInstance().getModContainer("terrablender").get().getMetadata().getVersion();
            Version min;
            try {
                min = Version.parse(minTerraBlenderVersion);
            } catch (VersionParsingException e) {
                throw new RuntimeException(e);
            }
            return version.compareTo(min) >= 0;
        }
        return false;
    }

    public enum Mode {
        COMPATIBLE,
        DEFAULT
    }
}
