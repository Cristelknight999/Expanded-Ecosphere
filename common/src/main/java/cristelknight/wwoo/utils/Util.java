package cristelknight.wwoo.utils;

import cristelknight.wwoo.WWOO;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import static cristelknight.wwoo.WWOO.Mode.DEFAULT;

public class Util {

	public static MutableComponent translatableText(String id) {
		return Component.translatable(WWOO.MODID + ".config.text." + id);
	}


	public static WWOO.Mode getMode(String mode){
		if(!WWOO.isTerraBlenderLoaded()){
			return DEFAULT;
		}
		try {
			return WWOO.Mode.valueOf(mode.toUpperCase());
		}
		catch (IllegalArgumentException e){
			WWOO.LOGGER.warn("Invalid Mode '{}' for option '{}'", mode, "mode");
			return DEFAULT;
		}
	}
}
