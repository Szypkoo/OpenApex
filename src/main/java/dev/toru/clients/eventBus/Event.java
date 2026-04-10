package dev.toru.clients.eventBus;

import lombok.Getter;
import lombok.Setter;

public class Event {
    @Getter @Setter
    private boolean cancelled;
}
