package lol.apex.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import dev.toru.clients.eventBus.Event;
import lombok.Setter;


public class PlayerInteractionRangeEvent extends Event {

    @Getter @Setter
    @AllArgsConstructor
    public static class Entity extends Event {
        private double reach;
    } 

    @Getter @Setter
    @AllArgsConstructor
    public static class Block extends Event {
        private double reach;
    }
}
