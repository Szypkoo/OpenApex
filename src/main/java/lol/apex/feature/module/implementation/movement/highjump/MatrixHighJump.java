package lol.apex.feature.module.implementation.movement.highjump;

import lol.apex.event.client.PreMotionEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.movement.HighJumpModule;
import lol.apex.util.player.MoveUtil;

public class MatrixHighJump extends SubModule {
    public MatrixHighJump() {
        super("Matrix");
    }

    private static int ticks = 0;
    private static int stage = 0;

    public static void onMotion(HighJumpModule parent, PreMotionEvent event) {
        if (mc.player.verticalCollision) {
            if (ticks == 1) {
                mc.player.setOnGround(false);
                event.onGround = false;
                MoveUtil.setMotionY(0.966);
                ticks = 2;
                stage = 1;
            }

            if (ticks == 10 && stage == 1) {
                ticks = 0;
                stage = 0;
            }
            ticks++;
        }
    }
}
