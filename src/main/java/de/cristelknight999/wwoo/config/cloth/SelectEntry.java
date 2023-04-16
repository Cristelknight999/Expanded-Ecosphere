package de.cristelknight999.wwoo.config.cloth;

import com.mojang.blaze3d.vertex.PoseStack;
import de.cristelknight999.wwoo.WWOO;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static de.cristelknight999.wwoo.WWOO.Mode.COMPATIBLE;
import static de.cristelknight999.wwoo.WWOO.Mode.DEFAULT;
import static de.cristelknight999.wwoo.WWOO.currentMode;
import static de.cristelknight999.wwoo.WWOO.selectedD;
import static de.cristelknight999.wwoo.terra.TerraInit.terraEnableDisable;

public class SelectEntry extends AbstractConfigListEntry<WWOO.Mode> {

    private static final int HEIGHT = 20;
    private static Button button = null;

    public SelectEntry(Button.OnPress onPress) {
        super(Component.literal("SelectEntry"), false);
        Component s = currentMode.equals(DEFAULT) ? selectedD : Component.literal(DEFAULT.toString());
        button = Button.builder(s, onPress).bounds(0, 0, 200, HEIGHT).build();
    }

    @Override
    public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        button.setX(x + (entryWidth - button.getWidth()) / 2);
        button.setY(y + (entryHeight - HEIGHT) / 2);
        button.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public WWOO.Mode getValue() { return currentMode; }

    @Override
    public Optional<WWOO.Mode> getDefaultValue() { return Optional.of(DEFAULT); }

    @Override
    public void save() {
        currentMode = button.getMessage().equals(selectedD) ? DEFAULT : COMPATIBLE;
        if(!FabricLoader.getInstance().isModLoaded("terrablender")) return;
        terraEnableDisable();
    }

    public static void setMessage(Component component){
        button.setMessage(component);
    }

    @Override
    public boolean isEdited() {
        return super.isEdited() || (currentMode != DEFAULT && button.getMessage().equals(selectedD));
    }


    private List<Button> children0() {
        return Collections.singletonList(button);
    }

    @Override
    public List<? extends GuiEventListener> children() { return children0(); }

    @Override
    public List<? extends NarratableEntry> narratables() { return children0(); }

    public static void onPress(){
        button.setMessage(selectedD);
        SelectEntry2.setMessage(Component.literal(COMPATIBLE.toString()));
    }
}