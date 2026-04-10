package lol.apex.feature.ui.notification;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.render.Render2DEvent;
import lol.apex.feature.ui.imgui.IImWrapper;
import lol.apex.feature.ui.imgui.ImGuiImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NotificationRenderer {
    private final List<Notification> notifications = new ArrayList<>();

    public void push(String title, String desc) {
        push(new Notification(NotificationType.INFORMATION, title, desc));
    }

    public void push(Notification notification) {
        notifications.add(notification);
    }

    public void update() {
        Iterator<Notification> it = notifications.iterator();

        while (it.hasNext()) {
            Notification n = it.next();

            if (n.shouldRemove()) {
                it.remove();
            }
        }
    }

    @EventHook
    public void onRender2D(Render2DEvent event) {
        update();

        ImGuiImpl.render(wrapper -> {
            var io = wrapper.getIO();

            float screenWidth = io.getDisplaySizeX();
            float screenHeight = io.getDisplaySizeY();

            float spacing = 45f;
            float margin = 10f;

            int index = 0;

            for (Notification notification : notifications) {

                float width = notification.getWidth();
                float height = 40;

                float x = screenWidth - width - margin;
                float y = screenHeight - margin - height - (index * spacing);

                notification.render(wrapper, x, y);

                index++;
            }
        });
    }
}