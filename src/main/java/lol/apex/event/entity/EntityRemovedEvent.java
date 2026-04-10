package lol.apex.event.entity;

import dev.toru.clients.eventBus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;

@Getter @AllArgsConstructor
public class EntityRemovedEvent extends Event {
    private Entity entity;
}
