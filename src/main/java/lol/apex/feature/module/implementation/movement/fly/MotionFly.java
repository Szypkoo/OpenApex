package lol.apex.feature.module.implementation.movement.fly;

import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModuleWithParent;
import lol.apex.feature.module.implementation.movement.FlyModule;
import lol.apex.util.player.MoveUtil;

public class MotionFly extends SubModuleWithParent<FlyModule> {

    public MotionFly(FlyModule parent) {
        super(parent, "Motion");
    }

    public static void onMove(FlyModule parent, PlayerMoveEvent event) {
        double y = 0.0;

        if(mc.options.jumpKey.isPressed()) {
            y += parent.increment.getValue();
        }

        if(mc.options.sneakKey.isPressed()) {
            y -= parent.increment.getValue();
        }

        event.y = y;
        MoveUtil.setMotionY(y);
        MoveUtil.modifySpeed(event, parent.speed.getValue() * 10);
    }
}
