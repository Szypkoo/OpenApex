package lol.apex.feature.ui.screen.clickgui.data;

import imgui.ImGui;
import imgui.type.ImString;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

public final class ClickGuiState {
    private ClickGuiSection selectedSection = ClickGuiSection.COMBAT;
    private final ImString searchQuery = new ImString(128);
    private boolean redirectKeyboardToSearch = true;

    public void onInit() {
        redirectKeyboardToSearch = true;
    }

    public boolean handleKeyPressed(KeyInput input, Runnable closeAction) {
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
            closeAction.run();
            return true;
        }
        if (input.key() == GLFW.GLFW_KEY_ENTER || input.key() == GLFW.GLFW_KEY_KP_ENTER) {
            redirectKeyboardToSearch = false;
        }
        return false;
    }

    public void updateSearchFocusFromMouse() {
        if (redirectKeyboardToSearch && (ImGui.isMouseClicked(0) || ImGui.isMouseClicked(1) || ImGui.isMouseClicked(2))) {
            redirectKeyboardToSearch = false;
        }
    }

    public ClickGuiSection getSelectedSection() {
        return selectedSection;
    }

    public void setSelectedSection(ClickGuiSection selectedSection) {
        this.selectedSection = selectedSection;
    }

    public ImString getSearchQuery() {
        return searchQuery;
    }

    public boolean shouldRedirectKeyboardToSearch() {
        return redirectKeyboardToSearch;
    }

    public void stopRedirectingKeyboardToSearch() {
        redirectKeyboardToSearch = false;
    }

    public String getNormalizedSearchQuery() {
        String query = searchQuery.get();
        return query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
    }
}
