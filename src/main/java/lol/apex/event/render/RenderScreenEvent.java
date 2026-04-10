package lol.apex.event.render;

import dev.toru.clients.eventBus.Event;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screen.Screen;

@Getter
@Setter
public class RenderScreenEvent extends Event {
    private Screen screen;
}
