package com.telepathicgrunt.structure_tutorial.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ZipResourcePack;

public class MultiFilePackFinder implements ResourcePackProvider {

	private static final FileFilter RESOURCEPACK_FILTER = (pack) ->
			(pack.isFile() && pack.getName().endsWith(".zip")) ||
					(pack.isDirectory() && (new File(pack, "pack.mcmeta")).isFile());

	private static final FileFilter DATAPACK_FILTER = (pack) ->
			(pack.isFile() && pack.getName().endsWith(".zip")) ||
					(pack.isDirectory() && (new File(pack, "pack.mcmeta")).isFile() && (new File(pack, "data/")).exists());

	private final boolean shouldForcePacks;
	private final Map<File, FilePackType> packs;
	private final ResourcePackSource packSource;
	private final ResourceType packType;

	public MultiFilePackFinder(boolean shouldForcePacks, ResourceType packType, ResourcePackSource packSource, Set<File> files) {
		this.shouldForcePacks = shouldForcePacks;
		this.packSource = packSource;
		this.packs = new HashMap<>();
		this.packType = packType;
		for (File file : files)
			this.packs.put(file, FilePackType.MISSING);
	}

	private void updatePacks() {
		for (File file : this.packs.keySet()) {
			if (file.isFile() && file.getPath().endsWith(".zip"))
				packs.put(file, FilePackType.ZIPED_PACK);
			else if (file.isDirectory() && new File(file, "pack.mcmeta").exists())
				packs.put(file, FilePackType.UNZIPED_PACK);
			else {
				if (!file.exists())
					file.mkdirs();
				packs.put(file, FilePackType.PACK_FOLDER);
			}
		}
	}

	@Override
	public void register(Consumer<ResourcePackProfile> packConsumer, ResourcePackProfile.Factory packBuilder) {
		updatePacks();

		for (File file : this.packs.keySet()) {
			FilePackType type = this.packs.get(file);

			ResourcePackProfile pack = null;

			switch (type) {
				case UNZIPED_PACK:
				case ZIPED_PACK:
					pack = ResourcePackProfile.of(
							this.shouldForcePacks ? "global:" : "globalOpt:" + file.getName(), this.shouldForcePacks,
							createSupplier(file),
							packBuilder, ResourcePackProfile.InsertionPosition.TOP, this.packSource
					);
					if (pack != null)
						packConsumer.accept(pack);
					break;
				case PACK_FOLDER:
					File[] afile = file.listFiles(this.packType == ResourceType.SERVER_DATA ? DATAPACK_FILTER : RESOURCEPACK_FILTER);
					if (afile != null)
						for (File packFile : afile) {
							pack = ResourcePackProfile.of(
									this.shouldForcePacks ? "global:" : "globalOpt:" + packFile.getName(), this.shouldForcePacks,
									createSupplier(packFile),
									packBuilder, ResourcePackProfile.InsertionPosition.TOP, this.packSource);
							if (pack != null) {
								packConsumer.accept(pack);
								pack = null;
							}
						}
					break;
				default:
					break;
			}
		}
	}

	private Supplier<ResourcePack> createSupplier(File pack) {
		return pack.isDirectory() ? () -> new DirectoryResourcePack(pack) : () -> new ZipResourcePack(pack);
	}

	private enum FilePackType {
		MISSING,
		ZIPED_PACK,
		UNZIPED_PACK,
		PACK_FOLDER
	}
}