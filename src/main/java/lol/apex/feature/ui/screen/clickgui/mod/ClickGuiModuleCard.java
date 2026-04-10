package lol.apex.feature.ui.screen.clickgui.mod;

import imgui.ImGui;
import imgui.type.ImBoolean;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.ui.imgui.ImGuiFonts;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class ClickGuiModuleCard {
    private final ClickGuiSettingsRenderer settingsRenderer = new ClickGuiSettingsRenderer();

    public void render(Module module, float paneWidth) {
        ImGui.beginChild("ModulePane" + module.getName(), paneWidth, getHeight(module), true);
        renderHeader(module);
        ImGui.pushFont(ImGuiFonts.getFont("product-regular", 18));
        settingsRenderer.render(module);
        ImGui.popFont();
        ImGui.endChild();
    }

    public float getHeight(Module module) {
        float height = 0.0f;
        float itemSpacingY = ImGui.getStyle().getItemSpacingY();
        boolean hasVisibleSettings = hasVisibleSettings(module);

        height += Math.max(ImGui.calcTextSize(module.getName()).y, ImGui.getFrameHeight());
        height += itemSpacingY;
        height += 1.0f + itemSpacingY;

        if (module.isExpanded()) {
            for (var setting : module.getBaseSettings()) {
                if (setting.isHidden()) {
                    continue;
                }
                height += ImGui.getFrameHeight();
                height += itemSpacingY;
            }
        }

        height += ImGui.getStyle().getWindowPaddingY() * 2 - (module.isExpanded() && hasVisibleSettings ? 5 : 9);
        return height;
    }

    private void renderHeader(Module module) {
        ImGui.pushID(module.getName());
        ImGui.text(module.getName());

        if (ImGui.beginItemTooltip()) {
            ImGui.text(module.getDescription());
            if (module.getKey() > 0) {
                ImGui.separator();
                int key = module.getKey();
                int scanCode = GLFW.glfwGetKeyScancode(key);
                var translationKey = InputUtil.fromKeyCode(new KeyInput(key, scanCode, 0));
                ImGui.textDisabled("Keybind: " + translationKey.getLocalizedText().getString());
            }
            ImGui.endTooltip();
        }

        if (!module.getBaseSettings().isEmpty() && hasVisibleSettings(module)) {
            float arrowWidth = ImGui.getFrameHeight();
            float arrowX = ImGui.getCursorPosX() + ImGui.getContentRegionAvailX() - arrowWidth * 2 - 4;
            ImGui.sameLine(arrowX);
            if (ImGui.arrowButton("##arrow", module.isExpanded() ? 2 : 3)) {
                module.toggleExpanded();
            }
        }

        var enabled = new ImBoolean(module.isEnabled());
        float checkboxWidth = ImGui.getFrameHeight();
        float checkboxX = ImGui.getCursorPosX() + ImGui.getContentRegionAvailX() - checkboxWidth;
        ImGui.sameLine(checkboxX);
        if (ImGui.checkbox("##" + module.getName(), enabled)) {
            module.toggle();
        }

        if (module.isExpanded() && hasVisibleSettings(module)) {
            ImGui.separator();
        }
        ImGui.popID();
    }

    private boolean hasVisibleSettings(Module module) {
        for (var setting : module.getBaseSettings()) {
            if (!setting.isHidden()) {
                return true;
            }
        }
        return false;
    }
}
