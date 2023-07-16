package cristelknight.wwoo.fabric;

import cristelknight.wwoo.WWOO;
import net.fabricmc.api.ModInitializer;

public class WWOOFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        WWOO.init();
    }

}
