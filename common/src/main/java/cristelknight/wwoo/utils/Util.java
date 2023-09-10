package cristelknight.wwoo.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import cristelknight.wwoo.WWOO;
import net.cristellib.CristelLibExpectPlatform;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cristelknight.wwoo.WWOO.Mode.DEFAULT;

public class Util {

	public static MutableComponent translatableText(String id) {
		return Component.translatable(WWOO.MODID + ".config.text." + id);
	}

	public static <T> T readConfig(JsonElement load, Codec<T> codec, DynamicOps<JsonElement> ops) {
		DataResult<Pair<T, JsonElement>> decode = codec.decode(ops, load);
		Optional<DataResult.PartialResult<Pair<T, JsonElement>>> error = decode.error();
		if (error.isPresent()) {
			throw new IllegalArgumentException("Couldn't read " + load.toString() + ", crashing instead.");
		}
		return decode.result().orElseThrow().getFirst();
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


	public static @Nullable JsonObject getObjectFromPath(Path path){
		InputStream im;
		try {
			if(path == null){
				WWOO.LOGGER.error("Path was null");
				return null;
			}
			im = Files.newInputStream(path);
		} catch (IOException e) {
			WWOO.LOGGER.error("Couldn't read " + path);
			return null;
		}

		try (InputStreamReader reader = new InputStreamReader(im)) {
			JsonElement el = JsonParser.parseReader(reader);
			if (!el.isJsonObject()) throw new RuntimeException("Input stream is on JsonElement");
			return el.getAsJsonObject();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
