package lol.apex.feature.emulator.vulcan.checks.impl;

import lol.apex.feature.emulator.vulcan.checks.api.VulcanCheck;
import org.jspecify.annotations.Nullable;

public class EntitySpeedA extends VulcanCheck<Double> {

    private static final CheckInfo INFO = new CheckInfo(
            "Entity Speed", "Limit",
            "Riding an entity too quickly."
    );

    public EntitySpeedA() {
        super(5, .5, .25, INFO);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public Double emulate(@Nullable Double fallback) {
        if (mc.player.getVehicle() == null) return fallback;
        final var liquid = mc.player.getVehicle().isTouchingWater();
        double max = 0.65;
        if (liquid) {
            max += 0.15;
        }
        return max;
    }
}
