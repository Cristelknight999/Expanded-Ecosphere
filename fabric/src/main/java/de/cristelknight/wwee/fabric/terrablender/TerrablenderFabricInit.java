package de.cristelknight.wwee.fabric.terrablender;

import de.cristelknight.wwee.ExpandedEcosphere;
import de.cristelknight.wwee.terra.TerraInit;
import terrablender.api.TerraBlenderApi;

public class TerrablenderFabricInit implements TerraBlenderApi {

    @Override
    public void onTerraBlenderInitialized() {
        if(ExpandedEcosphere.currentMode.equals(ExpandedEcosphere.Mode.COMPATIBLE)){
            TerraInit.terraEnable();
        }
    }
}
