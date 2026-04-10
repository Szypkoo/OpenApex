package lol.apex.util.rotation;

import lol.apex.util.CommonVars;
import lombok.experimental.UtilityClass;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@UtilityClass
public final class BlockRotationUtil implements CommonVars {
    // catgpt yayyyy
    public static @Nullable Rotation getRotationTowardsBlock(@NonNull BlockHitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.MISS) return null;
        final var hitPos = hitResult.getPos();
        final var eyePos = mc.player.getEyePos();

        final var dx = hitPos.x - eyePos.x;
        final var dy = hitPos.y - eyePos.y;
        final var dz = hitPos.z - eyePos.z;

        final var yaw = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(-dx, dz)));

        final var horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        final var pitch = (float) MathHelper.wrapDegrees(Math.toDegrees(-Math.atan2(dy, horizontalDistance)));

        return new Rotation(yaw, pitch);
    }

}
