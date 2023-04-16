package de.cristelknight999.wwoo.utils;

import de.cristelknight999.wwoo.WWOO;
import de.cristelknight999.wwoo.config.configs.CommentedConfig;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.resource.loader.BuiltinModResourcePackSource;
import net.fabricmc.fabric.impl.resource.loader.ModNioResourcePack;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.util.Tuple;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static de.cristelknight999.wwoo.WWOO.*;
import static de.cristelknight999.wwoo.WWOO.Mode.DEFAULT;

public class Util {


	public static ResourceLocation wwooRes(String name){
		return new ResourceLocation(MODID, name);
	}

	public static MutableComponent translatableText(String id) {
		return Component.translatable(MODID + ".config.text." + id);
	}




	public static void getPacks(Consumer<Pack> consumer){
		List<Tuple<Component, PackResources>> list = new ArrayList<>();
		ModContainer container = FabricLoader.getInstance().getModContainer(MODID).orElse(null);
		if(container != null){

			CommentedConfig config = CommentedConfig.getConfig();
			boolean b = currentMode.equals(WWOO.Mode.COMPATIBLE) || config.onlyVanillaBiomes();
			if(!b){
				Component s = Component.literal("WWOO");
				list.add(new Tuple<>(s, registerBuiltinResourcePack(new ResourceLocation(MODID, "wwoo_default"), s, container)));
			}
			if(config.forceLargeBiomes()){
				Component s = Component.literal("Large Biomes");
				list.add(new Tuple<>(s, registerBuiltinResourcePack(new ResourceLocation(MODID, "wwoo_force_large_biomes"), s, container)));
			}
		}
		if(list.isEmpty()) return;
		for (Tuple<Component, PackResources> entry : list) {
			PackResources pack = entry.getB();
			if(pack == null){
				LOGGER.debug("Pack is null");
				return;
			}
			if (!pack.getNamespaces(PackType.SERVER_DATA).isEmpty()) {
				Pack profile = Pack.readMetaAndCreate(
						pack.packId(),
						entry.getA(),
						true,
						ignored -> entry.getB(),
						PackType.SERVER_DATA,
						Pack.Position.TOP,
						new BuiltinModResourcePackSource(MODID)
				);

				//Pack profile = Pack.create(entry.getA(), true, () -> pack, factory, Pack.Position.TOP, new BuiltinModResourcePackSource(pack.getFabricModMetadata().getId()));
				if (profile != null) {
					//LOGGER.error(profile);
					consumer.accept(profile);
				}
				else LOGGER.error(pack + " couldn't be accepted");
			}
			else LOGGER.error(pack + " is empty");

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

	static PackResources registerBuiltinResourcePack(ResourceLocation id, Component displayName, ModContainer container) {
		return ModNioResourcePack.create(id, displayName, container, "resources/" + id.getPath(), PackType.SERVER_DATA, ResourcePackActivationType.ALWAYS_ENABLED);
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
