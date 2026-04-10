package lol.apex.event.player;

import dev.toru.clients.eventBus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;

@AllArgsConstructor
@Getter
public class PlayerOutlineEvent extends Event {
    private Entity entity;
}
