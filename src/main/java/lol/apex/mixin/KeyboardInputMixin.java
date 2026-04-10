package lol.apex.mixin;

import lol.apex.Apex;
import lol.apex.event.client.GameInputEvent;
import lol.apex.feature.module.implementation.movement.MovementCorrectionModule;
import lol.apex.util.CommonVars;
import lol.apex.util.rotation.RotationUtil;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin implements CommonVars {
    @Unique
    private void NoStrafeMovement(GameInputEvent event) {
        float diff = (RotationUtil.prevyaw - mc.player.getYaw());
        float f = (float) Math.sin(diff * ((float) Math.PI / 180F));
        float f1 = (float) Math.cos(diff * ((float) Math.PI / 180F));

        float multiplier = 1f;
        if (mc.player.isSneaking() || mc.player.isUsingItem()) multiplier = 10;

        float forward = (float) (Math.round((event.getInput().getMovementInput().y * f1
                + event.getInput().getMovementInput().x * f) * multiplier)) / multiplier;

        float strafe = (float) (Math.round((event.getInput().getMovementInput().x * f1
                - event.getInput().getMovementInput().y * f) * multiplier)) / multiplier;

        event.getInput().getMovementInput().y = forward;
        event.getInput().getMovementInput().x = strafe;
    }

    @Inject(at = @At(value = "TAIL"), method = "tick")
    public void SubscribeInputEvent(CallbackInfo ci) {
        KeyboardInput casted = (KeyboardInput) (Object) this;
        GameInputEvent event = new GameInputEvent(casted);
        Apex.eventBus.post(event);

        MovementCorrectionModule combatEngine = Apex.moduleManager.getByClass(MovementCorrectionModule.class);
        if (combatEngine.mode.getValue() == MovementCorrectionModule.Mode.STRAFE) {
            NoStrafeMovement(event);
        }
    }

//    @Inject(at = @At(value = "HEAD"), method = "tick")
//    public void inputEventPre(CallbackInfo ci) {
//        PreGameInputEvent event = new PreGameInputEvent();
//        Apex.eventBus.post(event);
//    }
}
