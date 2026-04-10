package lol.apex.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;

@Getter @Setter @AllArgsConstructor
public class PlayerAttackEventPost {
    private Entity target;
}
