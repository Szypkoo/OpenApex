package lol.apex.event.player;

import dev.toru.clients.eventBus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;

@Getter @AllArgsConstructor @Setter
public class PlayerUsingItemEvent extends Event {
    private ItemStack itemStack;
    private int useTicksRemaining;
    private boolean useItem;
}
