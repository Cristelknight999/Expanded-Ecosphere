package de.cristelknight.wwee.neoforge.client;

import de.cristelknight.wwee.config.cloth.ClothConfigScreen;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class NeoForgeClient {

    public static void registerConfigScreen(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (mc, screen) ->
                new ClothConfigScreen().create(screen));
    }

}