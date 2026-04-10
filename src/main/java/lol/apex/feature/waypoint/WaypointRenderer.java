package lol.apex.feature.waypoint;

import lol.apex.Apex;
import lol.apex.event.render.RenderWorldEvent;
import lol.apex.feature.file.impl.WaypointsFile;
import lol.apex.feature.module.implementation.visual.WaypointsModule;
import lol.apex.manager.implementation.WaypointManager;
import lol.apex.util.CommonUtil;
import lol.apex.util.CommonVars;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class WaypointRenderer implements CommonVars {

    private static final float BASE_SCALE = 0.025f;

    public void initialize() {
        WaypointsFile.DEFAULT.loadFromFile();
    }

    public static void onRenderWorld(RenderWorldEvent event) {
        var waypoints = WaypointManager.getWaypoints();
        if (waypoints.isEmpty()) return;

        var camera = event.renderState.cameraRenderState;
        var cameraPos = camera.pos;
        VertexConsumerProvider.Immediate provider = mc.worldRenderer.bufferBuilders.getEntityVertexConsumers();
        MatrixStack matrices = new MatrixStack();
        TextRenderer textRenderer = mc.textRenderer;
        int bgAlpha = (int) (mc.options.getTextBackgroundOpacity(0.25f) * 255.0f) << 24;

        for (var waypoint : waypoints) {
            if (!waypoint.isVisible()) continue;

            Vec3d wpPos = new Vec3d(waypoint.getX(), waypoint.getY() + 1.0, waypoint.getZ());

            double dx = mc.player.getX() - wpPos.x;
            double dz = mc.player.getZ() - wpPos.z;
            double distance = Math.sqrt(dx * dx + dz * dz);

            matrices.push();
            matrices.translate(wpPos.x - cameraPos.x, wpPos.y - cameraPos.y, wpPos.z - cameraPos.z);
            matrices.multiply(camera.orientation);

            float scale = Math.clamp(BASE_SCALE * Math.max(1.0f, (float) distance / 8.0f), BASE_SCALE, BASE_SCALE * 4.0f);
            matrices.scale(scale, -scale, scale);

            String name = waypoint.getName();
            String distText = String.format("%.1f blocks", distance);

            float maxWidth = Math.max(textRenderer.getWidth(name), textRenderer.getWidth(distText));

            if (background()) {
                textRenderer.draw("", -maxWidth / 2f - 2, -2, 0x80000000, false, matrices.peek().getPositionMatrix(), provider,
                        TextRenderer.TextLayerType.SEE_THROUGH, 0, 0xF000F0);
            }

            int startColor = CommonUtil.getFirstClientColor();
            int endColor = CommonUtil.getSecondClientColor();

            drawGradientText(textRenderer, provider, matrices, name, -textRenderer.getWidth(name) / 2f, startColor, endColor, 0);
            drawGradientText(textRenderer, provider, matrices, distText, -textRenderer.getWidth(distText) / 2f, startColor, endColor, 10);

            matrices.pop();
        }

        provider.draw();
    }

    private static void drawGradientText(TextRenderer textRenderer, VertexConsumerProvider provider, MatrixStack matrices,
                                         String text, float x, int startColor, int endColor, float yOffset) {
        for (int i = 0; i < text.length(); i++) {
            float t = text.length() == 1 ? 0f : (float) i / (text.length() - 1);
            int color = interpolateRGB(startColor, endColor, t);
            String c = String.valueOf(text.charAt(i));
            textRenderer.draw(c, x, yOffset, color, false,
                    matrices.peek().getPositionMatrix(), provider,
                    TextRenderer.TextLayerType.SEE_THROUGH, background() ? 0x80000000 : 0, 0xF000F0);
            x += textRenderer.getWidth(c);
        }
    }

    private static int interpolateRGB(int start, int end, float t) {
        int r = (int) (((start >> 16) & 0xFF) * (1 - t) + ((end >> 16) & 0xFF) * t);
        int g = (int) (((start >> 8) & 0xFF) * (1 - t) + ((end >> 8) & 0xFF) * t);
        int b = (int) (((start) & 0xFF) * (1 - t) + ((end) & 0xFF) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private static boolean background() {
        WaypointsModule module = Apex.moduleManager.getByClass(WaypointsModule.class);
        return module != null && module.background.getValue();
    }
}