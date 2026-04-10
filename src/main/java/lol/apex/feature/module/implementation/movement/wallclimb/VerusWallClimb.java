package lol.apex.feature.module.implementation.movement.wallclimb;

import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.PlayerUtil;

public class VerusWallClimb extends SubModule {
    public VerusWallClimb() {
        super("Verus");
    }
    public static void onMove(PlayerMoveEvent event) {
        if (mc.player.age % 3 == 0) {
            if(mc.player.horizontalCollision && mc.player.fallDistance < 1) {
                PlayerUtil.jump();
            }
        }
    }
}
