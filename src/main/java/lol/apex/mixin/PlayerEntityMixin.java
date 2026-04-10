package lol.apex.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import lol.apex.Apex;
import lol.apex.event.player.PlayerInteractionRangeEvent;
import lol.apex.feature.module.implementation.legit.KeepSprintModule;
import lol.apex.feature.module.implementation.legit.ReachModule;

import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;

import static lol.apex.util.CommonVars.mc;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "getEntityInteractionRange()D", at = @At("HEAD"), cancellable = true)
    private void onEntityInteractionRange(CallbackInfoReturnable<Double> cir) {
        ReachModule reachModule = Apex.moduleManager.getByClass(ReachModule.class);

        if (reachModule == null || !reachModule.enabled()) {
            return;
        }

        PlayerInteractionRangeEvent.Entity event =
                new PlayerInteractionRangeEvent.Entity(reachModule.getAmount());
        Apex.eventBus.post(event);

        if (event.isCancelled()) {
            cir.setReturnValue(event.getReach());
        }
    }

    @SuppressWarnings("ConstantValue")
    @Redirect(method = "knockbackTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;multiply(DDD)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d hookSlowVelocity(Vec3d instance, double x, double y, double z) {
        if ((Object) this == mc.player && KeepSprintModule.INSTANCE.isEnabled()) {
            x = z = KeepSprintModule.INSTANCE.getMotion();
        }

        return instance.multiply(x, y, z);
    }

    /**
     * for: attack, pierce
     */
    @WrapWithCondition(method = "knockbackTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V", ordinal = 0))
    private boolean hookSlowVelocity(PlayerEntity instance, boolean b) {
        if ((Object) this == mc.player) {
            KeepSprintModule.INSTANCE.sprinting = b;
            return !KeepSprintModule.INSTANCE.isEnabled() || b;
        }

        return true;
    }

    @SuppressWarnings({"UnreachableCode", "ConstantValue"})
    @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSprinting()Z"))
    private boolean hookSlowVelocity(boolean original) {
        if ((Object) this == mc.player && KeepSprintModule.INSTANCE.isEnabled()) {
            return KeepSprintModule.INSTANCE.sprinting;
        }

        return original;
    }

    @Inject(method = "getBlockInteractionRange()D", at = @At("HEAD"), cancellable = true)
    private void onBlockInteractionRange(CallbackInfoReturnable<Double> cir) {
        ReachModule reachModule = Apex.moduleManager.getByClass(ReachModule.class);

        if (reachModule == null || !reachModule.enabled()) {
            return;
        }

        PlayerInteractionRangeEvent.Block event =
                new PlayerInteractionRangeEvent.Block(reachModule.getAmount());
        Apex.eventBus.post(event);

        if (event.isCancelled()) {
            cir.setReturnValue(event.getReach());
        }
    }
}
