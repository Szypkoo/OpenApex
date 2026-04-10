package lol.apex.util.world;

import lol.apex.util.CommonVars;
import lombok.experimental.UtilityClass;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

@UtilityClass
public final class RaytraceUtil implements CommonVars {
    public static BlockHitResult rayTraceDown(double reach) {
        Vec3d start = mc.player.getEyePos();

        Vec3d end = new Vec3d(
                mc.player.lastX,
                start.y - reach,
                mc.player.lastZ
        );

        return mc.world.raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                mc.getCameraEntity()
        ));
    }

}
