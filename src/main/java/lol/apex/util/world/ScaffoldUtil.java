package lol.apex.util.world;

import lol.apex.util.CommonVars;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SnowBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.jspecify.annotations.Nullable;

@UtilityClass
public final class ScaffoldUtil implements CommonVars {
    public record PlacementPattern(int offsetX, int offsetY, int offsetZ, boolean additive) {}
    public record PosFace(
            BlockPos bp,
            Direction dir
    ) {}

    public static int getBlockStateIndex(BlockState state) {
        final var block = state.getBlock();
        final var stateManager = block.getStateManager();
        final var states = stateManager.getStates();
        return states.indexOf(state);
    }

    public static boolean isValidBlockPosition(BlockPos pos) {
        if (pos == null) {
            return false;
        }

        Block block = mc.world.getBlockState(pos).getBlock();

        if (!block.getDefaultState().isOpaque() && block.getDefaultState().isReplaceable()) {
            return false;
        }

        return !(block instanceof SnowBlock) || getBlockStateIndex(mc.world.getBlockState(pos)) != 0;
    }

    public static Vec3d getRandomizedHitVec(BlockPos blockPos, Direction side) {
        double x = (double) blockPos.getX() + 0.5;
        double y = (double) blockPos.getY() + 0.5;
        double z = (double) blockPos.getZ() + 0.5;

        x += (double) side.getOffsetX() / 2.0;
        y += (double) side.getOffsetY() / 2.0;
        z += (double) side.getOffsetZ() / 2.0;

        return new Vec3d(x, y, z);
    }

    public static @Nullable PosFace findPlaceableNeighbor(BlockPos pos, boolean disallowDownFace) {
        Vec3i[] baseOffsets = new Vec3i[]{
                new Vec3i(0, 0, 0), new Vec3i(-1, 0, 0),
                new Vec3i(1, 0, 0), new Vec3i(0, 0, 1),
                new Vec3i(0, 0, -1)
        };
        PlacementPattern[] searchPatterns = new PlacementPattern[]{
                new PlacementPattern(1, 1, 1, false),
                new PlacementPattern(2, 1, 2, false),
                new PlacementPattern(3, 1, 3, false),
                new PlacementPattern(4, 1, 4, false),
                new PlacementPattern(0, -1, 0, true)
        };

        for (PlacementPattern pattern : searchPatterns) {
            for (Vec3i baseOffset : baseOffsets) {
                Vec3i candidateOffset = !pattern.additive
                        ? new Vec3i(baseOffset.getX() * pattern.offsetX, baseOffset.getY() * pattern.offsetY, baseOffset.getZ() * pattern.offsetZ)
                        : new Vec3i(baseOffset.getX() + pattern.offsetX, baseOffset.getY() + pattern.offsetY, baseOffset.getZ() + pattern.offsetZ);

                for (Direction face : Direction.values()) {
                    if ((face != Direction.DOWN || !disallowDownFace) && isValidBlockPosition(pos.add(candidateOffset).offset(face, -1))) {
                        return new PosFace(pos.add(candidateOffset).offset(face, -1), face);
                    }
                }
            }
        }

        return null;
    }

}
