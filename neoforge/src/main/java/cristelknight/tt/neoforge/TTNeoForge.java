package cristelknight.tt.neoforge;

import cristelknight.tt.TT;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(TT.MODID)
public class TTNeoForge {

    public TTNeoForge(IEventBus bus) {
        TT.init();
    }

}
