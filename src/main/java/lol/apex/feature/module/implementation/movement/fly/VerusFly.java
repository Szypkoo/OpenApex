package lol.apex.feature.module.implementation.movement.fly;

import lol.apex.event.client.PreMotionEvent;
import lol.apex.feature.module.base.SubModuleWithParent;
import lol.apex.feature.module.implementation.movement.FlyModule;
import lol.apex.util.player.MoveUtil;

public class VerusFly extends SubModuleWithParent<FlyModule> {
    public VerusFly(FlyModule parent) {
        super(parent, "Verus", "A fly for verus anticheat.", "Verus");
    }

    public static void onPreMotion(PreMotionEvent event) {
        MoveUtil.setMotionY(-0.078400001525878);
        if(!mc.player.isOnGround()) {
            MoveUtil.setSpeedNoEvent(0.37d);
        }
    }
}
