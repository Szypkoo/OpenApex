package lol.apex.feature.module.implementation.movement.jesus;

import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.movement.JesusModule;
import lol.apex.util.world.WorldUtil;
import lol.apex.util.player.MoveUtil;
import lol.apex.util.player.PlayerUtil;
import net.minecraft.registry.tag.FluidTags;

public class MatrixJesus extends SubModule {
    public MatrixJesus() {
        super("Matrix");
    }
    public static void onMove(JesusModule parent, PlayerMoveEvent event) {
        double height =
                WorldUtil.fluidHeight(mc.player.getBoundingBox(), FluidTags.WATER);
        if(mc.player.isTouchingWater() || PlayerUtil.isOverLiquid()) {
            if(!MoveUtil.isMoving()) {
                return;
            }

            if(height > 0.0 && height <= 1.0) {
                MoveUtil.setMotionY(0.13);
            }
        }
    }
}
