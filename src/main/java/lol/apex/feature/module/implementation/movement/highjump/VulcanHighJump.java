package lol.apex.feature.module.implementation.movement.highjump;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.movement.HighJumpModule;
import lol.apex.util.player.MoveUtil;

public class VulcanHighJump extends SubModule {
    public VulcanHighJump() {
        super("Vulcan");
    }

    private static boolean shouldGlide = false;

    public static void onTick(HighJumpModule parent, ClientTickEvent event) {
        if (mc.options.jumpKey.isPressed() && mc.player.isOnGround()) {
            MoveUtil.setMotionY(parent.motion.getValue());
          //  MoveUtils.setMotionY(0.0);

            shouldGlide = true;
        }

        if(parent.glide.getValue() && shouldGlide) {
            if (mc.player.isOnGround()) {
                shouldGlide = false;
                return;
            }

            if (mc.player.fallDistance > 0) {
                if (mc.player.age % 2 == 0) {
                    MoveUtil.setMotionY(-0.155);
                }
            } else {
                MoveUtil.setMotionY(-0.1);
            }
        }
    }

    public static void reset() {
        shouldGlide = false;
    }
}
