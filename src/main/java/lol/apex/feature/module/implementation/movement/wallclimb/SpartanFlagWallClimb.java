package lol.apex.feature.module.implementation.movement.wallclimb;

import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;

public class SpartanFlagWallClimb extends SubModule {
    public SpartanFlagWallClimb() {
        super("Spartan");
    }

    public static void onMove(PlayerMoveEvent event) {
        if(mc.player.age % 3 == 0) {
            if(mc.player.horizontalCollision && mc.player.fallDistance < 1) {
                mc.player.jump();
            }
        }
    }
}
