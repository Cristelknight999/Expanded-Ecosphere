package de.cristelknight999.wwoo.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.cristelknight999.wwoo.WWOO;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.cristelknight999.wwoo.utils.Util.translatableText;

public class Updater {

    private Optional<Update> info;
    private final Version currentVersion;

    private boolean isBig = false;

    private int newUpdates;

    private static final String s = "[WWOO Updater]";

    public Updater(Version currentVersion) {
        this.currentVersion = currentVersion;
    }

    @SuppressWarnings("deprecation")
    public void checkForUpdates() {
            try {
                try (InputStream in = new URL("https://github.com/Cristelknight999/CristelknightUpdateChecker/releases/download/1.0/update.json").openStream()) {
                    String updateIndex;
                    try {
                        updateIndex = new JsonParser().parse(new InputStreamReader(in)).getAsJsonObject().get(getReleaseTarget()).getAsString();
                    } catch (NullPointerException e) {
                        WWOO.LOGGER.warn(s + " This version doesn't have an update index, skipping");
                        info = Optional.empty();
                        return;
                    }

                    JsonObject object = new JsonParser().parse(new InputStreamReader(new URL(updateIndex).openStream())).getAsJsonObject();
                    List<Update> newUpdate = new ArrayList<>();

                    for(JsonElement element : object.getAsJsonArray("versions")){
                        Update u = new Gson().fromJson(element, Update.class);
                        if(u.modDownloadFA.isEmpty()) continue;
                        if(currentVersion.compareTo(SemanticVersion.parse(u.semanticVersion)) < 0){
                            newUpdate.add(u);
                            if(u.isBig) isBig = true;
                        }
                    }

                    if(newUpdate.isEmpty()){
                        info = Optional.empty();
                        WWOO.LOGGER.info(s + " Found no updates");
                        return;
                    }

                    newUpdates = newUpdate.size();

                    String isBigString = isBig ? " important" : "";
                    WWOO.LOGGER.warn(s + " Found an" + isBigString + " update!");
                    info = newUpdate.stream().findFirst();
                    return;

                } catch (VersionParsingException e) {
                    throw new RuntimeException(e);
                }
            } catch(FileNotFoundException e) {
                WWOO.LOGGER.warn(s + " Unable to download " + e.getMessage());
            } catch (IOException e) {
                WWOO.LOGGER.warn(s + " Failed to get update info!", e);
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
        String string = isBig ? "wwoo.config.text.newUpdateBig" : "wwoo.config.text.newUpdate";

        Component component1 = Component.translatable("wwoo.config.text.newUpdates", newUpdates, newUpdates > 1 ? translatableText("multiple") : translatableText("null")).withStyle(ChatFormatting.GRAY);

        Component component = Component.translatable(string, translatableText("ch").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.UNDERLINE)
                .withStyle((s) -> s.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, update.modDownloadFA))), component1);

        return Optional.of(component);
    }

    public static String getReleaseTarget() {
        return SharedConstants.getCurrentVersion().isStable() ? SharedConstants.getCurrentVersion().getName() : WWOO.backupVersionNumber;
    }

}
