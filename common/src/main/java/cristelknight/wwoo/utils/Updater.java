package cristelknight.wwoo.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cristelknight.wwoo.WWOO;
import cristelknight.wwoo.WWOOExpectPlatform;
import net.cristellib.CristelLibExpectPlatform;
import net.cristellib.util.Platform;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
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


public class Updater {

    private Optional<Update> info;
    private final String currentVersion;

    private boolean isBig = false;

    private int newUpdates;

    private static final String s = "[WWOO Updater]";

    public Updater(String currentVersion) {
        this.currentVersion = currentVersion;
    }


    public void checkForUpdates() {
        try (InputStream in = new URL("https://github.com/Cristelknight999/CristelknightUpdateChecker/releases/download/1.0/update.json").openStream()) {
            String updateIndex;
            try {
                updateIndex = JsonParser.parseReader(new InputStreamReader(in)).getAsJsonObject().get(getReleaseTarget()).getAsString();
            } catch (NullPointerException e) {
                WWOO.LOGGER.warn(s + " This version doesn't have an update index, skipping");
                info = Optional.empty();
                return;
            }

            JsonObject object = JsonParser.parseReader(new InputStreamReader(new URL(updateIndex).openStream())).getAsJsonObject();
            List<Update> newUpdate = new ArrayList<>();

            for(JsonElement element : object.getAsJsonArray("versions")){
                Update u = new Gson().fromJson(element, Update.class);

                boolean isForge = CristelLibExpectPlatform.getPlatform().equals(Platform.FORGE);

                if(isForge && u.modDownloadFO.isEmpty()) continue;
                if(!isForge && u.modDownloadFA.isEmpty()) continue;

                if(WWOOExpectPlatform.isNewer(currentVersion, u.semanticVersion)){
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

        }
        catch(FileNotFoundException e) {
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

        Component component1 = Component.translatable("wwoo.config.text.newUpdates", newUpdates, newUpdates > 1 ? Util.translatableText("multiple") : Util.translatableText("null")).withStyle(ChatFormatting.GRAY);

        Component component = Component.translatable(string, Util.translatableText("ch").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.UNDERLINE)
                .withStyle((s) -> s.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, update.modDownloadFA))), component1);

        return Optional.of(component);
    }

    public static String getReleaseTarget() {
        return SharedConstants.getCurrentVersion().isStable() ? SharedConstants.getCurrentVersion().getName() : WWOO.backupVersionNumber;
    }

}
