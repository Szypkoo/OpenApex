package lol.apex.feature.module.implementation.other;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.Apex;
import lol.apex.event.client.ClientPostEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.ui.notification.Notification;
import lol.apex.feature.ui.notification.NotificationType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

@ModuleInfo(
        name = "MCF",
        description = "Allows you to middle-click players to add them as friends.",
        category = Category.OTHER
)
public class MCFModule extends Module {
    private boolean pressed;

    @EventHook
    public void onTick(ClientPostEvent event) {
        if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), 2) == 1) {
            if (!pressed) click();
            pressed = true;
        } else {
            pressed = false;
        }
    }

    private void click() {
        Entity targetedEntity = mc.targetedEntity;
        if (!(targetedEntity instanceof PlayerEntity)) return;

        PlayerEntity player = (PlayerEntity) targetedEntity;
        String playerName = player.getGameProfile().name();
        if (Apex.friendManager.isFriend(player)) {
            Apex.friendManager.remove(player.getUuid());
        //    Apex.sendChatMessage(playerName + " was removed from friends.");
            Apex.notificationRenderer.push(new Notification(NotificationType.INFORMATION, "MCF", playerName + " was removed from friends."));
        } else {
            Apex.friendManager.add(player.getUuid());
        //    Apex.sendChatMessage(playerName + " is now your friend.");
            Apex.notificationRenderer.push(new Notification(NotificationType.INFORMATION, "MCF", playerName + " is now your friend."));

        }
    }
}