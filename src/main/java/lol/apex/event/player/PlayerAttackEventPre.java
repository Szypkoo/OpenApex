package lol.apex.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;

import dev.toru.clients.eventBus.Event;

@Getter @Setter @AllArgsConstructor
public class PlayerAttackEventPre extends Event {
    private Entity target;
}
