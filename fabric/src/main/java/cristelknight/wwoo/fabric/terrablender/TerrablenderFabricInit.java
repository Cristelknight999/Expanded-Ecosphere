package cristelknight.wwoo.fabric.terrablender;

import cristelknight.wwoo.terra.TerraInit;
import terrablender.api.TerraBlenderApi;

public class TerrablenderFabricInit implements TerraBlenderApi {

    @Override
    public void onTerraBlenderInitialized() {
        TerraInit.registerRegions();
        TerraInit.readOverworldSurfaceRules();
    }
}
