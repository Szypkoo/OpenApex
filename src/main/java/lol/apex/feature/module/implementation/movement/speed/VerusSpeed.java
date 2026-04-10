package lol.apex.feature.module.implementation.movement.speed;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;
import lol.apex.util.player.PlayerUtil;

public class VerusSpeed extends SubModule {
    public VerusSpeed() {
        super("Verus");
    }

    private static float speed = 0.3772027f;

    public static void onMove(PlayerMoveEvent event) {
        MoveUtil.modifySpeed(event, speed);
    }

    public static void onTick(ClientTickEvent event) {
        if(mc.player.isOnGround() && MoveUtil.isMoving()) {
            PlayerUtil.jump();
        }
    }
}
