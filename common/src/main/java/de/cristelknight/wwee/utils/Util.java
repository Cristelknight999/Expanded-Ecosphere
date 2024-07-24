package de.cristelknight.wwee.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import de.cristelknight.wwee.ExpandedEcosphere;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static de.cristelknight.wwee.ExpandedEcosphere.Mode.DEFAULT;

public class Util {

	public static MutableComponent translatableText(String id) {
		return Component.translatable(ExpandedEcosphere.MODID + ".config.text." + id);
	}

	public static <T> T readConfig(JsonElement load, Codec<T> codec, DynamicOps<JsonElement> ops) {
		DataResult<Pair<T, JsonElement>> decode = codec.decode(ops, load);
		Optional<DataResult.Error<Pair<T, JsonElement>>> error = decode.error();
		if (error.isPresent()) {
			throw new IllegalArgumentException("Couldn't read " + load.toString() + ", crashing instead.");
		}
		return decode.result().orElseThrow().getFirst();
	}

	public static ExpandedEcosphere.Mode getMode(String mode){
		if(!ExpandedEcosphere.isTerraBlenderLoaded()){
			return DEFAULT;
		}
		try {
			return ExpandedEcosphere.Mode.valueOf(mode.toUpperCase());
		}
		catch (IllegalArgumentException e){
			ExpandedEcosphere.LOGGER.warn("Invalid Mode '{}' for option '{}'", mode, "mode");
			return DEFAULT;
		}
	}


	public static @Nullable JsonObject getObjectFromPath(Path path){
		InputStream im;
		try {
			if(path == null){
				ExpandedEcosphere.LOGGER.error("Path was null");
				return null;
			}
			im = Files.newInputStream(path);
		} catch (IOException e) {
            ExpandedEcosphere.LOGGER.error("Couldn't read {}", path);
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
