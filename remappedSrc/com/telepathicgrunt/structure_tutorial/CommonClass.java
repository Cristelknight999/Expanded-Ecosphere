package com.telepathicgrunt.structure_tutorial;

import com.telepathicgrunt.structure_tutorial.config.PackConfig;
import com.telepathicgrunt.structure_tutorial.utils.MultiFilePackFinder;
import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourceType;

public class CommonClass {

	private static Path GAME_DIR;

	static{
		GAME_DIR = Path.of(".");

		String launchArgument = System.getProperty("sun.java.command");

		if(launchArgument == null){
			StructureTutorialMain.LOGGER.warn("Unable to find launch arguments, the mod might not function as expected.");
		}else if(launchArgument.contains("gameDir")){
			Pattern pattern = Pattern.compile("gameDir\\s(.+?)(?:\\s|$)");
			Matcher matcher = pattern.matcher(launchArgument);
			if(!matcher.find()){
				StructureTutorialMain.LOGGER.error("Unable to find gameDir in launch arguments '{}' even though it was specified", launchArgument);
			}else{
				String gameDirParam = matcher.group(1);
				GAME_DIR = Path.of(gameDirParam);
			}
		}
	}

	public static ResourcePackProvider getRepositorySource(ResourceType type, boolean force) {
		Set<File> files = new HashSet<>();

		List<String> packFolders = switch (type){
			case CLIENT_RESOURCES -> PackConfig.getRequiredResourceacks();
			case SERVER_DATA -> force ? PackConfig.getRequiredDatapacks() : PackConfig.getOptionalDatapacks();
		};

		packFolders.stream().map(str -> new File(GAME_DIR.toFile(), "/" + str))
				.forEach(files::add);

		return new MultiFilePackFinder(force, type, nameComp -> nameComp, files);
	}
}
