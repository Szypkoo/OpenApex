package lol.apex.feature.module.implementation.movement.speed;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;
import lol.apex.util.player.PlayerUtil;

public class GodsEyeSpeed extends SubModule {
    public GodsEyeSpeed() {
        super("GodsEye");
    }

    private static final float speed = 0.284f;

    public static void onMove(PlayerMoveEvent event) {
        if(mc.player.isBlocking()) return;

        MoveUtil.modifySpeed(event, speed);
    }

    // could possibly make it faster by changing jump height

    public static void onTick(ClientTickEvent event) {
        if(mc.player.isBlocking()) return;

        if (mc.player.isOnGround() && MoveUtil.isMoving()) {
            PlayerUtil.jump();
            MoveUtil.setMotionY(0.367);
        }
    }
}
