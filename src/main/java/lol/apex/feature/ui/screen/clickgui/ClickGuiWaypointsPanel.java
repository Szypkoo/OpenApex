package lol.apex.feature.ui.screen.clickgui;

import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import lol.apex.Apex;
import lol.apex.feature.waypoint.Waypoint;
import lol.apex.manager.implementation.WaypointManager;
import lol.apex.util.CommonVars;

import java.util.List;

public final class ClickGuiWaypointsPanel implements CommonVars {
    private final ImString renameMaxLength = new ImString(64);
    private int renamingIndex = -1;
    private boolean focusRenameInput = false;

    public void render(String searchQuery) {
        ImGui.text("Apex Waypoints");

        float footerHeight = ImGui.getFrameHeight() + 8 + ImGui.getStyle().getItemSpacingY();
        ImGui.beginChild("WaypointsList", 0, -footerHeight, true);

        List<Waypoint> waypoints = WaypointManager.getWaypoints();
        boolean applyRename = false;
        String oldName = null;
        String newName = null;

        for (int i = 0; i < waypoints.size(); i++) {
            Waypoint wp = waypoints.get(i);

            if (!searchQuery.isEmpty() && !wp.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                continue;
            }

            ImGui.beginChild("Waypoint_" + i, 0, 30, false);

            if (i == renamingIndex) {
                if (focusRenameInput) {
                    ImGui.setKeyboardFocusHere();
                    focusRenameInput = false;
                }
                if (ImGui.inputText("##rename", renameMaxLength, ImGuiInputTextFlags.EnterReturnsTrue | ImGuiInputTextFlags.AutoSelectAll)
                        || ImGui.isItemDeactivatedAfterEdit()) {
                    applyRename = true;
                    oldName = wp.getName();
                    newName = renameMaxLength.get();
                }
            } else {
                ImGui.text(wp.getName());
            }

            ImGui.sameLine();
            ImBoolean visible = new ImBoolean(wp.isVisible());
            if (ImGui.checkbox("Visible##" + i, visible)) {
                wp.setVisible(visible.get());
            }

            ImGui.sameLine();
            if (ImGui.button("Rename##" + i)) {
                startRename(i);
            }

            ImGui.sameLine();
            if (ImGui.button("Remove##" + i)) {
                WaypointManager.remove(wp);
                i--;
            }

            ImGui.endChild();
        }

        ImGui.endChild();

        if (applyRename) {
            finishRename(oldName, newName);
        }

        ImGui.separator();
        if (ImGui.button("Add Waypoint")) {
            WaypointManager.add(new Waypoint(
                    "Waypoint" + (waypoints.size() + 1),
                    (int) mc.player.getX(),
                    (int) mc.player.getY(),
                    (int) mc.player.getZ(),
                    true
            ));
        }
    }

    private void startRename(int index) {
        renamingIndex = index;
        renameMaxLength.set(WaypointManager.getWaypoints().get(index).getName());
        focusRenameInput = true;
    }

    private void finishRename(String oldName, String rawNewName) {
        renamingIndex = -1;
        focusRenameInput = false;

        if (oldName == null) return;

        String newName = rawNewName.trim();
        if (newName.isEmpty()) {
            Apex.sendChatMessage("Waypoint name cannot be empty!");
            return;
        }

        for (Waypoint wp : WaypointManager.getWaypoints()) {
            if (wp.getName().equalsIgnoreCase(newName)) {
                Apex.sendChatMessage("A waypoint with that name already exists!");
                return;
            }
        }

        for (Waypoint wp : WaypointManager.getWaypoints()) {
            if (wp.getName().equals(oldName)) {
                wp.setName(newName);
                Apex.sendChatMessage("Renamed waypoint '" + oldName + "' to '" + newName + "'.");
                break;
            }
        }
    }
}
