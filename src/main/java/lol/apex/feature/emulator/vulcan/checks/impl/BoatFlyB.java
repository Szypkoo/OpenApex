package lol.apex.feature.emulator.vulcan.checks.impl;

import lol.apex.feature.emulator.vulcan.DataListener;
import lol.apex.feature.emulator.vulcan.checks.api.ExemptType;
import lol.apex.feature.emulator.vulcan.checks.api.VulcanCheck;
import org.jspecify.annotations.Nullable;

public final class BoatFlyB extends VulcanCheck<Double> {
    private static final CheckInfo INFO = new CheckInfo(
            "BoatFly", "Horizontal", "Moving too quick horizontally."
    );
    public BoatFlyB() {
        super(5, .25, .25, INFO);
    }

    @Override
    public Double emulate(@Nullable Double fallback) {
        if (!DataListener.boat) return fallback;
        final int vehicleTicks = DataListener.vehicleTicks;
        if (vehicleTicks > 10) {
//            final Boat boatEntity = (Boat)this.data.getPlayer().getVehicle();
//            final double deltaXZ = this.data.getPositionProcessor().getVehicleDeltaXZ();
//            final double deltaY = this.data.getPositionProcessor().getVehicleDeltaY();
//            final boolean ice = this.data.getPositionProcessor().getSinceVehicleNearIceTicks() < 100;
//            final boolean liquid = this.data.getPositionProcessor().getSinceVehicleNearLiquidTicks() < 100;
//            final boolean slime = this.data.getPositionProcessor().getSinceVehicleNearSlimeTicks() < 100;
//            final boolean bubbleColumn = this.data.getPositionProcessor().getSinceVehicleNearBubbleColumnTicks() < 100;
//            final int boatsAround = this.data.getPositionProcessor().getBoatsAround();
//            if (ServerUtil.isHigherThan1_13() && !boatEntity.hasGravity()) {
//                return;
//            }
//            if (Vulcan.INSTANCE.getFishingRodPulledBoats().containsKey(boatEntity.getEntityId())) {
//                return;
//            }
            double max = 0.25;
            if (this.isExempt(ExemptType.WATERLOGGED)) {
                max += 0.1;
            }
            return max;
//            final boolean invalid = deltaXZ > max && deltaY > -0.05 && !liquid && !slime && !bubbleColumn && !ice;
//            if (invalid && boatsAround == 1) {
//                if (this.increaseBuffer() > this.MAX_BUFFER) {
//                    this.fail("deltaXZ=" + deltaXZ + " deltaY=" + deltaY);
//                    if (Config.BOAT_FLY_B_KICKOUT) {
//                        Bukkit.getScheduler().runTask(Vulcan.INSTANCE.getPlugin(), () -> this.data.getPlayer().leaveVehicle());
//                    }
//                }
//            }
//            else {
//                this.decayBuffer();
//            }
        }
        return fallback;
    }
}
