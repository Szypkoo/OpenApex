package lol.apex.feature.file.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lol.apex.Apex;
import lol.apex.feature.file.BaseFile;

import java.util.UUID;

public class FriendsFile extends BaseFile<JsonElement> {
    public static final FriendsFile DEFAULT = new FriendsFile("Friends.json");

    public FriendsFile(String file) {
        super(file, JsonElement.class);
    }

    @Override
    protected void load(JsonElement el) {
        if (Apex.friendManager == null) {
            return;
        }

        if (el.isJsonObject()) { // compatibility
            el = el.getAsJsonObject().getAsJsonArray("friends");
        }

        final var arr = el.getAsJsonArray();

        Apex.friendManager.beginLoad();
        try {
            Apex.friendManager.clearLoadedFriends();

            for (final var element : arr) {
                if (!element.isJsonPrimitive()) {
                    continue;
                }

                try {
                    Apex.friendManager.addLoadedFriend(UUID.fromString(element.getAsString()));
                } catch (IllegalArgumentException exception) {
                    Apex.LOGGER.warn("Skipping invalid friend UUID in {}", getName(), exception);
                }
            }
        } finally {
            Apex.friendManager.endLoad();
        }
    }

    @Override
    protected JsonArray save() {
        JsonArray friends = new JsonArray();

        if (Apex.friendManager != null) {
            for (UUID friend : Apex.friendManager) {
                friends.add(friend.toString());
            }
        }

        return friends;
    }
}
