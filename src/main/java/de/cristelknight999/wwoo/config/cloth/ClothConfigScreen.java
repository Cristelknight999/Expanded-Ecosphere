package de.cristelknight999.wwoo.config.cloth;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import de.cristelknight999.wwoo.config.configs.CommentedConfig;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.*;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.cristelknight999.wwoo.WWOO.*;
import static de.cristelknight999.wwoo.utils.Util.translatableText;


@Environment(value= EnvType.CLIENT)
public class ClothConfigScreen implements ConfigScreenFactory<Screen> {

    private static Screen lastScreen;

    @Override
    public Screen create(Screen parent) {
        lastScreen = parent;
        CommentedConfig config = CommentedConfig.getConfig();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setDefaultBackgroundTexture(new ResourceLocation(getIdentifier(config.backGroundBlock().getBlock())))
                .setTitle(Component.translatable(MODID + ".config.title").withStyle(ChatFormatting.BOLD));

        ConfigEntries entries = new ConfigEntries(builder.entryBuilder(), builder.getOrCreateCategory(mainName("main")), builder.getOrCreateCategory(mainName("default")), builder.getOrCreateCategory(mainName("compatible")));
        builder.setSavingRunnable(() -> {
            CommentedConfig.setINSTANCE(entries.createConfig());
            CommentedConfig.getConfig(true, true);
        });
        return builder.build();
    }

    private static Component fieldName(String id) {
        return Component.translatable(MODID + ".config.entry." + id);
    }

    private static Component mainName(String id) {
        return Component.translatable(MODID + ".config.category." + id);
    }

    private static class ConfigEntries {
        private final ConfigEntryBuilder builder;
        private final BooleanListEntry showUpdates, showBigUpdates, onlyVanillaBiomes, forceLargeBiomes;
        private final @NotNull DropdownBoxEntry<Block> backgroundBlock;
        private final ConfigCategory category1, category2, category3;

        public ConfigEntries(ConfigEntryBuilder builder, ConfigCategory category1, ConfigCategory category2, ConfigCategory category3) {
            this.builder = builder;
            this.category1 = category1;
            this.category2 = category2;
            this.category3 = category3;


            SubCategoryBuilder main1 = new SubCategoryBuilder(Component.literal("main1"), Component.literal("main1"));
            SubCategoryBuilder main2 = new SubCategoryBuilder(Component.literal("main2"), Component.literal("main2"));
            SubCategoryBuilder main3 = new SubCategoryBuilder(Component.literal("main3"), Component.literal("main3"));


            category2.addEntry(new SelectEntry(buttonWidget -> SelectEntry.onPress()));
            category3.addEntry(new SelectEntry2(buttonWidget -> SelectEntry2.onPress()));

            addTexts(main1, main3);

            CommentedConfig config = CommentedConfig.getConfig();

            textListEntry(translatableText("defaultMode").withStyle(ChatFormatting.GRAY), main2);
            onlyVanillaBiomes = createBooleanField("onlyVanillaBiomes", config.onlyVanillaBiomes(), CommentedConfig.DEFAULT.onlyVanillaBiomes(), main2, false, new Component[]{});

            backgroundBlock = createBlockField("bB", config.backGroundBlock().getBlock(), CommentedConfig.DEFAULT.backGroundBlock().getBlock(), main1, List.of(FT.NO_BLOCK_ENTITY, FT.NO_BUTTON));

            showUpdates = createBooleanField("showUpdates", config.showUpdates(), CommentedConfig.DEFAULT.showUpdates(), main1, false, new Component[]{});
            showBigUpdates = createBooleanField("showBigUpdates", config.showBigUpdates(), CommentedConfig.DEFAULT.showBigUpdates(), main1, false, new Component[]{});

            forceLargeBiomes = createBooleanField("forceLargeBiomes", config.forceLargeBiomes(), CommentedConfig.DEFAULT.forceLargeBiomes(), main1,false, new Component[]{});
            textListEntry(translatableText("forceLargeBiomes").withStyle(ChatFormatting.GRAY), main1);

            linkButtons(category1);
            linkButtons(category2);
            linkButtons(category3);

            //showWarning = createBooleanField("showWarning", config.showWarning, DEFAULT.showWarning, main1, false, new Component[0]);

        }


        public CommentedConfig createConfig() {
            return new CommentedConfig(currentMode.toString(), onlyVanillaBiomes.getValue(), forceLargeBiomes.getValue(), showUpdates.getValue(), showBigUpdates.getValue(), backgroundBlock.getValue().defaultBlockState());
        }




        private FloatListEntry createFloatField(String id, float value, float defaultValue, SubCategoryBuilder sub) {
            FloatListEntry entry = builder.startFloatField(fieldName(id), value)
                    .setDefaultValue(defaultValue)
                    .setMin(0).setMax(100)
                    .build();
            main(sub, entry);
            return entry;
        }

