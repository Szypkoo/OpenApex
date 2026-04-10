package lol.apex.feature.module.implementation.movement.speed;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;
import lol.apex.util.player.PlayerUtil;

public class KarhuSpeed extends SubModule {
    public KarhuSpeed() {
        super("Karhu");
    }

    private static int airTicks = 0;
    private static int jumps = 0;

    public static void onTick(ClientTickEvent event) {
        if(mc.player.isOnGround()) {
            PlayerUtil.jump();
            jumps++;
            airTicks = 0;
        } else {
            airTicks ++;
        }

        if(airTicks == 1) {
            switch(jumps) {
                case 2:
                    MoveUtil.setMotionY(0.21);
                    break;

                case 3:
                    MoveUtil.setMotionY(0.25);
                    break;

                case 5:
                    MoveUtil.setMotionY(0.17);
                    break;
            }
        }
    }
}
