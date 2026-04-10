package lol.apex.event.player;

import dev.toru.clients.eventBus.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlayerUseMultiplierEvent extends Event {
    public float forward, sideways;
}
