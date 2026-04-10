package lol.apex.feature.module.implementation.movement.speed;

import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.movement.SpeedModule;
import lol.apex.util.player.MoveUtil;

public class VanillaSpeed extends SubModule {
    public VanillaSpeed() {
        super("Vanilla");
    }
    public static void onMove(SpeedModule parent, PlayerMoveEvent event) {
        MoveUtil.modifySpeed(event, parent.speed.getValue());
    }
}
