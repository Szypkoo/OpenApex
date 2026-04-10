package lol.apex.feature.module.implementation.player.scaffold.rotation;

import lol.apex.util.CommonVars;
import lol.apex.util.rotation.MathUtil;
import lol.apex.util.rotation.Rotation;
import lol.apex.util.rotation.RotationUtil;

public final class HypixelRotations implements CommonVars {
    private record IntRange(int min, int max) {
        public int random() {
            return MathUtil.randomInt(this.min, this.max);
        }
        public boolean in(int x) {
            return x >= this.min && x <= this.max;
        }
    }
    private record FloatRange(float min, float max) {
        public float random() {
            return MathUtil.randomFloat(this.min, this.max);
        }
        @SuppressWarnings("unused")
        public boolean in(float x) {
            return x >= this.min && x <= this.max;
        }
    }
    /*    val tolr by intRange("Tolerance", 3..6, 1..20, "rotation ticks")
    val fast by floatRange("Fast", 80f..90f, 1f..180f, "degrees")
    val slow by floatRange("Slow", 36.99999f..38.2f, 1f..180f, "degrees")
    val fastToleranceRange by intRange("FastToleranceRange", 2..9, 2..9)
    var thing = tolr.random()*/
    private static final IntRange TOLERANCE = new IntRange(3, 6);
    private static final FloatRange FAST = new FloatRange(80f, 90f);
    private static final FloatRange SLOW = new FloatRange(36.99999f, 38.2f);
    private static final IntRange FAST_TOLERANCE_RANGE = new IntRange(2, 9);
    private static int currentTolerance = TOLERANCE.random();

    public static Rotation smooth(Rotation target) {
        final var distance = Math.abs(mc.player.getYaw() - target.yaw());
        // SKIDDING!
        final var tolerance = FAST_TOLERANCE_RANGE.in(currentTolerance) ? FAST.random() : SLOW.random();
        if (mc.player.isOnGround()) {
            currentTolerance = TOLERANCE.random();
            return target;
        }
        if (distance > tolerance) {
            if (currentTolerance > 0) currentTolerance--;
            currentTolerance = Math.max(currentTolerance, 1);
            float smoothedYaw = RotationUtil.smoothRot(mc.player.getYaw(), target.yaw(), tolerance);
            return target.withYaw(smoothedYaw);
        } else {
            return target;
        }
    }
}
