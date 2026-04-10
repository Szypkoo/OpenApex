package lol.apex.feature.ui.screen;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import lol.apex.Apex;
import lol.apex.feature.module.implementation.visual.ClickGuiModule;
import lol.apex.feature.ui.imgui.ImGuiScreen;
import lol.apex.feature.ui.screen.clickgui.*;
import lol.apex.feature.ui.screen.clickgui.config.ClickGuiConfigsPanel;
import lol.apex.feature.ui.screen.clickgui.data.ClickGuiSection;
import lol.apex.feature.ui.screen.clickgui.data.ClickGuiState;
import lol.apex.feature.ui.screen.clickgui.mod.ClickGuiModuleGrid;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

public class ImGuiClickGui extends ImGuiScreen {
    private static final float SIDEBAR_WIDTH = 150.0f;

    private final ClickGuiState state = new ClickGuiState();
    private final ClickGuiSearchBar searchBar = new ClickGuiSearchBar();
    private final ClickGuiSidebar sidebar = new ClickGuiSidebar();
    private final ClickGuiModuleGrid moduleGrid = new ClickGuiModuleGrid();
    private final ClickGuiConfigsPanel configsPanel = new ClickGuiConfigsPanel();
    private final ClickGuiSettingsPanel settingsPanel = new ClickGuiSettingsPanel();
    private final ClickGuiWaypointsPanel waypointsPanel = new ClickGuiWaypointsPanel();
    private ClickGuiSection lastSection = ClickGuiSection.COMBAT;

    public ImGuiClickGui() {
        super(Text.empty());
    }

    @Override
    public void init() {
        super.init();
        state.onInit();
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (state.handleKeyPressed(input, this::close)) {
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void close() {
        super.close();
        Apex.moduleManager.getByClass(ClickGuiModule.class).enabledNoNoise(false);
    }

    @Override
    public void renderScreen(ImGuiIO io) {
        if (ImGui.begin(Apex.getName() + " @ " + Apex.WEBSITE, ImGuiWindowFlags.NoCollapse)) {
            ImGui.setWindowSize(1074, 672, ImGuiCond.Once);
            state.updateSearchFocusFromMouse();

            searchBar.render(state);
            ImGui.separator();

            ImGui.beginChild("Sidebar", SIDEBAR_WIDTH, 0, true);
            sidebar.render(state, ImGui.getContentRegionAvailX());
            ImGui.endChild();

            ImGui.sameLine();

            ImGui.beginChild("MainContent", 0, 0, true);

            var selected = state.getSelectedSection();
            if (selected != ClickGuiSection.HUD_EDITOR) {
                lastSection = selected;
            }

            switch (selected) {
                case CONFIGS -> configsPanel.render(state.getNormalizedSearchQuery());
                case SETTINGS -> settingsPanel.render();
                case WAYPOINTS -> waypointsPanel.render(state.getNormalizedSearchQuery());
                case HUD_EDITOR -> {
                    client.setScreen(new ImGuiHudEditorScreen(this));
                    state.setSelectedSection(lastSection);
                }
                default -> moduleGrid.render(state.getSelectedSection(), state.getNormalizedSearchQuery());
            }
            ImGui.endChild();
        }
        ImGui.end();
    }
}
