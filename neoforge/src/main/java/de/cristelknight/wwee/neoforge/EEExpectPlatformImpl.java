package de.cristelknight.wwee.neoforge;

import net.neoforged.fml.ModList;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class EEExpectPlatformImpl {
    public static boolean isNewer(String oldVersion, String newVersion) {
        return new DefaultArtifactVersion(oldVersion).compareTo(new DefaultArtifactVersion(newVersion)) < 0;
    }

    public static String getVersionForMod(String modId) {
        return ModList.get().getModContainerById(modId).get().getModInfo().getVersion().toString();
    }
}
