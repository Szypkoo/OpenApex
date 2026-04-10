package lol.apex.feature.ui.screen.clickgui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import lol.apex.Apex;
import lol.apex.feature.module.implementation.visual.ClickGuiModule;
import lol.apex.feature.ui.imgui.ImGuiComponents;
import lol.apex.feature.ui.imgui.ImGuiThemes;

public final class ClickGuiSettingsPanel {
    public void render() {
        ImGui.text("Menu themes");
        ImGui.beginChild("ThemeList", 0, 0, true);
        renderImGuiThemeGrid();
        ImGui.endChild();
    }

    private void renderImGuiThemeGrid() {
        final var clickGuiModule = Apex.moduleManager.getByClass(ClickGuiModule.class);
        int textColor = ImGui.getColorU32(ImGuiCol.Text);
        int bgColor = ImGui.getColorU32(ImGuiCol.Button);
        int bgHovered = ImGui.getColorU32(ImGuiCol.ButtonHovered);
        float buttonWidth = 125;
        float buttonHeight = 40;
        float spacing = ImGui.getStyle().getItemSpacingX();
        float availableWidth = ImGui.getContentRegionAvailX();
        float currentX = 0;

        for (ImGuiThemes.Theme theme : ImGuiThemes.Theme.values()) {
            if (currentX + buttonWidth > availableWidth) {
                currentX = 0;
            } else if (currentX > 0) {
                ImGui.sameLine();
            }

            String label = clickGuiModule != null && clickGuiModule.imguiTheme.getValue() == theme
                    ? theme + "##selected"
                    : theme.toString();

            if (ImGuiComponents.coloredButton(label, new ImVec2(buttonWidth, buttonHeight), textColor, bgColor, bgHovered)) {
                ImGuiThemes.apply(theme);
                if (clickGuiModule != null) {
                    clickGuiModule.imguiTheme.setValue(theme);
                }
            }

            currentX += buttonWidth + spacing;
        }
    }
}
