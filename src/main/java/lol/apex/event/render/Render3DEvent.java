package lol.apex.event.render;

import dev.toru.clients.eventBus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.util.math.MatrixStack;

@Getter @AllArgsConstructor
public class Render3DEvent extends Event {
    private final float ticks; 
    private final MatrixStack matrix;

}
