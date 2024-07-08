package cristelknight.tt.fabric;

import cristelknight.tt.TT;
import net.fabricmc.api.ModInitializer;

public class TTFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        TT.init();
    }

}
