package de.cristelknight.wwee;

import net.minecraft.resources.ResourceLocation;

public class EERL {

    public static ResourceLocation create(String path){
        return ResourceLocation.fromNamespaceAndPath(ExpandedEcosphere.MODID, path);
    }

    public static String asString(String path) {
        return ExpandedEcosphere.MODID + ":" + path;
    }
}
