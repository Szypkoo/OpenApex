package lol.apex.mixin;

import lol.apex.Apex;
import lol.apex.event.player.PlayerCameraPositionEvent;
import lol.apex.util.CommonVars;
import lol.apex.util.rotation.RotationUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public abstract class CameraMixin implements CommonVars {
    @Unique float tickDelta;
    @Shadow private Vec3d pos;

    @Inject(method = "update", at = @At("TAIL"))
    private void cameraUpdate(World area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        PlayerCameraPositionEvent event = new PlayerCameraPositionEvent(new Vec3d(pos.x, pos.y, pos.z));
        Apex.eventBus.post(event);
        if (event.isCancelled()) {
            this.pos = event.getPosition();
        }
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
    public void r(Args args) {
        float yaw = tickDelta == 1.0f ? RotationUtil.yaw :
                MathHelper.lerp(tickDelta, RotationUtil.prevyaw, RotationUtil.yaw);
        float pitch = tickDelta == 1.0f ? RotationUtil.pitch :
                MathHelper.lerp(tickDelta, RotationUtil.prevpitch, RotationUtil.pitch);
        if(mc.options.getPerspective().isFrontView()) {
            yaw += 180.0f;
            pitch = -pitch;
        }
        args.set(0, yaw);
        args.set(1, pitch);
    }

    @Inject(method = "update", at = @At("HEAD"))
    private void updateTickDelta(World area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDeltaProgress, CallbackInfo info) {
        tickDelta = tickDeltaProgress;
    }
}
