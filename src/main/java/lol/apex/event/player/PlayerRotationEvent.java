package lol.apex.event.player;

import lol.apex.util.rotation.Rotation;

public class PlayerRotationEvent {
    private Rotation rot;
    private boolean modified = false;

    public PlayerRotationEvent(float yaw, float pitch) {
        this.rot = new Rotation(yaw, pitch);
    }

    public PlayerRotationEvent(Rotation rot) {
        this.rot = rot;
    }

    public boolean modified() {
        return modified;
    }

    public Rotation get() {
        return this.rot;
    }

    public void set(Rotation rot) {
        modified = true;
        this.rot = rot;
    }

    public float yaw() {
        return this.rot.yaw();
    }

    public float pitch() {
        return this.rot.pitch();
    }

    public void yaw(float yaw) {
        modified = true;
        this.rot = this.rot.withYaw(yaw);
    }

    public void pitch(float pitch) {
        modified = true;
        this.rot = this.rot.withPitch(pitch);
    }
    public void angles(float yaw, float pitch) {
        yaw(yaw);
        pitch(pitch);
    }
}
