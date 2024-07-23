package cristelknight.wwoo.fabric.terrablender;

import cristelknight.wwoo.ExpandedEcosphere;
import cristelknight.wwoo.terra.TerraInit;
import terrablender.api.TerraBlenderApi;

public class TerrablenderFabricInit implements TerraBlenderApi {

    @Override
    public void onTerraBlenderInitialized() {
        if(ExpandedEcosphere.currentMode.equals(ExpandedEcosphere.Mode.COMPATIBLE)){
            TerraInit.terraEnable();
        }
    }
}
