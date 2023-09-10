package cristelknight.wwoo.config.cloth;

import cristelknight.wwoo.WWOO;
import cristelknight.wwoo.config.configs.BannedBiomesConfig;
import cristelknight.wwoo.config.configs.WWOOConfig;
import cristelknight.wwoo.utils.BiomeReplace;
import cristelknight.wwoo.utils.Util;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.entries.*;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.cristellib.CristelLib;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
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

import static cristelknight.wwoo.WWOO.*;
import static cristelknight.wwoo.WWOO.Mode.DEFAULT;


@Environment(value= EnvType.CLIENT)
public class ClothConfigScreen {

    private static Screen lastScreen;

    public Screen create(Screen parent) {
        lastScreen = parent;
        WWOOConfig config = WWOOConfig.DEFAULT.getConfig();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setDefaultBackgroundTexture(new ResourceLocation(getIdentifier(config.backGroundBlock().getBlock())))
                .setTitle(Component.translatable(MODID + ".config.title").withStyle(ChatFormatting.BOLD));

        ConfigEntries entries = new ConfigEntries(builder.entryBuilder(), builder.getOrCreateCategory(mainName("main")), builder.getOrCreateCategory(mainName("biomes")), builder.getOrCreateCategory(mainName("modes")));
        builder.setSavingRunnable(() -> {
            WWOOConfig.DEFAULT.setInstance(entries.createConfig());
            WWOOConfig.DEFAULT.getConfig(true, true);

            BannedBiomesConfig.DEFAULT.setInstance(entries.createBiomesConfig());
            BannedBiomesConfig config2 = BannedBiomesConfig.DEFAULT.getConfig(true, true);


            if(config2.enableBiomes() && WWOO.currentMode.equals(DEFAULT)) BiomeReplace.replace();
            else CristelLib.DATA_PACK.removeData(new ResourceLocation("minecraft", "overworld"));
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
        return Component.translatable(MODID + ".config.entry.toolTip" + id);
    }

    private static Component textEntry(String id) {
        return Component.translatable(MODID + ".config.text." + id);
    }

    private static class ConfigEntries {
        private final ConfigEntryBuilder builder;
        private final BooleanListEntry showUpdates, showBigUpdates, onlyVanillaBiomes, forceLargeBiomes, enableBiomes;
        private final @NotNull DropdownBoxEntry<Block> backgroundBlock;
        private final EnumListEntry<WWOO.Mode> mode;
        private final StringListListEntry biomeList;

        public ConfigEntries(ConfigEntryBuilder builder, ConfigCategory category1, ConfigCategory category2, ConfigCategory category3) {
            this.builder = builder;

            WWOOConfig config = WWOOConfig.DEFAULT.getConfig();

            // Tab 3
            if(!isTerraBlenderLoaded()){
                textListEntry(Component.translatable(MODID + ".config.text.requiresTerrablender", minTerraBlenderVersion), category3);
                textListEntry(Component.translatable(MODID + ".config.text.").withStyle((s) -> s.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/terrablender"))), category3);
            }
            mode = builder.startEnumSelector(fieldName("selectMode"), WWOO.Mode.class, currentMode).setDefaultValue(DEFAULT).setRequirement(Requirement.isTrue(WWOO::isTerraBlenderLoaded)).build();
            category3.addEntry(mode);


            // Tab 2
            enableBiomes = createBooleanField("enableBiomes", BannedBiomesConfig.DEFAULT.getConfig().enableBiomes(), BannedBiomesConfig.DEFAULT.enableBiomes(), category2, new Component[]{});
            biomeList = builder.startStrList(fieldName("biomeList"), convertMapToList(BannedBiomesConfig.DEFAULT.getConfig().bannedBiomes())).setTooltip(new Component[]{fieldToolTip("biomeList")}).setDefaultValue(List.of()).setRequirement(Requirement.isTrue(enableBiomes)).build();
            category2.addEntry(biomeList);


            // Tab 1
            textListEntry(Component.translatable(MODID + ".config.text.modes", Component.literal(currentMode.toString()).withStyle(ChatFormatting.DARK_PURPLE)).withStyle(ChatFormatting.GRAY), category1);

            backgroundBlock = createBlockField("bB", config.backGroundBlock().getBlock(), WWOOConfig.DEFAULT.backGroundBlock().getBlock(), category1, List.of(FT.NO_BLOCK_ENTITY, FT.NO_BUTTON));
            showUpdates = createBooleanField("showUpdates", config.showUpdates(), WWOOConfig.DEFAULT.showUpdates(), category1, new Component[]{});
            showBigUpdates = createBooleanField("showBigUpdates", config.showBigUpdates(), WWOOConfig.DEFAULT.showBigUpdates(), category1, new Component[]{});
            onlyVanillaBiomes = builder.startBooleanToggle(fieldName("onlyVanillaBiomes"), config.onlyVanillaBiomes()).setDefaultValue(WWOOConfig.DEFAULT.onlyVanillaBiomes()).setRequirement(() -> mode.getValue().equals(DEFAULT)).build();
            category1.addEntry(onlyVanillaBiomes);
            forceLargeBiomes = createBooleanField("forceLargeBiomes", config.forceLargeBiomes(), WWOOConfig.DEFAULT.forceLargeBiomes(), category1, new Component[]{});

            textListEntry(Util.translatableText("forceLargeBiomes").withStyle(ChatFormatting.GRAY), category1);





            linkButtons(category1);
            linkButtons(category2);
            linkButtons(category3);
        }



        public WWOOConfig createConfig() {
            WWOO.Mode currentMode = mode.getValue();
            WWOO.currentMode = currentMode;

            return new WWOOConfig(currentMode.toString(), onlyVanillaBiomes.getValue(), forceLargeBiomes.getValue(), showUpdates.getValue(), showBigUpdates.getValue(), backgroundBlock.getValue().defaultBlockState());
        }

        public BannedBiomesConfig createBiomesConfig() {
            return new BannedBiomesConfig(enableBiomes.getValue(), convertListToMap(biomeList.getValue()));
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

        private void linkButtons(ConfigCategory category){
            TextListEntry tle = this.builder.startTextDescription(Component.literal(" ")).build();
            category.addEntry(tle);
            category.addEntry(new LinkEntry(fieldName("dc"), buttonWidget -> Minecraft.getInstance().setScreen(new ConfirmLinkScreen(confirmed -> {
                if (confirmed) {
                    net.minecraft.Util.getPlatform().openUri(WWOO.LINK_DC);
                }
                Minecraft.getInstance().setScreen(new ClothConfigScreen().create(lastScreen)); }, WWOO.LINK_DC, true)), new ResourceLocation(MODID, "textures/gui/dc.png"), 3));
            category.addEntry(tle);
            category.addEntry(new LinkEntry(fieldName("h"), buttonWidget -> Minecraft.getInstance().setScreen(new ConfirmLinkScreen(confirmed -> {
                if (confirmed) {
                    net.minecraft.Util.getPlatform().openUri(WWOO.LINK_H);
                }
                Minecraft.getInstance().setScreen(new ClothConfigScreen().create(lastScreen)); }, WWOO.LINK_H, true)), new ResourceLocation(MODID, "textures/gui/cf.png"), 10));
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
