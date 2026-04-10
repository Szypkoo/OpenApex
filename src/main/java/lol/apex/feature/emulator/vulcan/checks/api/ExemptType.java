package lol.apex.feature.emulator.vulcan.checks.api;

import lol.apex.util.CommonVars;

import java.util.function.BooleanSupplier;

public enum ExemptType implements CommonVars {
    WATERLOGGED(() -> {
        if (mc.player == null) return false;
        final var vehicle = mc.player.getVehicle();
        if (vehicle == null) return false;
        // I think this is what is supposed to happen? (this is used in BoatFlyB to give +0.1 max speed)
        return vehicle.isSubmergedInWater() || vehicle.isTouchingWater();
    });

    private final BooleanSupplier run;

    ExemptType(BooleanSupplier run) {
        this.run = run;
    }

    public final boolean run() {
        return this.run.getAsBoolean();
    }
}
