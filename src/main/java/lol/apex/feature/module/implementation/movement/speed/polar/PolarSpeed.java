package lol.apex.feature.module.implementation.movement.speed.polar;

import lol.apex.event.player.PlayerJumpingFactorEvent;
import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;

public class PolarSpeed extends SubModule {
    public PolarSpeed() {
        super("Polar", "1% polar speed.");
    }
    private static int tick = 0;
    
    public static void onMove(PlayerMoveEvent event) {
        if(mc.player.getVelocity().y <= -0.1) {
            tick ++; 
            if(tick % 2 == 0) {
                MoveUtil.setMotionY(-0.1);
            } else {
                MoveUtil.setMotionY(-0.16);
            }
        } else {
            tick = 0;
        }
    } 

    public static void onJumpChangeFactor(PlayerJumpingFactorEvent event) {
        event.setCancelled(true);
        event.setJumpingFactor(0.0265f); 
        mc.player.setOnGround(false);
    }
}
