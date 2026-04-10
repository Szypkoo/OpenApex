package lol.apex.feature.module.implementation.movement.speed;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;
import lol.apex.util.player.PlayerUtil;

public class VulcanSpeed extends SubModule {
    public VulcanSpeed() {
        super("Vulcan");
    }

     private static final float speed = 0.2499f;

    public static void onMove(PlayerMoveEvent event) {
        MoveUtil.modifySpeed(event, speed);
    }

    public static void onTick(ClientTickEvent event) {
        if(mc.player.isOnGround() && MoveUtil.isMoving()) {
            PlayerUtil.jump();
        }
    }
}
