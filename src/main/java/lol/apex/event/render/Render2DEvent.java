package lol.apex.event.render;

import dev.toru.clients.eventBus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

@Getter
@AllArgsConstructor
public class Render2DEvent extends Event {
    private final DrawContext context;
    private final RenderTickCounter ticks;
    private final int scaledWidth, scaledHeight;
}