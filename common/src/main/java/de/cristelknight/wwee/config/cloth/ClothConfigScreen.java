package de.cristelknight.wwee.config.cloth;


import de.cristelknight.cristellib.CristelLib;
import de.cristelknight.cristellib.config.client.ScreenBuilder;
import de.cristelknight.wwee.ExpandedEcosphere;
import de.cristelknight.wwee.config.configs.EEConfig;
import de.cristelknight.wwee.config.configs.ReplaceBiomesConfig;
import de.cristelknight.wwee.terra.TerraInit;
import de.cristelknight.wwee.utils.BiomeReplace;
import de.cristelknight.wwee.utils.Util;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.entries.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.cristelknight.wwee.ExpandedEcosphere.MODID;
import static de.cristelknight.wwee.ExpandedEcosphere.Mode.DEFAULT;
import static de.cristelknight.wwee.ExpandedEcosphere.currentMode;


@Environment(value= EnvType.CLIENT)
public class ClothConfigScreen {


    public Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable(MODID + ".config.title").withStyle(ChatFormatting.BOLD));

        ConfigEntries entries = new ConfigEntries(builder.entryBuilder(), builder.getOrCreateCategory(mainName("main")), builder.getOrCreateCategory(mainName("biomes")), builder.getOrCreateCategory(mainName("modes")));
        new ScreenBuilder(MODID).addToBuilder(builder);
        builder.setSavingRunnable(() -> {
            EEConfig.DEFAULT.setInstance(entries.createConfig());
            EEConfig.DEFAULT.getConfig(true, true);

            ReplaceBiomesConfig.DEFAULT.setInstance(entries.createBiomesConfig());
            ReplaceBiomesConfig config2 = ReplaceBiomesConfig.DEFAULT.getConfig(true, true);

            if(ExpandedEcosphere.isTerraBlenderLoaded()) TerraInit.terraEnableDisable();
            if(config2.enableBiomes() && currentMode.equals(DEFAULT)) BiomeReplace.replace();
            else CristelLib.CONFIG_PACK.removeData(ResourceLocation.withDefaultNamespace("dimension/overworld.json"));
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
        private final BooleanListEntry removeOreBlobs, checkForUpdates, showUpdates, showBigUpdates, forceLargeBiomes, enableBiomes;
        private final EnumListEntry<ExpandedEcosphere.Mode> mode;
        private final StringListListEntry biomeList;

        public ConfigEntries(ConfigEntryBuilder builder, ConfigCategory category1, ConfigCategory category2, ConfigCategory category3) {
            this.builder = builder;

            EEConfig config = EEConfig.DEFAULT.getConfig();

            // Tab 3
            if(!ExpandedEcosphere.isTerraBlenderLoaded()){
                textListEntry(Component.translatable(MODID + ".config.text.requiresTerrablender", ExpandedEcosphere.minTerraBlenderVersion), category3);
                textListEntry(Component.translatable(MODID + ".config.text.downloadTB").withStyle((s) -> s.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/terrablender"))), category3);
            }
            mode = builder.startEnumSelector(fieldName("selectMode"), ExpandedEcosphere.Mode.class, currentMode).setDefaultValue(DEFAULT).setRequirement(Requirement.isTrue(ExpandedEcosphere::isTerraBlenderLoaded)).build();
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

            checkForUpdates = createBooleanField("checkForUpdates", config.checkForUpdates(), EEConfig.DEFAULT.checkForUpdates(), category1, new Component[]{});
            showUpdates = createBooleanField("showUpdates", config.showUpdates(), EEConfig.DEFAULT.showUpdates(), category1, new Component[]{});
            showBigUpdates = createBooleanField("showBigUpdates", config.showBigUpdates(), EEConfig.DEFAULT.showBigUpdates(), category1, new Component[]{});
            removeOreBlobs = createBooleanField("removeOreBlobs", config.removeOreBlobs(), EEConfig.DEFAULT.removeOreBlobs(), category1, new Component[]{fieldToolTip("removeOreBlobs")});
            forceLargeBiomes = createBooleanField("forceLargeBiomes", config.forceLargeBiomes(), EEConfig.DEFAULT.forceLargeBiomes(), category1, new Component[]{});

            textListEntry(Util.translatableText("forceLargeBiomes").withStyle(ChatFormatting.GRAY), category1);

        }



        public EEConfig createConfig() {
            ExpandedEcosphere.Mode currentMode = mode.getValue();
            ExpandedEcosphere.currentMode = currentMode;

            return new EEConfig(currentMode.toString(), forceLargeBiomes.getValue(), removeOreBlobs.getValue(), checkForUpdates.getValue(), showUpdates.getValue(), showBigUpdates.getValue()/*, backgroundBlock.getValue().defaultBlockState()*/);
        }

        public ReplaceBiomesConfig createBiomesConfig() {
            return new ReplaceBiomesConfig(enableBiomes.getValue(), convertListToMap(biomeList.getValue()));
        }
        private static List<String> convertMapToList(Map<String, String> stringMap) {
            return stringMap.entrySet().stream()
                    .map(entry -> entry.getKey() + "#" + entry.getValue())
                    .collect(Collectors.toList());
        }
        private static Map<String, String> convertListToMap(List<String> stringList) {
            return stringList.stream()
                    .map(s -> s.split("#"))
                    .filter(parts -> parts.length == 2)
                    .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
        }

        private BooleanListEntry createBooleanField(String id, boolean value, boolean defaultValue, ConfigCategory category, Component[] tooltip) {
            BooleanListEntry e = builder.startBooleanToggle(fieldName(id), value)
                    .setDefaultValue(defaultValue).setTooltip(tooltip).build();

            category.addEntry(e);
            return e;
        }

        public void textListEntry(Component component, ConfigCategory category){
            TextListEntry tle = this.builder.startTextDescription(component).build();
            category.addEntry(tle);
        }
    }
}
