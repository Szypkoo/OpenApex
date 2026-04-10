package lol.apex.feature.emulator.vulcan.checks.api;


import lol.apex.Apex;
import lol.apex.util.CommonVars;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;

public abstract class VulcanCheck<T> implements CommonVars {
    // type is complexType because why not
    public record CheckInfo(String name, String type, String description) {}
    private double buffer;
    public final CheckInfo ci;
    public final double MAX_BUFFER;
    public final double BUFFER_DECAY;
    public final double BUFFER_MULTIPLE_ON_FLAG;

    public VulcanCheck(final double max,
                        final double multiple,
                        final double decay,
                        final CheckInfo ci) {
        this.ci = ci;
        this.MAX_BUFFER = max;
        this.BUFFER_MULTIPLE_ON_FLAG = multiple;
        this.BUFFER_DECAY = decay;
    }

    public abstract T emulate(@Nullable T fallback);

    public void fail(final @NonNull Object info) {
        this.multiplyBuffer(this.BUFFER_MULTIPLE_ON_FLAG);
        Apex.sendChatMessage(String.format("Vulcan't flag: %s (%s): %s", ci.name, ci.type, info));
    }

    public void fail() {
        this.multiplyBuffer(this.BUFFER_MULTIPLE_ON_FLAG);
        Apex.sendChatMessage(String.format("Vulcan't flag: %s (%s)", ci.name, ci.type));
    }

    protected boolean isExempt(final ExemptType... exemptTypes) {
        return Arrays.stream(exemptTypes).allMatch(ExemptType::run);
    }

    public double increaseBuffer() {
        return this.buffer = Math.min(10000.0, this.buffer + 1.0);
    }

    public double decreaseBufferBy(final double amount) {
        return this.buffer = Math.max(0.0, this.buffer - amount);
    }

    public double increaseBufferBy(final double amount) {
        return this.buffer = Math.min(10000.0, this.buffer + amount);
    }

    public void resetBuffer() {
        this.buffer = 0.0;
    }

    public void multiplyBuffer(final double multiplier) {
        this.buffer *= multiplier;
    }

    public void decayBuffer() {
        this.decreaseBufferBy(this.BUFFER_DECAY);
    }
}