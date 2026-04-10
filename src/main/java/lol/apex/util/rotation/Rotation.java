package lol.apex.util.rotation;

import org.jspecify.annotations.NonNull;

/** use this instead of float[] arrays **/
public record Rotation(float yaw, float pitch) {
    public @NonNull Rotation withYaw(float yaw) {
        return new Rotation(yaw, pitch);
    }
    public @NonNull Rotation withPitch(float pitch) {
        return new Rotation(this.yaw, pitch);
    }
    public @NonNull Rotation copy(float yaw, float pitch) {
        return new Rotation(yaw, pitch);
    }
    public @NonNull Rotation copy(float yaw) {
        return new Rotation(yaw, pitch);
    }
}
