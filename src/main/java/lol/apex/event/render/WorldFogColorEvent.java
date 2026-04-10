package lol.apex.event.render;

import dev.toru.clients.eventBus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class WorldFogColorEvent extends Event {
    private Color color;
}
