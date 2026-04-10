package lol.apex.feature.ui.screen.clickgui;

import imgui.ImGui;
import lol.apex.feature.ui.Icons;
import lol.apex.feature.ui.imgui.ImGuiFonts;
import lol.apex.feature.ui.screen.clickgui.data.ClickGuiState;

public final class ClickGuiSearchBar {
    public void render(ClickGuiState state) {
        ImGui.pushFont(ImGuiFonts.getFont("icomoon", 18));
        ImGui.text(Icons.SEARCH);
        ImGui.popFont();

        ImGui.sameLine();
        ImGui.setNextItemWidth(-1.0f);
        if (state.shouldRedirectKeyboardToSearch()) {
            ImGui.setKeyboardFocusHere();
        }
        if (ImGui.inputText("##clickgui-search", state.getSearchQuery()) && !state.getNormalizedSearchQuery().isEmpty()) {
            state.stopRedirectingKeyboardToSearch();
        }
        if (ImGui.isItemDeactivatedAfterEdit()) {
            state.stopRedirectingKeyboardToSearch();
        }
    }
}
