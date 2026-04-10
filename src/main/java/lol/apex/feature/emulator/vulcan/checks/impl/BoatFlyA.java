package lol.apex.feature.emulator.vulcan.checks.impl;

import lol.apex.feature.emulator.vulcan.checks.api.VulcanCheck;
import org.jspecify.annotations.Nullable;

public final class BoatFlyA extends VulcanCheck<Double> {
    private static final CheckInfo INFO = new CheckInfo(
            "BoatFly", "Vertical",
            "Moving upwards in a boat."
    );
    public BoatFlyA() {
        super(4, .33, .125, INFO);
    }

    @Override
    public Double emulate(@Nullable Double fallback) {
        final var v = fallback == null ? 0.01 : fallback;
        return Math.min(v, 0.01); // if the deltaY >= 0.01
    }
}
