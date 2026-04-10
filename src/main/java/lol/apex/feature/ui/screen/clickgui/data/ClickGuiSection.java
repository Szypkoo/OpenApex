package lol.apex.feature.ui.screen.clickgui.data;

import lol.apex.feature.module.base.Category;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ClickGuiSection {
    COMBAT("Combat", Category.COMBAT, false),
    MOVEMENT("Movement", Category.MOVEMENT, false),
    PLAYER("Player", Category.PLAYER, false),
    VISUAL("Visual", Category.VISUAL, false),
    LEGIT("Legit", Category.LEGIT, false),
    OTHER("Other", Category.OTHER, false),
    WAYPOINTS("Waypoints", null, false),
    FRIENDS("Friends", null, false),
    CONFIGS("Configs", null, false),
    SETTINGS("Settings", null, false),
    HUD_EDITOR("HUD Editor", null, true);

    private final String label;
    private final Category category;
    private final boolean fromBottom;

    public Category getCategory() {
        return category;
    }

    public boolean isCategory() {
        return category != null;
    }

    public boolean isFromBottom() {
        return fromBottom;
    }

    @Override
    public String toString() {
        return label;
    }
}
