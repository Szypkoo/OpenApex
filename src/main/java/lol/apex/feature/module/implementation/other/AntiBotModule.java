package lol.apex.feature.module.implementation.other;

import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;

@ModuleInfo(
        name = "AntiBot",
        description = "Stops anti-cheat bots and NPCs from being targeted.",
        category = Category.OTHER
)
public class AntiBotModule extends Module {

    public final BoolSetting checkInTablist = new BoolSetting("Check In Tablist", true);
    public final BoolSetting checkValidName = new BoolSetting("Check Valid Name", true);
    public final BoolSetting checkAge = new BoolSetting("Check Age", true);
    public final BoolSetting checkLatency = new BoolSetting("Check Latency", true);

    private static final String VALID_USERNAME_REGEX = "^[a-zA-Z0-9_]{1,16}$";

    public boolean isBot(PlayerEntity player) {
        if (player == null || mc.player == null)
            return true;

        if (player == mc.player)
            return false;

        PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());

        if (checkAge.getValue() && player.age < 20)
            return true;

        if (checkInTablist.getValue() && entry == null)
            return true;

        if (checkLatency.getValue() && entry != null && entry.getLatency() == 0)
            return true;

        if (checkValidName.getValue() && entry != null) {
            String name = entry.getProfile().name();

            return name == null ||
                    !name.matches(VALID_USERNAME_REGEX) ||
                    name.contains(" ") ||
                    name.contains("NPC");
        }

        return false;
    }

    public boolean isInTab(PlayerListEntry entry) {
        for (PlayerListEntry info : mc.getNetworkHandler().getPlayerList()) {
            if (info.getProfile().id().equals(entry.getProfile().id())) {
                return true;
            }
        }
        return false;
    }
}