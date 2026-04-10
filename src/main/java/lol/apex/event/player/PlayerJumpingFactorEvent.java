package lol.apex.event.player;

import dev.toru.clients.eventBus.Event;
import lombok.Getter;
import lombok.Setter;

public class PlayerJumpingFactorEvent extends Event {
    @Getter @Setter
    private double jumpingFactor;    
}
