package lol.apex.event.render;

import dev.toru.clients.eventBus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
public class WorldFogStrengthEvent extends Event {
    private float strength;
}
