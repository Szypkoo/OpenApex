package lol.apex.mixin;

import lol.apex.Apex;
import lol.apex.event.player.PlayerRotationEvent;
import lol.apex.util.CommonVars;
import lol.apex.util.rotation.Rotation;
import lol.apex.util.rotation.RotationUtil;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Mouse.class)
public class MouseMixin implements CommonVars {

    @ModifyArgs(method = "updateMouse", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    public void updateMouse(Args args) {
        float pitch = RotationUtil.pitch;
        float yaw = RotationUtil.yaw;

        RotationUtil.yaw = (float)((double)RotationUtil.yaw + (Double)args.get(0) * 0.15);
        RotationUtil.pitch = (float)((double)RotationUtil.pitch + (Double)args.get(1) * 0.15);
        RotationUtil.pitch = MathHelper.clamp(RotationUtil.pitch, -90.0f, 90.0f);
        RotationUtil.prevpitch += RotationUtil.pitch - pitch;
        RotationUtil.prevyaw += RotationUtil.yaw - yaw;
        PlayerRotationEvent event = new PlayerRotationEvent(RotationUtil.yaw, RotationUtil.pitch);
        Apex.eventBus.post(event);

        ClientPlayerEntity thePlayer = mc.player;

        args.set(0, (Object)0.0);
        args.set(1, (Object)0.0);

        if (!event.modified()) {
            thePlayer.setYaw(RotationUtil.yaw);
            thePlayer.setPitch(RotationUtil.pitch);
            return;
        }

        final var x = event.get();

        final var fixedRotation = RotationUtil.applySensitivity(
                x,
                new Rotation(thePlayer.getYaw(), thePlayer.getPitch())
        );

        thePlayer.setYaw(fixedRotation.yaw());
        thePlayer.setPitch(fixedRotation.pitch());
    }
}
