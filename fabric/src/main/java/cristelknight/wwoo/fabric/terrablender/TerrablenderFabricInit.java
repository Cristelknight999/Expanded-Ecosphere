package cristelknight.wwoo.fabric.terrablender;

import cristelknight.wwoo.WWOO;
import cristelknight.wwoo.terra.TerraInit;
import terrablender.api.TerraBlenderApi;

public class TerrablenderFabricInit implements TerraBlenderApi {

    @Override
    public void onTerraBlenderInitialized() {
        if(WWOO.currentMode.equals(WWOO.Mode.COMPATIBLE)){
            TerraInit.terraEnable();
        }
    }
}
