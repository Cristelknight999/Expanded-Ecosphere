package de.cristelknight.wwee.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.cristelknight.cristellib.CristelLibExpectPlatform;
import de.cristelknight.cristellib.util.Platform;
import de.cristelknight.wwee.EEExpectPlatform;
import de.cristelknight.wwee.ExpandedEcosphere;
import de.cristelknight.wwee.config.configs.EEConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Updater {

    private Optional<Update> info;
    private final String currentVersion;

    private boolean isBig = false;

    private int newUpdates;

    private static final String s = "[Expanded Ecosphere Updater]";

    public Updater(String currentVersion) {
        this.currentVersion = currentVersion;
    }


    public void checkForUpdates() {
        EEConfig config = EEConfig.DEFAULT.getConfig();
        if(!config.checkForUpdates()) return;


        try (InputStream in = URI.create("https://github.com/Cristelknight999/CristelknightUpdateChecker/releases/download/1.0/update.json").toURL().openStream()) {
            String updateIndex;
            try {
                updateIndex = JsonParser.parseReader(new InputStreamReader(in)).getAsJsonObject().get(getReleaseTarget()).getAsString();
            } catch (NullPointerException e) {
                ExpandedEcosphere.LOGGER.warn(s + " This version doesn't have an update index, skipping");
                info = Optional.empty();
                return;
            }

            JsonObject object = JsonParser.parseReader(new InputStreamReader(URI.create(updateIndex).toURL().openStream())).getAsJsonObject();
            List<Update> newUpdate = new ArrayList<>();

            boolean isForge = CristelLibExpectPlatform.getPlatform().equals(Platform.FORGE);

            for(JsonElement element : object.getAsJsonArray("versions")){
                Update u = new Gson().fromJson(element, Update.class);

                if(isForge && u.modDownloadFO.isEmpty()) continue;
                if(!isForge && u.modDownloadFA.isEmpty()) continue;

                if(EEExpectPlatform.isNewer(currentVersion, u.semanticVersion)){
                    newUpdate.add(u);
                    if(u.isBig) isBig = true;
                }
            }

            if(newUpdate.isEmpty()){
                info = Optional.empty();
                ExpandedEcosphere.LOGGER.info(s + " Found no updates");
                return;
            }

            newUpdates = newUpdate.size();

            String isBigString = isBig ? " important" : "";
            ExpandedEcosphere.LOGGER.warn(s + " Found an{} update!", isBigString);
            info = newUpdate.stream().findFirst();
            return;

        }
        catch(FileNotFoundException e) {
            ExpandedEcosphere.LOGGER.warn(s + " Unable to download {}", e.getMessage());
        } catch (IOException e) {
            ExpandedEcosphere.LOGGER.warn(s + " Failed to get update info!", e);
        }

        info = Optional.empty();
    }

    public boolean isBig(){
        return isBig;
    }


    public Optional<Component> getUpdateMessage() {
        Update update = info.orElse(null);

        if (update == null) {
            return Optional.empty();
        }
        String string = isBig ? "expanded_ecosphere.config.text.newUpdateBig" : "expanded_ecosphere.config.text.newUpdate";

        Component component1 = Component.translatable("expanded_ecosphere.config.text.newUpdates", newUpdates, newUpdates > 1 ? Util.translatableText("multiple") : Util.translatableText("null")).withStyle(ChatFormatting.GRAY);

        boolean isForge = CristelLibExpectPlatform.getPlatform().equals(Platform.FORGE);

        Component component = Component.translatable(string, Util.translatableText("ch").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.UNDERLINE)
                .withStyle((s) -> s.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, isForge ? update.modDownloadFO : update.modDownloadFA))), component1);

        return Optional.of(component);
    }

    public static String getReleaseTarget() {
        return SharedConstants.getCurrentVersion().isStable() ? SharedConstants.getCurrentVersion().getName() : ExpandedEcosphere.backupVersionNumber;
    }

}
