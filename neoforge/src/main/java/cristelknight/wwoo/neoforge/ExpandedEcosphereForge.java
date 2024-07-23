package cristelknight.wwoo.neoforge;

import cristelknight.wwoo.ExpandedEcosphere;
import cristelknight.wwoo.config.cloth.ClothConfigScreen;
import cristelknight.wwoo.terra.TerraInit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.ConfigScreenHandler;

@Mod(ExpandedEcosphere.MODID)
public class ExpandedEcosphereForge {

    public ExpandedEcosphereForge(IEventBus bus) {
        ExpandedEcosphere.init();

        if(ExpandedEcosphere.isTerraBlenderLoaded()) bus.addListener(this::terraBlenderSetup);
        if(isClothConfigLoaded()) ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> new ClothConfigScreen().create(parent)));
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
