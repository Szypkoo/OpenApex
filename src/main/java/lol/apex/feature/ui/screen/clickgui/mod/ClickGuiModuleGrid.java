package lol.apex.feature.ui.screen.clickgui.mod;

import imgui.ImGui;
import lol.apex.Apex;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.ui.screen.clickgui.data.ClickGuiSection;

import java.util.ArrayList;
import java.util.List;

public final class ClickGuiModuleGrid {
    private final ClickGuiModuleCard moduleCard = new ClickGuiModuleCard();

    public void render(ClickGuiSection section, String normalizedSearchQuery) {
        if (!section.isCategory()) {
            return;
        }

        float spacing = ImGui.getStyle().getItemSpacingX();
        float availableWidth = ImGui.getContentRegionAvailX();
        float minPaneWidth = 250.0f;
        int columns = Math.max(1, (int) Math.floor((availableWidth + spacing) / (minPaneWidth + spacing)));
        float paneWidth = (availableWidth - (spacing * (columns - 1))) / columns;
        List<Module> modules = getModules(section, normalizedSearchQuery);
        List<List<Module>> columnModules = new ArrayList<>(columns);
        float[] columnHeights = new float[columns];
        float itemSpacingY = ImGui.getStyle().getItemSpacingY();

        for (int i = 0; i < columns; i++) {
            columnModules.add(new ArrayList<>());
        }

        for (Module module : modules) {
            float moduleHeight = moduleCard.getHeight(module);
            int targetColumn = 0;
            for (int i = 1; i < columns; i++) {
                if (columnHeights[i] < columnHeights[targetColumn]) {
                    targetColumn = i;
                }
            }

            columnModules.get(targetColumn).add(module);
            columnHeights[targetColumn] += moduleHeight + itemSpacingY;
        }

        for (int column = 0; column < columns; column++) {
            ImGui.beginGroup();
            for (Module module : columnModules.get(column)) {
                moduleCard.render(module, paneWidth);
            }
            ImGui.endGroup();

            if (column + 1 < columns) {
                ImGui.sameLine();
            }
        }
    }

    private List<Module> getModules(ClickGuiSection section, String normalizedSearchQuery) {
        var stream = normalizedSearchQuery.isEmpty()
                ? Apex.moduleManager.getModulesByCategory(section.toString()).stream()
                : Apex.moduleManager.stream();

        return stream.filter(module -> moduleMatchesSearch(module, normalizedSearchQuery)).toList();
    }

    private boolean moduleMatchesSearch(Module module, String normalizedQuery) {
        if (normalizedQuery.isEmpty()) {
            return true;
        }

        String name = module.getName() == null ? "" : module.getName().toLowerCase();
        String description = module.getDescription() == null ? "" : module.getDescription().toLowerCase();
        return name.contains(normalizedQuery) || description.contains(normalizedQuery);
    }
}
