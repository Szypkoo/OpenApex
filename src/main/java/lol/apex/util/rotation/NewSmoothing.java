package lol.apex.util.rotation;

import lol.apex.Apex;
import lol.apex.util.CommonVars;
import net.minecraft.util.math.Box;

/// Trying to recreate stupid trick nigger shots that sweats do and seeing if it works on polar.
/// So:
/// - We should start by snapping above their head with some offset and randomness
/// - After that, we should pull our pitch down in order to hit them.
///   This is the part where polar will almost likely check, and we need to do a lot of stuff to mimick legits.
/// - useless and optional: 15% chance, we should randomly 180, 45, then 135 to perform a full spin.
public final class NewSmoothing implements CommonVars {
    public Rotation current;
    private static final float MIN_SPEED = 1.25f;
    private static final float DECELERATE_SPEED_START = 1f;
    private static final float DECELERATE_SPEED_END = 0.75f;
    private float decelerate = DECELERATE_SPEED_START;
    private float curSpeed = MIN_SPEED * 4;

    private enum State {
        /// No target, or not prepared yet.
        GO_ABOVE_HEAD,
        /// Now is time to pull down, this is the part polar will likely care about, so we need to be a bit slow.
        PULLING_DOWN,
        /// A random spin
//        SPIN
    }
    private State state = State.GO_ABOVE_HEAD;

    private float getRotSpeed() {
        var speed = curSpeed;
        speed *= decelerate *= 0.9998f;
        // useless randomization
        if (decelerate <= DECELERATE_SPEED_END) {
            decelerate = DECELERATE_SPEED_START + (MathUtil.randomFloat(0.998f, 2.45f) - DECELERATE_SPEED_END);
        }
        curSpeed = Math.max(speed, MIN_SPEED);
        return curSpeed;
    }

    public void onSuccessfulHit() {
        state = State.GO_ABOVE_HEAD;
    }

    public Rotation smooth(Box targetBox) {
        current = new Rotation(mc.player.getYaw(), mc.player.getPitch());
        switch (state) {
            case GO_ABOVE_HEAD -> {
                final var aboveBox = targetBox
                        .withMinX(targetBox.maxX + MathUtil.randomFloat(9f, 14f))
                        .withMaxX(targetBox.maxX + MathUtil.randomFloat(15f, 18f))
                        .withMinY(targetBox.maxY + MathUtil.randomFloat(10f, 24f))
                        .withMaxY(targetBox.maxY + MathUtil.randomFloat(24.1f, 28f))
                        .withMinZ(targetBox.maxZ + MathUtil.randomFloat(2.5f, 9f))
                        .withMaxZ(targetBox.maxZ + MathUtil.randomFloat(17f, 19f));

                final var rots = RotationUtil.rotation(
                        AlgebraUtil.nearest(aboveBox, mc.player.getEyePos()),
                        mc.player.getEyePos()
                );

                final var speed = getRotSpeed();
                final var smoothedYaw = RotationUtil.smoothRot(current.yaw(), rots.yaw(), speed);
                final var newRot = new Rotation(smoothedYaw + (MathUtil.nextFloat(-1.0f, 1.0f)) * 1.2f, rots.pitch());
                current = newRot;
                state = State.PULLING_DOWN;
                return newRot;
            }
            case PULLING_DOWN -> {
                final var rots = RotationUtil.rotation(
                        AlgebraUtil.nearest(targetBox, mc.player.getEyePos()),
                        mc.player.getEyePos()
                );
                final var speed = getRotSpeed();
                final var pitchSpeed = MathUtil.randomFloat(19f, 12f);
                final var smoothedYaw = RotationUtil.smoothRot(current.yaw(), rots.yaw(), speed);
                final var smoothedPitch = RotationUtil.smoothRot(current.pitch(), rots.pitch(), pitchSpeed);
                final var newRot = new Rotation(smoothedYaw + (MathUtil.nextFloat(-1.0f, 1.0f)) * 1.2f, smoothedPitch);
                current = newRot;
                return newRot;
            }
            default -> {
                Apex.LOGGER.error("Unhandled Polar2 state: {}", state);
                return RotationUtil.rotation(
                        AlgebraUtil.nearest(targetBox, mc.player.getEyePos()),
                        mc.player.getEyePos()
                );
            }
        }
    }
}
