package de.cristelknight.wwee.neoforge;

import de.cristelknight.wwee.ExpandedEcosphere;
import de.cristelknight.wwee.config.cloth.ClothConfigScreen;
import de.cristelknight.wwee.terra.TerraInit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(ExpandedEcosphere.MODID)
public class ExpandedEcosphereForge {

    public ExpandedEcosphereForge(IEventBus bus, ModContainer container) {
        ExpandedEcosphere.init();



        if(ExpandedEcosphere.isTerraBlenderLoaded()) bus.addListener(this::terraBlenderSetup);
        if(isClothConfigLoaded()) container.registerExtensionPoint(IConfigScreenFactory.class, (mc, screen) ->
                new ClothConfigScreen().create(screen));
    }

    public static boolean isClothConfigLoaded(){
        return ModList.get().isLoaded("cloth_config");
    }

    private void terraBlenderSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if(ExpandedEcosphere.currentMode.equals(ExpandedEcosphere.Mode.COMPATIBLE)){
                TerraInit.terraEnable();
            }
        });
    }
}
