package lol.apex.feature.emulator.vulcan.checks.impl;

import lol.apex.feature.emulator.vulcan.DataListener;
import lol.apex.feature.emulator.vulcan.checks.api.VulcanCheck;
import org.jspecify.annotations.Nullable;

import static lol.apex.feature.emulator.vulcan.DataListener.*;

public final class BoatFlyC extends VulcanCheck<Double> {
    private static final CheckInfo INFO = new CheckInfo("BoatFly", "Hover", "Hovering in a boat.");
    private int lastWaterTick = -1;
    /**
     * Since they calculate the acceleration with `Math.abs(deltaY - lastDeltaY)`,
     * we can "hover" by going up the required acceleration and then down. While this isn't stable,
     * you can always just like hide it from the user by doing it at the packet level.
     **/
    private boolean flip = false;
    /** when not exempt, the check requires you to be accelerating your deltaY `>=` 0.025 **/
    private static final double REQUIRED_ACCEL = 0.025;

    public BoatFlyC() {
        super(5, .25, .25, INFO);
    }

    @Override
    public Double emulate(@Nullable Double fallback) {
        if (vehicleTicks > 10 && boat) {
            if (DataListener.lastMove == null || mc.player == null) return fallback;
            if (vehicleAirTicks <= 5) return fallback;
//            final double deltaY = DataListener.lastMove.position().y;
            final double lastDeltaY = DataListener.lastMove.position().y;
//            final double acceleration = Math.abs(deltaY - lastDeltaY);
//            final int airTicks = DataListener.vehicleAirTicks;
//            final boolean invalid = deltaY > -0.05 && acceleration < 0.025 && airTicks > 5;
//            if (this.data.getPositionProcessor().isVehicleNearEntity()) {
//                return;
//            }
            final var vehicle = mc.player.getVehicle();
            if (vehicle == null) return fallback;
            if (vehicle.hasNoGravity()) {
                return fallback;
            }
            if (lastWaterTick - vehicle.age < 50 || vehicle.isTouchingWater() || vehicle.isPartlyTouchingWater()) {
                lastWaterTick = vehicle.age;
                return fallback;
            }
            flip = !flip;
            final var solutionDeltaY = lastDeltaY - (flip ? -REQUIRED_ACCEL : REQUIRED_ACCEL);
            final var solAccel = Math.abs(solutionDeltaY - lastDeltaY);
            if (solAccel < REQUIRED_ACCEL) {
                this.fail("Solution fails check: acceleration is " + solAccel + " but check requires " + REQUIRED_ACCEL);
            }
            return solutionDeltaY;
//            TODO(BoatFly/vulcan't/exempts): I can't be asked to do allat
//            final boolean ice = this.data.getPositionProcessor().getSinceVehicleNearIceTicks() < 50;
//            final boolean liquid = this.data.getPositionProcessor().getSinceVehicleNearLiquidTicks() < 50; // done
//            final boolean slime = this.data.getPositionProcessor().getSinceVehicleNearSlimeTicks() < 50;
//            final boolean bubbleColumn = this.data.getPositionProcessor().getSinceVehicleNearBubbleColumnTicks() < 50;
//            final boolean piston = this.data.getPositionProcessor().getSinceVehicleNearPistonTicks() < 50;
//            final int boatsAround = this.data.getPositionProcessor().getBoatsAround();
        }
        return fallback;
    }
}
