package cristelknight.wwoo.fabric;

import cristelknight.wwoo.ExpandedEcosphere;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;

public class EEExpectPlatformImpl {
    public static boolean isNewer(String oldVersion, String newVersion) {
        try {
            if(Version.parse(oldVersion).compareTo(Version.parse(newVersion)) < 0){
                return true;
            }
        } catch (VersionParsingException ignored) {
            ExpandedEcosphere.LOGGER.error("Couldn't parse versions: old version: {}, new version: {}", oldVersion, newVersion);
        }
        return false;
    }

    public static String getVersionForMod(String modId) {
        return FabricLoader.getInstance().getModContainer(modId).get().getMetadata().getVersion().toString();
    }
}
