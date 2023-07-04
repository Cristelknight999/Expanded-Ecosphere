package de.cristelknight999.wwoo.utils;

import de.cristelknight999.wwoo.WWOO;
import de.cristelknight999.wwoo.config.configs.CommentedConfig;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;

import static de.cristelknight999.wwoo.WWOO.*;
import static de.cristelknight999.wwoo.WWOO.Mode.DEFAULT;

public class Util {


	public static ResourceLocation wwooRes(String name){
		return new ResourceLocation(MODID, name);
	}

	public static MutableComponent translatableText(String id) {
		return Component.translatable(MODID + ".config.text." + id);
	}




	public static void registerPacks(){
		ModContainer container = FabricLoader.getInstance().getModContainer(MODID).orElse(null);
		if(container != null){

			CommentedConfig config = CommentedConfig.getConfig();
			boolean b = currentMode.equals(WWOO.Mode.COMPATIBLE) || config.onlyVanillaBiomes();
			if(!b){
				ResourceManagerHelper.registerBuiltinResourcePack(new ResourceLocation(MODID, "wwoo_default"), container, ResourcePackActivationType.ALWAYS_ENABLED);
			}
			if(config.forceLargeBiomes()){
				ResourceManagerHelper.registerBuiltinResourcePack(new ResourceLocation(MODID, "wwoo_force_large_biomes"), container, ResourcePackActivationType.ALWAYS_ENABLED);
			}
		}
	}

	public static Path getResourceDirectory(String modid, String subPath) {
		ModContainer container = FabricLoader.getInstance().getModContainer(modid).orElse(null);
		if(container != null){
			Path path = container.findPath(subPath).orElse(null);
			if(path == null) WWOO.LOGGER.error("Path for subPath: " + subPath + " in modId: " + modid + " is null");
			return path;
		}
		WWOO.LOGGER.error("Mod container for modId:" + modid + " is null");
		return null;
	}

	public static Mode getMode(String mode){
		if(!isTerraBlenderLoaded()){
			return DEFAULT;
		}
		try {
			return Mode.valueOf(mode.toUpperCase());
		}
		catch (IllegalArgumentException e){
			LOGGER.warn("["+MODID+"] Invalid Mode '{}' for option '{}'", mode, "mode");
			return DEFAULT;
		}
	}
}
