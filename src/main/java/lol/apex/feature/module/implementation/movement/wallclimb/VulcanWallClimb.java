package lol.apex.feature.module.implementation.movement.wallclimb;

import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;
import net.minecraft.util.math.MathHelper;

public class VulcanWallClimb extends SubModule {
    public VulcanWallClimb() {
        super("Vulcan");
    }
    public static void onMove(PlayerMoveEvent event) {
        if(mc.player.horizontalCollision) {
            if(mc.player.age % 2 == 0) {
                mc.player.setOnGround(true); 
                mc.player.setVelocity(mc.player.getVelocity().x, 0.42f, mc.player.getVelocity().z);
            } 

            float yaw = (float)Math.toRadians(mc.player.getYaw()); 

            event.setX(event.getX() - MathHelper.sin(yaw) * 0.1f); 
            event.setZ(event.getZ() + MathHelper.cos(yaw) * 0.1f);
        }
    }
}
