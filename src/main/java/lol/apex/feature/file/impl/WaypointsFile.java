package lol.apex.feature.file.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lol.apex.Apex;
import lol.apex.feature.file.BaseFile;
import lol.apex.feature.waypoint.Waypoint;
import lol.apex.manager.implementation.WaypointManager;

public class WaypointsFile extends BaseFile<JsonElement> {
    public static final WaypointsFile DEFAULT = new WaypointsFile("Waypoints.json");

    public WaypointsFile(String file) {
        super(file, JsonElement.class);
    }

    @Override
    protected void load(JsonElement el) {
        if (el.isJsonObject()) { // compatibility
            final var obj = el.getAsJsonObject();
            if (obj.has("waypoints"))
                el = obj.getAsJsonArray("waypoints");
            else return;
        }

        final var arr = el.getAsJsonArray();

        WaypointManager.clear();

        for (final var element : arr) {
            final var waypointObject = element.getAsJsonObject();
            final var name = waypointObject.get("name").getAsString();
            final var x = waypointObject.get("x").getAsInt();
            final var y = waypointObject.get("y").getAsInt();
            final var z = waypointObject.get("z").getAsInt();
            final var visible = waypointObject.get("visible").getAsBoolean();

            Waypoint waypoint = new Waypoint(name, x, y, z, visible);
            WaypointManager.add(waypoint);
        }
    }

    @Override
    protected JsonArray save() {
        JsonArray waypoints = new JsonArray();

        for (Waypoint wp : WaypointManager.getWaypoints()) {
            JsonObject wpObj = new JsonObject();
            wpObj.addProperty("name", wp.getName());
            wpObj.addProperty("x", wp.getX());
            wpObj.addProperty("y", wp.getY());
            wpObj.addProperty("z", wp.getZ());
            wpObj.addProperty("visible", wp.isVisible());
            waypoints.add(wpObj);
        }

        if (waypoints.isEmpty()) {
            Apex.LOGGER.warn("No waypoints to save in {}", getName());
        }

        return waypoints;
    }
}
