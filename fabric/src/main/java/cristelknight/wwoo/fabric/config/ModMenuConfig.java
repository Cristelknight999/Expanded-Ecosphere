package cristelknight.wwoo.fabric.config;


import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import cristelknight.wwoo.config.cloth.ClothConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

@Environment(value= EnvType.CLIENT)
public class ModMenuConfig implements ModMenuApi {
    public static final ConfigScreenFactory<?> screen = FabricLoader.getInstance().isModLoaded("cloth-config2")
            ? new ClothConfigScreen()::create
            : parent -> null;

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen;
    }
}