package com.telepathicgrunt.structure_tutorial.config;

import java.util.ArrayList;
import java.util.List;

public class PackConfig {


	public static List<String> getRequiredDatapacks() {
		List<String> REQUIRED_DATAPACKS = new ArrayList<>();
		REQUIRED_DATAPACKS.add("global_packs/required_data/");
		return REQUIRED_DATAPACKS;
	}

	public static List<String> getOptionalDatapacks() {
		List<String> OPTIONAL_DATAPACKS = new ArrayList<>();
		OPTIONAL_DATAPACKS.add("global_packs/optional_data/");
		return OPTIONAL_DATAPACKS;
	}

	public static List<String> getRequiredResourceacks() {
		List<String> REQUIRED_RESOURCEACKS = new ArrayList<>();
		REQUIRED_RESOURCEACKS.add("global_packs/required_resources/");
		return REQUIRED_RESOURCEACKS;
	}

}
