package cristelknight.wwoo.config.cloth;

import cristelknight.wwoo.ExpandedEcosphere;
import cristelknight.wwoo.config.configs.ReplaceBiomesConfig;
import cristelknight.wwoo.config.configs.EEConfig;
import cristelknight.wwoo.terra.TerraInit;
import cristelknight.wwoo.utils.BiomeReplace;
import cristelknight.wwoo.utils.Util;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.*;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.cristellib.CristelLib;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cristelknight.wwoo.ExpandedEcosphere.*;
import static cristelknight.wwoo.ExpandedEcosphere.Mode.DEFAULT;


@Environment(value= EnvType.CLIENT)
public class ClothConfigScreen {


    public Screen create(Screen parent) {
        EEConfig config = EEConfig.DEFAULT.getConfig();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setDefaultBackgroundTexture(new ResourceLocation(getIdentifier(config.backGroundBlock().getBlock())))
                .setTitle(Component.translatable(MODID + ".config.title").withStyle(ChatFormatting.BOLD));

        ConfigEntries entries = new ConfigEntries(builder.entryBuilder(), builder.getOrCreateCategory(mainName("main")), builder.getOrCreateCategory(mainName("biomes")), builder.getOrCreateCategory(mainName("modes")));
        builder.setSavingRunnable(() -> {
            EEConfig.DEFAULT.setInstance(entries.createConfig());
            EEConfig.DEFAULT.getConfig(true, true);

            ReplaceBiomesConfig.DEFAULT.setInstance(entries.createBiomesConfig());
            ReplaceBiomesConfig config2 = ReplaceBiomesConfig.DEFAULT.getConfig(true, true);

            if(ExpandedEcosphere.isTerraBlenderLoaded()) TerraInit.terraEnableDisable();
            if(config2.enableBiomes() && ExpandedEcosphere.currentMode.equals(DEFAULT)) BiomeReplace.replace();
            else CristelLib.DATA_PACK.removeData(new ResourceLocation("minecraft", "dimension/overworld.json"));
        });
        return builder.build();
    }

    private static Component fieldName(String id) {
        return Component.translatable(MODID + ".config.entry." + id);
    }

    private static Component mainName(String id) {
        return Component.translatable(MODID + ".config.category." + id);
    }

    private static Component fieldToolTip(String id) {
        return Component.translatable(MODID + ".config.entry." + id + ".toolTip");
    }

    private static class ConfigEntries {
        private final ConfigEntryBuilder builder;
        private final BooleanListEntry removeOreBlobs, showUpdates, showBigUpdates, forceLargeBiomes, enableBiomes;
        private final @NotNull DropdownBoxEntry<Block> backgroundBlock;
        private final EnumListEntry<ExpandedEcosphere.Mode> mode;
        private final StringListListEntry biomeList;

