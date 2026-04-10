package lol.apex.feature.ui.screen.clickgui.config;

import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import lol.apex.Apex;
import lol.apex.feature.file.impl.ModulesFile;
import lol.apex.feature.ui.Icons;
import lol.apex.feature.ui.imgui.ImGuiFonts;
import lol.apex.util.game.ChatUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class ClickGuiConfigsPanel {
    private final List<String> configs = new ArrayList<>();
    private boolean initialized;
    private int selectedConfigIndex = -1;
    private int renamingConfigIndex = -1;
    private final ImString renamingConfigName = new ImString(64);
    private boolean focusRenameInput;

    public void render(String normalizedSearchQuery) {
        ensureInitialized();

        ImGui.text("Available configs");

        float footerHeight = ImGui.getFrameHeight() + 8 + ImGui.getStyle().getItemSpacingY();
        ImGui.beginChild("ConfigList", 0, -footerHeight, true);
        boolean applyRename = false;
        String oldConfigName = null;
        String requestedConfigName = null;

        for (int i = 0; i < configs.size(); i++) {
            String configName = configs.get(i);
            if (!matchesSearch(configName, normalizedSearchQuery)) {
                continue;
            }

            if (i == renamingConfigIndex) {
                ImGui.pushID("rename-config-" + i);
                if (focusRenameInput) {
                    ImGui.setKeyboardFocusHere();
                    focusRenameInput = false;
                }

                boolean submitted = ImGui.inputText(
                        "##config-rename",
                        renamingConfigName,
                        ImGuiInputTextFlags.EnterReturnsTrue | ImGuiInputTextFlags.AutoSelectAll
                );
                if (submitted || ImGui.isItemDeactivatedAfterEdit()) {
                    applyRename = true;
                    oldConfigName = configName;
                    requestedConfigName = renamingConfigName.get();
                }
                ImGui.popID();
                continue;
            }

            if (ImGui.selectable(configName, i == selectedConfigIndex)) {
                selectedConfigIndex = i;
            }
            if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(0)) {
                startRename(i);
            }
        }

        ImGui.endChild();
        if (applyRename) {
            finishRename(oldConfigName, requestedConfigName);
        }

        renderActions();
    }

    private void renderActions() {
        float buttonSize = ImGui.getFrameHeight() + 6;
        renderActionButton(Icons.UPLOAD, buttonSize, "Load selected config", this::loadSelectedConfig);
        ImGui.sameLine();
        renderActionButton(Icons.SAVE, buttonSize, "Save selected config", this::saveSelectedConfig);
        ImGui.sameLine();
        renderActionButton(Icons.ADD_CIRCLE, buttonSize, "Create new config", this::createConfig);
        ImGui.sameLine();
        renderActionButton(Icons.TRASH, buttonSize, "Delete selected config", this::deleteSelectedConfig);
        ImGui.sameLine();
        renderActionButton(Icons.REFRESH, buttonSize, "Refresh config list", this::refreshConfigs);
    }

    private void renderActionButton(String icon, float buttonSize, String tooltip, Runnable action) {
        ImGui.pushFont(ImGuiFonts.getFont("icomoon", 18));
        if (ImGui.button(icon, buttonSize + 2, buttonSize)) {
            action.run();
        }
        ImGui.popFont();

        if (ImGui.isItemHovered()) {
            ImGui.setTooltip(tooltip);
        }
    }

    private void loadSelectedConfig() {
        String selected = getSelectedConfigName();
        if (selected == null) {
            ChatUtil.sendCommandErrorMessage("Select a config to load.");
            return;
        }

        new ModulesFile(selected).loadFromFile();
        Apex.sendChatMessage("Loaded config " + selected + ".");
    }

    private void saveSelectedConfig() {
        String selected = getSelectedConfigName();
        if (selected == null) {
            ChatUtil.sendCommandErrorMessage("Select a config to save.");
            return;
        }

        new ModulesFile(selected).saveToFile();
        Apex.sendChatMessage("Saved config " + selected + ".");
        refreshConfigs();
    }

    private void createConfig() {
        String newName = getNextConfigName();
        new ModulesFile(newName).saveToFile();
        Apex.sendChatMessage("Created config " + newName + ".");
        refreshConfigs();
        selectedConfigIndex = configs.indexOf(newName);
    }

    private void deleteSelectedConfig() {
        String selected = getSelectedConfigName();
        if (selected == null) {
            ChatUtil.sendCommandErrorMessage("Select a config to delete.");
            return;
        }

        if (deleteConfig(selected)) {
            Apex.sendChatMessage("Deleted config " + selected + ".");
            refreshConfigs();
            return;
        }

        ChatUtil.sendCommandErrorMessage("Failed to delete config " + selected + ".");
    }

    private void ensureInitialized() {
        if (!initialized) {
            refreshConfigs();
            initialized = true;
        }
    }

    private void refreshConfigs() {
        String previousSelection = getSelectedConfigName();
        File dir = ModulesFile.BASE_DIR.resolve("Configs").toFile();
        File[] files = dir.listFiles((ignored, name) -> name.endsWith(".json"));

        configs.clear();
        if (files != null) {
            Arrays.sort(files, (left, right) -> left.getName().compareToIgnoreCase(right.getName()));
            for (File file : files) {
                configs.add(file.getName().replace(".json", ""));
            }
        }

        if (previousSelection != null) {
            selectedConfigIndex = configs.indexOf(previousSelection);
        }
        if (selectedConfigIndex < 0 && !configs.isEmpty()) {
            selectedConfigIndex = 0;
        }
        if (renamingConfigIndex < 0 || renamingConfigIndex >= configs.size()) {
            renamingConfigIndex = -1;
            focusRenameInput = false;
        }
    }

    private String getSelectedConfigName() {
        if (selectedConfigIndex < 0 || selectedConfigIndex >= configs.size()) {
            return null;
        }
        return configs.get(selectedConfigIndex);
    }

    private boolean deleteConfig(String name) {
        File dir = ModulesFile.BASE_DIR.resolve("Configs").toFile();
        File file = new File(dir, name + ".json");
        return file.exists() && file.delete();
    }

    private String getNextConfigName() {
        int index = 1;
        String name;
        do {
            name = "config-" + index++;
        } while (configs.contains(name));
        return name;
    }

    private void startRename(int index) {
        if (index < 0 || index >= configs.size()) {
            return;
        }

        renamingConfigIndex = index;
        selectedConfigIndex = index;
        renamingConfigName.set(configs.get(index));
        focusRenameInput = true;
    }

    private void finishRename(String oldName, String rawNewName) {
        renamingConfigIndex = -1;
        focusRenameInput = false;

        if (oldName == null) {
            return;
        }

        String newName = normalizeConfigName(rawNewName);
        if (newName.isEmpty()) {
            ChatUtil.sendCommandErrorMessage("Config name cannot be empty.");
            return;
        }
        if (newName.contains("/") || newName.contains("\\")) {
            ChatUtil.sendCommandErrorMessage("Config name cannot contain path separators.");
            return;
        }
        if (newName.equals(oldName)) {
            return;
        }
        if (configs.contains(newName)) {
            ChatUtil.sendCommandErrorMessage("Config " + newName + " already exists.");
            return;
        }

        if (renameConfig(oldName, newName)) {
            Apex.sendChatMessage("Renamed config " + oldName + " to " + newName + ".");
            refreshConfigs();
            selectedConfigIndex = configs.indexOf(newName);
            return;
        }

        ChatUtil.sendCommandErrorMessage("Failed to rename config " + oldName + ".");
    }

    private String normalizeConfigName(String name) {
        if (name == null) {
            return "";
        }

        String normalized = name.trim();
        if (normalized.endsWith(".json")) {
            normalized = normalized.substring(0, normalized.length() - 5);
        }
        return normalized;
    }

    private boolean renameConfig(String oldName, String newName) {
        File dir = ModulesFile.BASE_DIR.resolve("Configs").toFile();
        File oldFile = new File(dir, oldName + ".json");
        File newFile = new File(dir, newName + ".json");
        return oldFile.exists() && !newFile.exists() && oldFile.renameTo(newFile);
    }

    private boolean matchesSearch(String configName, String normalizedSearchQuery) {
        if (normalizedSearchQuery.isEmpty()) {
            return true;
        }

        return configName != null && configName.toLowerCase(Locale.ROOT).contains(normalizedSearchQuery);
    }
}
