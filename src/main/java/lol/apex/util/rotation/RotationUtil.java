package lol.apex.util.rotation;

import lol.apex.Apex;
import lol.apex.util.CommonVars;
import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@UtilityClass
public final class RotationUtil implements CommonVars {
    public static float yaw;
    public static float prevyaw;
    public static float pitch;
    public static float prevpitch;

    public static double getSensitivityStep() {
        double sens = MinecraftClient.getInstance().options.getMouseSensitivity().getValue();
        return Math.pow(sens, 3) * 8.0F * 0.15D;
    }

    public static Rotation applySensitivity(Rotation target, Rotation start) {
        float yawDiff = target.yaw() - start.yaw(); // wrap
        float pitchDiff = target.pitch() - start.pitch();

        double gcd = getSensitivityStep();
        double mouseDeltaX = Math.round(yawDiff / gcd);
        double mouseDeltaY = Math.round(pitchDiff / gcd);

        double fixedDeltaYaw = mouseDeltaX * gcd;
        double fixedDeltaPitch = mouseDeltaY * gcd;

        float f = (float)fixedDeltaYaw;
        float g = (float)fixedDeltaPitch;

        float fixedYaw = start.yaw() + f;
        float fixedPitch = start.pitch() + g;
        fixedPitch = MathHelper.clamp(fixedPitch, -90.0F, 90.0F);

        return new Rotation(fixedYaw, fixedPitch);
    }

    public static float smoothRot(float current, float goal, float speed) {
        return current + MathHelper.clamp(MathHelper.wrapDegrees(goal - current), -speed, speed);
    }

    public static Rotation rotation(double x, double y, double z, double ax, double ay, double az) {
        final var diffX = x - ax;
        final var diffY = y - ay;
        final var diffZ = z - az;
        final var yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0d);
        final var pitch = (float) (-Math.toDegrees(Math.atan2(diffY, Math.hypot(diffX, diffZ))));
        return new Rotation(yaw, pitch);
    }

    public static Rotation rotation(Vec3d a, Vec3d b) {
        return rotation(a.x, a.y, a.z, b.x, b.y, b.z);
    }

    public static float getMovementDirectionYaw() {
        var actualYaw = RotationUtil.yaw;
        final var inp = mc.player.input.playerInput;
        if (inp.backward() && !inp.forward()) {
            actualYaw += 180f;
        }
        var forwardMultiplier = inp.backward() && !inp.forward() ? -0.5f : inp.forward() && !inp.backward() ? 0.5f : 1f;

        if (inp.left() && !inp.right()) {
            actualYaw -= 90f * forwardMultiplier;
        }

        if (inp.right() && !inp.left()) {
            actualYaw += 90f * forwardMultiplier;
        }

        return actualYaw;
    }
}
