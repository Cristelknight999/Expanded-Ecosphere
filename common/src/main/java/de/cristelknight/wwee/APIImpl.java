package de.cristelknight.wwee;

import de.cristelknight.cristellib.CristelLibRegistry;
import de.cristelknight.cristellib.StructureConfig;
import de.cristelknight.cristellib.api.CristelLibAPI;
import de.cristelknight.cristellib.api.CristelPlugin;
import de.cristelknight.cristellib.builtinpacks.BuiltInPackLoader;
import de.cristelknight.cristellib.config.ConfigType;
import de.cristelknight.wwee.config.configs.EEConfig;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Set;

@CristelPlugin
public class APIImpl implements CristelLibAPI {
    public static final StructureConfig MINECRAFT_ED = StructureConfig.createWithDefaultConfigPath(ExpandedEcosphere.MODID, "toggle_structure_config", ConfigType.TOGGLE);
    public static final StructureConfig MINECRAFT_P = StructureConfig.createWithDefaultConfigPath(ExpandedEcosphere.MODID, "placement_structure_config", ConfigType.PLACEMENT);

    @Override
    public void registerConfigs(Set<StructureConfig> sets) {
        sets.add(MINECRAFT_ED);
        sets.add(MINECRAFT_P);

        MINECRAFT_ED.setHeader("""
                This config makes it possible to switch off any structure.
                To disable a structure, simply set the value of that structure to "false".
                To change the rarity of a structure category use the structure placement config.
                
                =====
                Created by Cristel Lib
                """);
        MINECRAFT_P.setHeader("""
                This config makes it possible to change the spacing, separation, salt (and frequency) of the structure sets.
                    SPACING ---  controls how far a structure can be from others of its kind
                	SEPARATION --- controls how close to each other two structures of the same type can be.
                KEEP IN MIND THAT SPACING ALWAYS NEEDS TO BE HIGHER THAN SEPARATION.
                
                =====
                Created by Cristel Lib
                """);
    }

    @Override
    public void registerStructureSets(CristelLibRegistry registry) {
        registry.registerSetToConfig(ExpandedEcosphere.MODID, "wythers", List.of(
                        "villages", "features", "banyan_sparse_jungle",
                        "baobab_dry_tropical_forest", "baobab_savanna",
                        "baobab_tropical_forest", "el_dorado",
                        "elephant_graveyard_fossils"
                ),
                MINECRAFT_ED, MINECRAFT_P);
    }

    @Override
    public void registerBuiltInPacks() {
        BuiltInPackLoader.registerPack(EERL.create("resources/ee_default"), Component.literal("Expanded Ecosphere Default World Gen"), () -> ExpandedEcosphere.currentMode.equals(ExpandedEcosphere.Mode.DEFAULT));
        BuiltInPackLoader.registerPack(EERL.create("resources/ee_remove_blobs"), Component.literal("Disables granit, etc."), () -> EEConfig.DEFAULT.getConfig().removeOreBlobs());
        BuiltInPackLoader.registerPack(EERL.create("resources/ee_force_large_biomes"), Component.literal("Forcing LARGE biomes"), () -> EEConfig.DEFAULT.getConfig().forceLargeBiomes());
    }

}