package lol.apex.feature.ui.imgui;

import imgui.ImGui;
import imgui.ImGuiIO;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public abstract class ImGuiScreen extends Screen implements RenderInterface {
    protected ImGuiScreen(Text title) {
        super(title);
    }

    @Override
    public void init() {
        super.init();
        ImGuiImpl.inputEnabled = true;
    }

    @Override
    public void close() {
        super.close();
        ImGuiImpl.inputEnabled = false;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (ImGuiImpl.inputEnabled) {
            ImGuiImpl.mouseButtonCallback(client.getWindow().getHandle(), click.button(), GLFW.GLFW_PRESS, 0);
            if (ImGui.getIO().getWantCaptureMouse()) {
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (ImGuiImpl.inputEnabled) {
            ImGuiImpl.mouseButtonCallback(client.getWindow().getHandle(), click.button(), GLFW.GLFW_RELEASE, 0);
            if (ImGui.getIO().getWantCaptureMouse()) {
                return true;
            }
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (ImGuiImpl.inputEnabled) {
            ImGuiImpl.scrollCallback(client.getWindow().getHandle(), horizontalAmount, verticalAmount);
            if (ImGui.getIO().getWantCaptureMouse()) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (ImGuiImpl.inputEnabled) {
            ImGuiImpl.cursorPosCallback(client.getWindow().getHandle(), mouseX, mouseY);
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (ImGuiImpl.inputEnabled) {
            ImGuiImpl.keyCallback(client.getWindow().getHandle(), input.key(), input.scancode(), GLFW.GLFW_PRESS, input.modifiers());
            if (ImGui.getIO().getWantCaptureKeyboard()) {
                return true;
            }
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        if (ImGuiImpl.inputEnabled) {
            ImGuiImpl.keyCallback(client.getWindow().getHandle(), input.key(), input.scancode(), GLFW.GLFW_RELEASE, input.modifiers());
            if (ImGui.getIO().getWantCaptureKeyboard()) {
                return true;
            }
        }
        return super.keyReleased(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (ImGuiImpl.inputEnabled) {
            ImGuiImpl.charCallback(client.getWindow().getHandle(), input.codepoint());
            if (ImGui.getIO().getWantCaptureKeyboard()) {
                return true;
            }
        }
        return super.charTyped(input);
    }

    @Override
    public final void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public final void render(ImGuiIO io) {
        ImGuiThemes.apply();
        renderScreen(io);
    }

    public abstract void renderScreen(ImGuiIO io);

    @Override
    public boolean shouldPause() {
        return false;
    }
}