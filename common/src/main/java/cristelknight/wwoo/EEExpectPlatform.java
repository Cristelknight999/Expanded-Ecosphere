package cristelknight.wwoo;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class EEExpectPlatform {

    @ExpectPlatform
    public static boolean isNewer(String oldVersion, String newVersion){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static String getVersionForMod(String modId){
        throw new AssertionError();
    }

}
