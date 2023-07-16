package cristelknight.wwoo.forge;

import cristelknight.wwoo.WWOO;
import cristelknight.wwoo.config.cloth.ClothConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
@Mod(WWOO.MODID)
public class WWOOForge {

    public WWOOForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();


        modEventBus.addListener(this::commonSetup);

        if(isClothConfigLoaded()) ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> new ClothConfigScreen().create(parent)));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        //event.enqueueWork(VineryForgeVillagers::registerPOIs);
    }
    public static boolean isClothConfigLoaded(){
        return ModList.get().isLoaded("cloth_config");
    }
}
