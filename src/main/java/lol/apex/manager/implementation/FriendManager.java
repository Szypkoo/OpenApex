package lol.apex.manager.implementation;

import lol.apex.Apex;
import lol.apex.feature.file.impl.FriendsFile;
import lol.apex.manager.ListManager;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class FriendManager extends ListManager<UUID> {

    private boolean loading;

    @Override
    public void add(UUID value) {
        super.add(value);
        saveFriends();
    }

    @Override
    public void addIfAbsent(UUID value) {
        if (getFirst(existing -> existing.equals(value)).isPresent()) {
            return;
        }

        super.addIfAbsent(value);
        saveFriends();
    }

    @Override
    public void remove(UUID value) {
        super.remove(value);
        saveFriends();
    }

    @Override
    public void clear() {
        super.clear();
        saveFriends();
    }

    public boolean isFriend(PlayerEntity entity) {
        return getFirst(u -> u.equals(entity.getUuid())).isPresent();
    }

    public void saveFriends() {
        if (!loading) {
            FriendsFile.DEFAULT.saveToFile();
        }
    }

    public void loadFriends() {
        FriendsFile.DEFAULT.loadFromFile();
    }

    public void beginLoad() {
        loading = true;
    }

    public void endLoad() {
        loading = false;
    }

    public void clearLoadedFriends() {
        super.clear();
    }

    public void addLoadedFriend(UUID friend) {
        super.addIfAbsent(friend);
    }

    public void initialize() {
        FriendsFile.DEFAULT.loadFromFile();
        Apex.LOGGER.info("Loaded friends list");
    }
}
