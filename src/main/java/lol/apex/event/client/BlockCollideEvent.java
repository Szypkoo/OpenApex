package lol.apex.event.client;

import dev.toru.clients.eventBus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Getter
@Setter
@AllArgsConstructor
public class BlockCollideEvent extends Event {
    private BlockState blockState;
    private BlockView world;
    private BlockPos blockPos;
    private ShapeContext context;
    private CallbackInfoReturnable<VoxelShape> callback;


}
