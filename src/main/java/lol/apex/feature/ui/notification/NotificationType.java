package lol.apex.feature.ui.notification;

import lol.apex.feature.ui.Icons;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum NotificationType {
    INFORMATION("Info", Icons.INFO_CIRCLE),
    WARNING("Warning", Icons.WARNING),
    SUCCESS("Success", Icons.CHECK_CIRCLE),
    ERROR("Error", Icons.ERROR_CIRLCE);

    private final String alias;
    public final String icon;

    @Override
    public String toString() {
        return alias;
    }
}