        private BooleanListEntry createBooleanField(String id, boolean value, boolean defaultValue, SubCategoryBuilder sub, boolean rr, Component[] tooltip) {
            BooleanToggleBuilder e = builder.startBooleanToggle(fieldName(id), value)
                    .setDefaultValue(defaultValue).setTooltip(tooltip);
            BooleanListEntry entry = rr ? e.requireRestart().build() : e.build();
            main(sub, entry);
            return entry;
        }

        private IntegerListEntry createIntField(String id, int value, int defaultValue, SubCategoryBuilder sub) {
            IntegerListEntry entry = builder.startIntField(fieldName(id), value)
                    .setDefaultValue(defaultValue)
                    .setMin(0).setMax(100)
                    .build();
            main(sub, entry);
            return entry;
        }

        private @NotNull DropdownBoxEntry<Block> createBlockField(String id, Block value, Block defaultValue, SubCategoryBuilder sub, List<FT> filter) {


            DropdownMenuBuilder<Block> e = builder.startDropdownMenu(fieldName(id), DropdownMenuBuilder.TopCellElementBuilder.ofBlockObject(value), DropdownMenuBuilder.CellCreatorBuilder.ofBlockObject())
                    .setDefaultValue(defaultValue)
                    .setSelections(BuiltInRegistries.BLOCK.stream().sorted(Comparator.comparing(Block::toString)).filter(new BlockPredicate(filter)).collect(Collectors.toCollection(LinkedHashSet::new)));

            @NotNull DropdownBoxEntry<Block> entry = e.build();

            main(sub, entry);
            return entry;
        }

        private void main(SubCategoryBuilder sub, AbstractConfigListEntry<?> entry){
            if(sub.getFieldNameKey().equals(Component.literal("main1"))){
                category1.addEntry(entry);
            }
            else if(sub.getFieldNameKey().equals(Component.literal("main2"))){
                category2.addEntry(entry);
            }
            else if(sub.getFieldNameKey().equals(Component.literal("main3"))){
                category3.addEntry(entry);
            }
            else {
                sub.add(entry);
            }
        }

        public void textListEntry(Component component, SubCategoryBuilder sub){
            TextListEntry tle = this.builder.startTextDescription(component).build();
            main(sub, tle);
        }

        private void addTexts(SubCategoryBuilder main1, SubCategoryBuilder main3){
            if(!isTerraBlenderLoaded()){
                textListEntry(Component.translatable(MODID + ".config.text.rqTB", Component.literal("Terrablender " + minTerraBlenderVersion).withStyle(ChatFormatting.BOLD), translatableText("orh")), main3);
                textListEntry(Component.translatable(MODID + ".config.text.downloadTB", translatableText("ch").withStyle(ChatFormatting.BOLD).withStyle((s) -> s.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/terrablender")))), main3);
            }
            textListEntry(translatableText("compatibleMode").withStyle(ChatFormatting.GRAY), main3);
            textListEntry(Component.translatable( MODID + ".config.text.comp1", translatableText("youcan").withStyle(ChatFormatting.GRAY), Component.literal(currentMode.toString()).withStyle(ChatFormatting.DARK_RED), translatableText("moreinfo").withStyle(ChatFormatting.GRAY), Component.literal(mainName("default").getString()).withStyle(ChatFormatting.DARK_PURPLE),
                    translatableText("or").withStyle(ChatFormatting.GRAY), Component.literal(mainName("compatible").getString()).withStyle(ChatFormatting.DARK_PURPLE)), main1);
        }

        private void linkButtons(ConfigCategory category){
            TextListEntry tle = this.builder.startTextDescription(Component.literal(" ")).build();
            category.addEntry(tle);
            category.addEntry(new LinkEntry(fieldName("dc"), buttonWidget -> Minecraft.getInstance().setScreen(new ConfirmLinkScreen(confirmed -> {
                if (confirmed) {
                    Util.getPlatform().openUri(LINK_DC);
                }
                Minecraft.getInstance().setScreen(new ClothConfigScreen().create(lastScreen)); }, LINK_DC, true)), new ResourceLocation(MODID, "textures/gui/dc.png"), 3));
            category.addEntry(tle);
            category.addEntry(new LinkEntry(fieldName("h"), buttonWidget -> Minecraft.getInstance().setScreen(new ConfirmLinkScreen(confirmed -> {
                if (confirmed) {
                    Util.getPlatform().openUri(LINK_H);
                }
                Minecraft.getInstance().setScreen(new ClothConfigScreen().create(lastScreen)); }, LINK_H, true)), new ResourceLocation(MODID, "textures/gui/cf.png"), 10));
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
