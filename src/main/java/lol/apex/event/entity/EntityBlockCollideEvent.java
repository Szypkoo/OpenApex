package lol.apex.event.entity;

import dev.toru.clients.eventBus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Getter
@Setter
@AllArgsConstructor
public class EntityBlockCollideEvent extends Event {
    private BlockState blockState;
    private BlockView world;
    private BlockPos blockPos;
    private EntityCollisionHandler handler;
    private boolean bl;
    private CallbackInfo callback;

}
