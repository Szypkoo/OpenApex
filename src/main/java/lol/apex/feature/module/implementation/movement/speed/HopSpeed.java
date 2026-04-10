package lol.apex.feature.module.implementation.movement.speed;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModuleWithParent;
import lol.apex.feature.module.implementation.movement.SpeedModule;
import lol.apex.util.player.MoveUtil;
import lol.apex.util.player.PlayerUtil;

public class HopSpeed extends SubModuleWithParent<SpeedModule> {
    public HopSpeed(SpeedModule parent) {
        super(parent, "Hop", null, "Generic");
    }
    public static void onMove(SpeedModule parent, PlayerMoveEvent event) {
        MoveUtil.modifySpeed(event, parent.speed.getValue());
    }

    public static void onTick(ClientTickEvent event) {
        if(mc.player.isOnGround() && MoveUtil.isMoving()) {
            PlayerUtil.jump();
        }
    }
}
