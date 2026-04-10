package lol.apex.feature.module.implementation.movement.speed;

import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;

public class MatrixSpeed extends SubModule {

    public MatrixSpeed() {
        super("Matrix");
    }

    public static void onMove(PlayerMoveEvent event) {
        if(!MoveUtil.isMovingForward()) return;

        if(!mc.player.isOnGround() && (MoveUtil.getMotionY() == -0.4448259643949201)) {
            double getmx = MoveUtil.getMotionX();
            double getmz = MoveUtil.getMotionZ();
            getmx *= 2.0d;
            getmz *= 2.0d;
            MoveUtil.setMotionX(getmx);
            MoveUtil.setMotionZ(getmz);
        }
    }
}
