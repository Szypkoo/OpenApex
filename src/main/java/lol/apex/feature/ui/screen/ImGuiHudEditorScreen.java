package lol.apex.feature.ui.screen;

import imgui.ImGuiIO;
import lol.apex.feature.ui.imgui.ImGuiScreen;
import lol.apex.feature.ui.screen.clickgui.editor.CustomHudEditor;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ImGuiHudEditorScreen extends ImGuiScreen {
    private final ImGuiClickGui parent;
    private final CustomHudEditor editor = new CustomHudEditor();

    public ImGuiHudEditorScreen(ImGuiClickGui parent) {
        super(Text.empty());
        this.parent = parent;
        editor.open();
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void close() {
        super.close();
        client.setScreen(parent);
    }

    @Override
    public void renderScreen(ImGuiIO io) {
        if (!editor.render(io)) {
            close();
        }
    }
}
