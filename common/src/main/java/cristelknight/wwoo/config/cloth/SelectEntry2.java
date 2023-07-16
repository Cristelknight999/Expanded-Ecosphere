package cristelknight.wwoo.config.cloth;

import com.mojang.blaze3d.vertex.PoseStack;
import cristelknight.wwoo.WWOO;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static cristelknight.wwoo.WWOO.Mode.COMPATIBLE;
import static cristelknight.wwoo.WWOO.Mode.DEFAULT;

public class SelectEntry2 extends AbstractConfigListEntry<WWOO.Mode> {

    private static final int HEIGHT = 20;
    private static Button button = null;

    public SelectEntry2(Button.OnPress onPress) {
        super(Component.literal("SelectEntry2"), false);
        Component s = WWOO.currentMode.equals(COMPATIBLE) ? WWOO.selectedC : Component.literal(COMPATIBLE.toString());
        button = Button.builder(s, onPress).bounds(0, 0, 200, HEIGHT).build();
    }

    @Override
    public boolean isEdited() {
        return super.isEdited() || (WWOO.currentMode != COMPATIBLE && button.getMessage().equals(WWOO.selectedC));
    }

    @Override
    public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        button.setX(x + (entryWidth - button.getWidth()) / 2);
        button.setY(y + (entryHeight - HEIGHT) / 2);
        button.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public WWOO.Mode getValue() { return WWOO.currentMode; }

    public static void setMessage(Component component){
        button.setMessage(component);
    }

    @Override
    public Optional<WWOO.Mode> getDefaultValue() { return Optional.of(COMPATIBLE); }

    @Override
    public void save() {
    }


    private List<Button> children0() {
        return Collections.singletonList(button);
    }

    @Override
    public List<? extends GuiEventListener> children() { return children0(); }

    @Override
    public List<? extends NarratableEntry> narratables() { return children0(); }

    public static void onPress(){
        if(WWOO.isTerraBlenderLoaded()){
            button.setMessage(WWOO.selectedC);
            SelectEntry.setMessage(Component.literal(DEFAULT.toString()));
        }
        else {
            button.setMessage(Component.translatable(WWOO.MODID + ".config.text.mrqtb", Component.literal("Terrablender").withStyle(ChatFormatting.BOLD)));
        }

    }
}