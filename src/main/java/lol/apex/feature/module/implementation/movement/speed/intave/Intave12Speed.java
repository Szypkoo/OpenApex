package lol.apex.feature.module.implementation.movement.speed.intave;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;
import lol.apex.util.player.PlayerUtil;

public class Intave12Speed extends SubModule {

    private static final double SPEED = 0.27d;

    public Intave12Speed() {
        super("Intave12", null, "Intave");
    }

    public static void onMove(PlayerMoveEvent event) {
        MoveUtil.modifySpeed(event, SPEED);
    }

    public static void onTick(ClientTickEvent event) {
        if(mc.player.isOnGround() && MoveUtil.isMoving()) {
            PlayerUtil.jump();
        }
    }
}
