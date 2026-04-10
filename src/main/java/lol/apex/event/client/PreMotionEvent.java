package lol.apex.event.client;

import lombok.AllArgsConstructor;
import dev.toru.clients.eventBus.Event;

@AllArgsConstructor
public class PreMotionEvent extends Event {
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public boolean onGround;
}