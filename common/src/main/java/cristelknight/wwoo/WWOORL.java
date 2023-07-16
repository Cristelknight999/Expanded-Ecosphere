package cristelknight.wwoo;

import net.minecraft.resources.ResourceLocation;

public class WWOORL extends ResourceLocation {

    public WWOORL(String path) {
        super(WWOO.MODID, path);
    }

    public static String asString(String path) {
        return (WWOO.MODID + ":" + path);
    }
}
