package lol.apex.util.skidded;

import imgui.ImGui;
import lol.apex.feature.ui.imgui.IImWrapper;
import lol.apex.util.CommonVars;
import lol.apex.util.annotation.Pasted;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.*;

import java.lang.Math;
import java.util.Arrays;
import java.util.List;

@Pasted("From Opal")
public class ProjectionUtil implements CommonVars {
    public static Frustum createFrustum(float tickDelta) {
        var camera = mc.gameRenderer.getCamera();

        var rotation = camera.getRotation().conjugate(new Quaternionf());
        var positionMatrix = new Matrix4f().rotation(rotation);

        var fov = mc.gameRenderer.getFov(camera, tickDelta, true);

        return mc.worldRenderer.setupFrustum(
                positionMatrix,
                mc.gameRenderer.getBasicProjectionMatrix(
                        Math.max(fov, mc.options.getFov().getValue())
                ),
                camera.getCameraPos()
        );
    }

    public static Vector4d getEntityPositionsOn2D(IImWrapper wrapper, Entity entity, float tickDelta) {
        var size = wrapper.getIO().getDisplaySize();
        int[] viewport = new int[]{
                0, 0,
                (int) size.x,
                (int) size.y
        };

        MatrixStack matrixStack = createMatrixStack(tickDelta);
        Matrix4f projectionMatrix = matrixStack.peek().getPositionMatrix();

        Box boundingBox = getInterpolatedBoundingBox(entity, tickDelta);
        List<Vec3d> corners = getBoxBounds(boundingBox);

        Vector4d projected = null;
        Vector4f windowCoords = new Vector4f();
        for (Vec3d corner : corners) {
            Vector3f camRelative = new Vector3f(
                    (float) (corner.x - mc.gameRenderer.getCamera().getCameraPos().x),
                    (float) (corner.y - mc.gameRenderer.getCamera().getCameraPos().y),
                    (float) (corner.z - mc.gameRenderer.getCamera().getCameraPos().z)
            );

            projectionMatrix.project(camRelative, viewport, windowCoords);
            windowCoords.y = viewport[3] - windowCoords.y; // flip Y

            if (windowCoords.w != 1.0f) continue;

            if (projected == null) {
                projected = new Vector4d(windowCoords.x, windowCoords.y, 0, 0);
            } else {
                projected.x = Math.min(projected.x, windowCoords.x);
                projected.y = Math.min(projected.y, windowCoords.y);
                projected.z = Math.max(projected.z, windowCoords.x);
                projected.w = Math.max(projected.w, windowCoords.y);
            }
        }

        if (projected == null) return null;

        double scale = mc.getWindow().getScaleFactor();
        projected.x /= scale;
        projected.y /= scale;
        projected.z /= scale;
        projected.w /= scale;

        projected.z -= projected.x;
        projected.w -= projected.y;

        return projected;
    }

    private static List<Vec3d> getBoxBounds(Box box) {
        return Arrays.asList(
                new Vec3d(box.minX, box.minY, box.minZ),
                new Vec3d(box.minX, box.maxY, box.minZ),
                new Vec3d(box.maxX, box.minY, box.minZ),
                new Vec3d(box.maxX, box.maxY, box.minZ),
                new Vec3d(box.minX, box.minY, box.maxZ),
                new Vec3d(box.minX, box.maxY, box.maxZ),
                new Vec3d(box.maxX, box.minY, box.maxZ),
                new Vec3d(box.maxX, box.maxY, box.maxZ)
        );
    }

    private static Vec3d interpolate(Entity entity, float tickDelta) {
        var camera = mc.gameRenderer.getCamera();

        double interpX = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
        double interpY = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
        double interpZ = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());

        return new Vec3d(
                interpX - camera.getCameraPos().x,
                interpY - camera.getCameraPos().y,
                interpZ - camera.getCameraPos().z
        );
    }

    private static Box getInterpolatedBoundingBox(Entity entity, float tickDelta) {
        var pos = interpolate(entity, tickDelta).add(mc.gameRenderer.getCamera().getCameraPos());

        var height = entity.getHeight();
        if (entity instanceof LivingEntity living && living.isBaby()) {
            height /= 2.0f;
        }

        var halfWidth = entity.getWidth() / 2.0f;

        var minY = pos.y;
        var maxY = pos.y + height;

        if (entity instanceof PlayerEntity player && PlayerEntityRenderer.shouldFlipUpsideDown(player)) {
            maxY = pos.y + height; // head position
            minY = maxY - height; // go downward
        }

        return new Box(
                pos.x - halfWidth,
                minY,
                pos.z - halfWidth,
                pos.x + halfWidth,
                maxY,
                pos.z + halfWidth
        ).expand(0.15, 0.15, 0.15);
    }

    private static MatrixStack createMatrixStack(float tickDelta) {
        var stack = new MatrixStack();
        var camera = mc.gameRenderer.getCamera();
        var fov = mc.gameRenderer.getFov(camera, tickDelta, true);

        stack.multiplyPositionMatrix(mc.gameRenderer.getBasicProjectionMatrix(fov));
        mc.gameRenderer.tiltViewWhenHurt(stack, camera.getLastTickProgress());

        if (mc.options.getBobView().getValue())
            mc.gameRenderer.bobView(stack, camera.getLastTickProgress());

        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180f));

        return stack;
    }
}