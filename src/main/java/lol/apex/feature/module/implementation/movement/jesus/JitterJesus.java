package lol.apex.feature.module.implementation.movement.jesus;

import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.movement.JesusModule;
import lol.apex.util.player.MoveUtil;
import lol.apex.util.player.PlayerUtil;

public class JitterJesus extends SubModule {
    public JitterJesus() {
        super("Jitter");
    }
    public static void onMove(JesusModule parent, PlayerMoveEvent event) {
        if(mc.player.isTouchingWater() || PlayerUtil.isOverLiquid()) {
            int pattern = (mc.player.age + (int)(mc.player.getX() * 100)) % 7;

            double yOffset = 0;
            switch (pattern) {
                case 0: yOffset = parent.jitterStrength.getValue() * 1.2; break;
                case 1: yOffset = -parent.jitterStrength.getValue() * 0.8; break;
                case 2: yOffset = parent.jitterStrength.getValue() * 0.5; break;
                case 3: yOffset = -parent.jitterStrength.getValue() * 1.5; break;
                case 4: yOffset = 0; break;
                case 5: yOffset = parent.jitterStrength.getValue(); break;
                case 6: yOffset = -parent.jitterStrength.getValue(); break;
            }

            yOffset += (Math.random() * 0.0002 - 0.0001);
            event.setY(event.getY() + yOffset);

            mc.player.setVelocity(mc.player.getVelocity().x, 0.08, mc.player.getVelocity().z);

            if(MoveUtil.isMoving()) {
                double speed = parent.speed.getValue();

                if(mc.player.age % 11 == 0) {
                    speed *= 0.97;
                } else if (mc.player.age % 17 == 0) {
                    speed *= 1.03;
                }

                speed += (Math.random() * 0.01 - 0.02);
                MoveUtil.modifySpeed(event, speed);
            }

            mc.player.setOnGround(Math.abs(yOffset) < 0.0008);
        }
    }
}
