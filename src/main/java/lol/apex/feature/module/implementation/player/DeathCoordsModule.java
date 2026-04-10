package lol.apex.feature.module.implementation.player;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.Apex;
import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.waypoint.Waypoint;
import lol.apex.manager.implementation.WaypointManager;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;

@ModuleInfo(
        name = "DeathCoords",
        description = "Allows you to help find out where you previously perished.",
        category = Category.PLAYER
)
public class DeathCoordsModule extends Module {

    public final BoolSetting waypoint = new BoolSetting("Create Waypoint", true);
    public final BoolSetting clipboard = new BoolSetting("Copy to clipboard", true);

    private int number = 1;

    @EventHook
    public void onPacket(PacketEvent.Receive event) {
        if(event.getPacket() instanceof DeathMessageS2CPacket packet) {
            if(packet.playerId() == mc.player.getId()) {
                double x = mc.player.getX();
                double y = mc.player.getY();
                double z = mc.player.getZ();

                String coords = String.format("%.0f %.0f %.0f", x, y, z);

                String message = "Saved Death Coords: " + coords;

                Apex.sendChatMessage(
                        message + (clipboard.getValue() ? " (copied)" : "")
                );

                if(clipboard.getValue()) {
                    mc.keyboard.setClipboard(coords);
                }

                if(waypoint.getValue()) {
                    number++;
                    WaypointManager.add(new Waypoint("Death Position #" + number, (int) x, (int) y, (int) z, true));
                    Apex.notificationRenderer.push("Death Coords", "Created death waypoint at coords.");
                }
            }
        }
    }
}

