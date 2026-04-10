package lol.apex.feature.module.implementation.movement.speed.spartan;

import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;

public class SpartanSlowSpeed extends SubModule {
    public SpartanSlowSpeed() {
        super("SpartanSlow");
    }

    private static double speed = 0.39d;

    public static void onMove(PlayerMoveEvent event) {
        MoveUtil.modifySpeed(event, speed);
    }
}
