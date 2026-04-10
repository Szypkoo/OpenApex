package lol.apex.feature.ui.screen.clickgui;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import lol.apex.feature.ui.screen.clickgui.data.ClickGuiSection;
import lol.apex.feature.ui.screen.clickgui.data.ClickGuiState;

public final class ClickGuiSidebar {
    public void render(ClickGuiState state, float fullWidth) {
        for (ClickGuiSection section : ClickGuiSection.values()) {
            if (section.isFromBottom()) continue;

            if (section == ClickGuiSection.WAYPOINTS) {
                ImGui.separator();
            }

            if (section == ClickGuiSection.CONFIGS) {
                ImGui.separator();
            }

            renderSectionButton(state, section, fullWidth);
        }

        int bottomSections = 0;
        for (ClickGuiSection section : ClickGuiSection.values()) {
            if (section.isFromBottom()) bottomSections++;
        }

        float buttonHeight = ImGui.getFrameHeight();
        float itemSpacingY = ImGui.getStyle().getItemSpacingY();
        float footerHeight = (buttonHeight + itemSpacingY) * bottomSections + itemSpacingY;
        float remainingHeight = ImGui.getContentRegionAvailY();
        if (remainingHeight > footerHeight) {
            ImGui.dummy(0.0f, remainingHeight - footerHeight);
        }

        boolean firstBottom = true;
        for (ClickGuiSection section : ClickGuiSection.values()) {
            if (!section.isFromBottom()) continue;

            if (firstBottom) {
                ImGui.separator();
                firstBottom = false;
            }
            renderSectionButton(state, section, fullWidth);
        }
    }

    private void renderSectionButton(ClickGuiState state, ClickGuiSection section, float width) {
        boolean selected = state.getSelectedSection() == section;
        if (selected) {
            ImGui.pushStyleColor(ImGuiCol.Button, ImGui.getStyleColorVec4(ImGuiCol.Button).plus(0.15f, 0.15f, 0.15f, 0.0f));
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, ImGui.getStyleColorVec4(ImGuiCol.ButtonHovered).plus(0.15f, 0.15f, 0.15f, 0.0f));
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, ImGui.getStyleColorVec4(ImGuiCol.ButtonActive).plus(0.15f, 0.15f, 0.15f, 0.0f));
        }

        if (ImGui.button(section.toString(), width, 0)) {
            state.setSelectedSection(section);
        }

        if (selected) {
            ImGui.popStyleColor(3);
        }
    }
}
