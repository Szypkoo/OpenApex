package lol.apex.feature.module.implementation.movement.speed;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;
import lol.apex.util.rotation.RotationUtil;

public final class ThemisSpeed extends SubModule {
    public ThemisSpeed() {
        super("Themis");
    }

    public static void onTick(ClientTickEvent event) {
        if(mc.player.isTouchingWater()) return;

        if(mc.player.isOnGround() && !mc.options.jumpKey.isPressed() && MoveUtil.isMoving()) {
            MoveUtil.boost(RotationUtil.getMovementDirectionYaw(), 0.2035);
            MoveUtil.setMotionY(1e-323);
        }
    }
}
