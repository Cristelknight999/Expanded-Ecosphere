package cristelknight.wwoo;

import net.minecraft.resources.ResourceLocation;

public class EERL extends ResourceLocation {

    public EERL(String path) {
        super(ExpandedEcosphere.MODID, path);
    }

    public static String asString(String path) {
        return (ExpandedEcosphere.MODID + ":" + path);
    }
}
