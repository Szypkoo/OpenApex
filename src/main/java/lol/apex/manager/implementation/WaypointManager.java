package lol.apex.manager.implementation;

import lol.apex.feature.waypoint.Waypoint;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class WaypointManager {
    @Getter
    private static final List<Waypoint> waypoints = new ArrayList<>();

    public static void add(Waypoint waypoint) {
        waypoints.add(waypoint);
    }

    public static void remove(Waypoint waypoint) {
        waypoints.remove(waypoint);
    }

    public static void clear() {
        waypoints.clear();
    }
}
