package lol.apex.event.client;

import dev.toru.clients.eventBus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.input.Input;

@Setter
@Getter
@AllArgsConstructor
public class GameInputEvent extends Event {
    private Input input;
    public boolean moveFix = false;

    public GameInputEvent(Input input) {
        this.input = input;
    }
}
