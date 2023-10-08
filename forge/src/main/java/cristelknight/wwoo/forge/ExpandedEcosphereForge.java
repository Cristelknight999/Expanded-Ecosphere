package cristelknight.wwoo.forge;

import cristelknight.wwoo.ExpandedEcosphere;
import cristelknight.wwoo.config.cloth.ClothConfigScreen;
import cristelknight.wwoo.terra.TerraInit;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
@Mod(ExpandedEcosphere.MODID)
public class ExpandedEcosphereForge {

    public ExpandedEcosphereForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ExpandedEcosphere.init();

        if(ExpandedEcosphere.isTerraBlenderLoaded()) modEventBus.addListener(this::terraBlenderSetup);
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
