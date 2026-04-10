package lol.apex.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import lol.apex.Apex;
import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.event.player.PlayerUseMultiplierEvent;
import lol.apex.event.player.PlayerUsingItemEvent;
import lol.apex.event.client.PreMotionEvent;
import lol.apex.event.client.PreUpdateEvent;
import lol.apex.feature.module.implementation.player.NoSlowModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow public abstract boolean isSneaking();
    @Shadow public abstract boolean shouldSlowDown();
    @Shadow public abstract boolean isUsingItem();
    @Shadow public abstract boolean isSubmergedInWater();

    @Shadow @Final protected MinecraftClient client;

    @Inject(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"
            ),
            cancellable = true
    )
    public void onMove(MovementType type, Vec3d movement, CallbackInfo ci) {
        PlayerMoveEvent event = new PlayerMoveEvent(movement.x, movement.y, movement.z);
        Apex.eventBus.post(event);

        if (event.isCancelled()) {
            super.move(type, new Vec3d(event.getX(), event.getY(), event.getZ()));
            ci.cancel();
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V"), method = "tick")
    public void onPreUpdate(CallbackInfo ci) {
        Apex.eventBus.post(new PreUpdateEvent());
        
        if (this.isUsingItem()) {
            PlayerUsingItemEvent event = new PlayerUsingItemEvent(
                    this.getActiveItem(),
                    this.getItemUseTimeLeft(),
                    true
            );
            Apex.eventBus.post(event);
        }
    }

    @Unique
    PreMotionEvent preMotionEvent;

    @Inject(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z", shift = At.Shift.AFTER), cancellable = true)
    private void sendPreMotionEvent(CallbackInfo ci) {
        PreMotionEvent event = new PreMotionEvent(
                this.getX(),
                this.getY(),
                this.getZ(),
                this.getYaw(),
                this.getPitch(),
                this.isOnGround()
        );
        this.preMotionEvent = event;

        Apex.eventBus.post(event);

        if (event.isCancelled()) ci.cancel();
    }

    /**
     * Hook custom multiplier
     *
     * <pre>
     * if (this.isUsingItem() && !this.hasVehicle()) {
     *     vec2f = vec2f.multiply(this.getActiveItemSpeedMultiplier());
     * }
     * </pre>
     */
    @WrapOperation(method = "applyMovementSpeedFactors", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec2f;multiply(F)Lnet/minecraft/util/math/Vec2f;", ordinal = 1))
    private Vec2f hookCustomMultiplier(Vec2f instance, float value, Operation<Vec2f> original) {
        final var playerUseMultiplier = new PlayerUseMultiplierEvent(value, value);
        Apex.eventBus.post(playerUseMultiplier);
        if (playerUseMultiplier.isCancelled()) {
            return new Vec2f(
                    instance.x,
                    instance.y
            );
        }
        return new Vec2f(
                instance.x * playerUseMultiplier.sideways,
                instance.y * playerUseMultiplier.forward
        );
    }

    /**
     * Hook sprint effect from NoSlow module
     */
    @ModifyExpressionValue(method = "isBlockedFromSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean hookSprintAffectStart(boolean original) {
        if (NoSlowModule.INSTANCE.enabled()) {
            return false;
        }

        return original;
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getX()D"))
    private double hookEventX(ClientPlayerEntity instance) {
        return preMotionEvent.x;
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getY()D"))
    private double hookEventY(ClientPlayerEntity instance) {
        return preMotionEvent.y;
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getZ()D"))
    private double hookEventZ(ClientPlayerEntity instance) {
        return preMotionEvent.z;
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
    private float hookEventYaw(ClientPlayerEntity instance) {
        return preMotionEvent.yaw;
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
    private float hookEventPitch(ClientPlayerEntity instance) {
        return preMotionEvent.pitch;
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isOnGround()Z"))
    private boolean hookOnGround(ClientPlayerEntity instance) {
        return preMotionEvent.onGround;
    }

    @Inject(method = "shouldStopSprinting", at = @At("HEAD"), cancellable = true)
    private void shouldStopSprinting(CallbackInfoReturnable<Boolean> cir) {
        if (this.isGliding() || this.shouldSlowDown()) {
            cir.setReturnValue(true);
        }
    }
}