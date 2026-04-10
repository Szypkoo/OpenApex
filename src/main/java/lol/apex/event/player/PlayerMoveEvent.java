package lol.apex.event.player;

import dev.toru.clients.eventBus.Event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @AllArgsConstructor @Setter
public class PlayerMoveEvent extends Event {
    public double x, y, z;
}
