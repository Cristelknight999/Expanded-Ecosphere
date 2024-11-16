package de.cristelknight.wwee.neoforge;

import de.cristelknight.wwee.ExpandedEcosphere;
import de.cristelknight.wwee.neoforge.client.NeoForgeClient;
import de.cristelknight.wwee.terra.TerraInit;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(ExpandedEcosphere.MODID)
public class ExpandedEcosphereForge {

    public ExpandedEcosphereForge(IEventBus bus, ModContainer container) {
        ExpandedEcosphere.init();

        if(ExpandedEcosphere.isTerraBlenderLoaded()) bus.addListener(this::terraBlenderSetup);

        if(FMLEnvironment.dist.equals(Dist.CLIENT) && isClothConfigLoaded()) NeoForgeClient.registerConfigScreen(container);
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
