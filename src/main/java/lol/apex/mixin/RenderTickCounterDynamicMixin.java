package lol.apex.mixin;

import lol.apex.util.game.GameTimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.render.RenderTickCounter;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.objectweb.asm.Opcodes;

@Mixin(RenderTickCounter.Dynamic.class)
public class RenderTickCounterDynamicMixin {
    @Shadow private float dynamicDeltaTicks;

    @Inject(method = "beginRenderTick(J)I", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/render/RenderTickCounter$Dynamic;lastTimeMillis:J", opcode = Opcodes.PUTFIELD))
    private void tick(long a, CallbackInfoReturnable<Integer> cir) {
        float speed = GameTimer.getSpeed();
        if(GameTimer.getSpeed() > 0)
            dynamicDeltaTicks *= speed;
    }
}
