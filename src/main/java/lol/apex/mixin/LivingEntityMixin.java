package lol.apex.mixin;

import lol.apex.Apex;
import lol.apex.event.player.PlayerJumpEvent;
import lol.apex.event.player.PlayerJumpingFactorEvent;
import lol.apex.feature.module.implementation.visual.AnimationsModule;
import lol.apex.util.CommonVars;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements CommonVars {

    @SuppressWarnings("unused")
    @Inject(method = "jump", at = @At("HEAD"))
    private void onJump(CallbackInfo info) {
        if((Object)this instanceof PlayerEntity player) {
            Apex.eventBus.post(new PlayerJumpEvent());
        }
    }

    @Unique
    public float lastYaw = 0.0f;

    @Inject(
            method = "jump",
            at = @At("HEAD")
    )
    private void modifyJump2(CallbackInfo info) {
        if (mc.player != null && ((Object) this instanceof ClientPlayerEntity)) {
            lastYaw = mc.player.getYaw();
        }
    }

    @Inject(method = "jump", at = @At("TAIL"))
    private void modifyJumpPost(CallbackInfo ci) {
        if (mc.player != null && ((Object) this instanceof ClientPlayerEntity)) {
            mc.player.setYaw(lastYaw);
        }
    }

    @Inject(
            method = "jump",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modifyJump(CallbackInfo info) {
        LivingEntity self = (LivingEntity)(Object)this;

        if (!(self instanceof PlayerEntity)) return;
        PlayerJumpingFactorEvent event = new PlayerJumpingFactorEvent();
        Apex.eventBus.post(event);

        if(event.isCancelled()) {
            double jumpFactor = event.getJumpingFactor();
            self.setVelocity(
                    self.getVelocity().x, jumpFactor,
                    self.getVelocity().z
            );
            self.velocityDirty = true;
            info.cancel();
        }
    } 

    @Inject(method = "getHandSwingDuration", at = @At("HEAD"), cancellable = true)
    public void handSwingDuration(CallbackInfoReturnable<Integer> cir) {

        var module = Apex.moduleManager.getByClass(AnimationsModule.class);
        if (module != null && module.useSwingSpeed.getValue() && module.enabled()) {
             cir.setReturnValue(module.swingSpeed.getValue().intValue());
        }
    }
}
