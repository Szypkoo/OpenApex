package lol.apex.event.packet;

import dev.toru.clients.eventBus.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SendMessageEvent extends Event {
    public String message; // getMessage() looks better 
}