        public ConfigEntries(ConfigEntryBuilder builder, ConfigCategory category1, ConfigCategory category2, ConfigCategory category3) {
            this.builder = builder;

            EEConfig config = EEConfig.DEFAULT.getConfig();

            // Tab 3
            if(!isTerraBlenderLoaded()){
                textListEntry(Component.translatable(MODID + ".config.text.requiresTerrablender", minTerraBlenderVersion), category3);
                textListEntry(Component.translatable(MODID + ".config.text.downloadTB").withStyle((s) -> s.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/terrablender"))), category3);
            }
            mode = builder.startEnumSelector(fieldName("selectMode"), ExpandedEcosphere.Mode.class, currentMode).setDefaultValue(DEFAULT).build();
            category3.addEntry(mode);
            textListEntry(Component.translatable(MODID + ".config.text.defaultMode").withStyle(ChatFormatting.GRAY), category3);
            textListEntry(Component.translatable(MODID + ".config.text.compatibleMode").withStyle(ChatFormatting.GRAY), category3);



            // Tab 2
            enableBiomes = createBooleanField("enableBiomes", ReplaceBiomesConfig.DEFAULT.getConfig().enableBiomes(), ReplaceBiomesConfig.DEFAULT.enableBiomes(), category2, new Component[]{});
            biomeList = builder.startStrList(fieldName("biomeList"), convertMapToList(ReplaceBiomesConfig.DEFAULT.getConfig().bannedBiomes())).setTooltip(fieldToolTip("biomeList")).setDefaultValue(List.of()).build();
            category2.addEntry(biomeList);
            textListEntry(Component.translatable(MODID + ".config.text.replaceBiomes").withStyle(ChatFormatting.GRAY), category2);

            // Tab 1
            textListEntry(Component.translatable(MODID + ".config.text.modes", Component.literal(currentMode.toString()).withStyle(ChatFormatting.DARK_PURPLE)).withStyle(ChatFormatting.GRAY), category1);

            backgroundBlock = createBlockField("bB", config.backGroundBlock().getBlock(), EEConfig.DEFAULT.backGroundBlock().getBlock(), category1, List.of(FT.NO_BLOCK_ENTITY, FT.NO_BUTTON));
            showUpdates = createBooleanField("showUpdates", config.showUpdates(), EEConfig.DEFAULT.showUpdates(), category1, new Component[]{});
            showBigUpdates = createBooleanField("showBigUpdates", config.showBigUpdates(), EEConfig.DEFAULT.showBigUpdates(), category1, new Component[]{});
            removeOreBlobs = createBooleanField("removeOreBlobs", config.removeOreBlobs(), EEConfig.DEFAULT.removeOreBlobs(), category1, new Component[]{fieldToolTip("removeOreBlobs")});
            forceLargeBiomes = createBooleanField("forceLargeBiomes", config.forceLargeBiomes(), EEConfig.DEFAULT.forceLargeBiomes(), category1, new Component[]{});

            textListEntry(Util.translatableText("forceLargeBiomes").withStyle(ChatFormatting.GRAY), category1);

        }



        public EEConfig createConfig() {
            ExpandedEcosphere.Mode currentMode = mode.getValue();
            ExpandedEcosphere.currentMode = currentMode;

            return new EEConfig(currentMode.toString(), forceLargeBiomes.getValue(), removeOreBlobs.getValue(), showUpdates.getValue(), showBigUpdates.getValue(), backgroundBlock.getValue().defaultBlockState());
        }

        public ReplaceBiomesConfig createBiomesConfig() {
            return new ReplaceBiomesConfig(enableBiomes.getValue(), convertListToMap(biomeList.getValue()));
        }
        private static List<String> convertMapToList(Map<String, String> stringMap) {
            return stringMap.entrySet().stream()
                    .map(entry -> entry.getKey() + "/" + entry.getValue())
                    .collect(Collectors.toList());
        }
        private static Map<String, String> convertListToMap(List<String> stringList) {
            return stringList.stream()
                    .map(s -> s.split("/"))
                    .filter(parts -> parts.length == 2)
                    .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
        }


        private BooleanListEntry createBooleanField(String id, boolean value, boolean defaultValue, ConfigCategory category, Component[] tooltip) {
            BooleanListEntry e = builder.startBooleanToggle(fieldName(id), value)
                    .setDefaultValue(defaultValue).setTooltip(tooltip).build();

            category.addEntry(e);
            return e;
        }
        private @NotNull DropdownBoxEntry<Block> createBlockField(String id, Block value, Block defaultValue, ConfigCategory category, List<FT> filter) {
            DropdownMenuBuilder<Block> e = builder.startDropdownMenu(fieldName(id), DropdownMenuBuilder.TopCellElementBuilder.ofBlockObject(value), DropdownMenuBuilder.CellCreatorBuilder.ofBlockObject())
                    .setDefaultValue(defaultValue)
                    .setSelections(BuiltInRegistries.BLOCK.stream().sorted(Comparator.comparing(Block::toString)).filter(new BlockPredicate(filter)).collect(Collectors.toCollection(LinkedHashSet::new)));
            @NotNull DropdownBoxEntry<Block> entry = e.build();
            category.addEntry(entry);
            return entry;
        }

        public void textListEntry(Component component, ConfigCategory category){
            TextListEntry tle = this.builder.startTextDescription(component).build();
            category.addEntry(tle);
        }

        static class BlockPredicate implements Predicate<Block> {
            private final List<FT> filters;
            public BlockPredicate(List<FT> filters) {
                this.filters = filters;
            }

            @Override
            public boolean test(Block block) {
                boolean b = true;
                for(FT filter : filters){
                    if(block instanceof AirBlock){
                        b = false;
                    }
                    if(filter.equals(FT.NO_BUTTON)){
                        if(block instanceof ButtonBlock){
                            b = false;
                        }
                    }
                    if(filter.equals(FT.PILLAR)){
                        if(!(block instanceof RotatedPillarBlock)){
                            b = false;
                        }
                    }
                    if(filter.equals(FT.NO_BLOCK_ENTITY)){
                        if(block.defaultBlockState().hasBlockEntity()){
                            b = false;
                        }
                    }
                }

                return b;
            }
        }
    }

    private static String getIdentifier(Block b){
        Stream<String> s = Arrays.stream(BuiltInRegistries.BLOCK.getKey(b).toString().split(":"));
        List<String> l = s.toList();
        String s2 = l.get(1);
        if(b instanceof SnowyDirtBlock || b instanceof DoorBlock || b.equals(Blocks.CAKE) || b.equals(Blocks.LOOM)){
            s2 = s2 + "_top";
        }
        else if(b.equals(Blocks.TNT)){
            s2 = s2 +"_side";
        }
        else if(b.equals(Blocks.LAVA) || b.equals(Blocks.WATER)){
            s2 = s2 +"_still";
        }
        else if(b instanceof FireBlock){
            s2 = s2 +"_0";
        }
        return l.get(0) + ":textures/block/" + s2 + ".png";
    }

    public enum FT {
        NO_BUTTON,
        PILLAR,
        NO_BLOCK_ENTITY,
        NONE
    }

}
