package lol.apex.mixin;

import lol.apex.Apex;
import lol.apex.event.client.BlockCollideEvent;
import lol.apex.event.entity.EntityBlockCollideEvent;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)

public class AbstractBlockMixin {

    @Inject(at = @At("HEAD"), method = "getCollisionShape", cancellable = true)
    public void getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir){
        if (Apex.eventBus == null) return;
        BlockCollideEvent event = new BlockCollideEvent(state, world, pos, context, cir);
        Apex.eventBus.post(event);
    }

    @Inject(at = @At("HEAD"), method = "onEntityCollision", cancellable = true)
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl, CallbackInfo ci){
        EntityBlockCollideEvent event = new EntityBlockCollideEvent(state, world, pos, handler, bl, ci);
        Apex.eventBus.post(event);
    }
}
