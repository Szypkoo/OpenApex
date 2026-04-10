package lol.apex.util.world;

import lol.apex.util.CommonVars;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@UtilityClass
public class WorldUtil implements CommonVars {
    public static double fluidHeight(Box box, TagKey<Fluid> tag) {
        int minX = MathHelper.floor(box.minX + 0.001);
        int maxX = MathHelper.ceil(box.maxX - 0.001);
        int minY = MathHelper.floor(box.minY + 0.001);
        int maxY = MathHelper.ceil(box.maxY - 0.001);
        int minZ = MathHelper.floor(box.minZ + 0.001);
        int maxZ = MathHelper.ceil(box.maxZ - 0.001);
        double maxHeight = 0.0;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    FluidState fluidState = mc.world.getFluidState(new BlockPos(x, y, z));
                    if (fluidState.isIn(tag)) {
                        maxHeight = Math.max(maxHeight, y + fluidState.getHeight() - box.minY);
                    }
                }
            }
        }

        return maxHeight;
    }


    public static boolean isShieldFacingAway(PlayerEntity player) {
        if (mc.player != null && player != null) {
            Vec3d playerPos = mc.player.getEntityPos();
            Vec3d targetPos = player.getEntityPos();

            Vec3d directionToPlayer = playerPos.subtract(targetPos).normalize();

            float yaw = player.getYaw();
            float pitch = player.getPitch();
            Vec3d facingDirection = new Vec3d(
                    -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
                    -Math.sin(Math.toRadians(pitch)),
                    Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))
            ).normalize();

            double dotProduct = facingDirection.dotProduct(directionToPlayer);

            return dotProduct < 0;
        }
        return false;
    }
}
