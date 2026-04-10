package lol.apex.feature.module.implementation.movement.speed;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;
import lol.apex.util.player.PlayerUtil;

public class LegitSpeed extends SubModule {
    public LegitSpeed() {
        super("Legit");
    }

    public static void onTick(ClientTickEvent event) {

        if(mc.player.isOnGround() && MoveUtil.isMoving()) {
            PlayerUtil.jump();
        }
    }
}
