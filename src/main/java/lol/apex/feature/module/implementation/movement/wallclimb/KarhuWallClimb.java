package lol.apex.feature.module.implementation.movement.wallclimb;

import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;
import net.minecraft.util.math.MathHelper;

public class KarhuWallClimb extends SubModule {
    public KarhuWallClimb() {
        super("Karhu");
    }

    public static void onMove(PlayerMoveEvent event) {
        if(mc.player.horizontalCollision && mc.player.age % 2 == 0) {
            mc.player.setOnGround(true);
            MoveUtil.setMotionY(0.42F);
        }
        double direction = MoveUtil.direction();
        event.setX(event.getX() - (double)(-MathHelper.sin((float)direction) * 0.1f));
        event.setZ(event.getZ() - (double)(-MathHelper.sin((float)direction) * 0.1f));
    }
}
