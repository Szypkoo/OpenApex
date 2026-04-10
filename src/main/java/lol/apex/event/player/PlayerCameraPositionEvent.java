package lol.apex.event.player;

import dev.toru.clients.eventBus.Event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.Vec3d;

@Getter
@Setter
@AllArgsConstructor
public class PlayerCameraPositionEvent extends Event {
    private Vec3d position;
}
