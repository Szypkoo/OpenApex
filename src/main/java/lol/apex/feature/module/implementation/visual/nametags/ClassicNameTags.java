package lol.apex.feature.module.implementation.visual.nametags;

import lol.apex.event.render.RenderWorldEvent;
import lol.apex.feature.module.base.SubModuleWithParent;
import lol.apex.feature.module.implementation.visual.NametagsModule;
import lol.apex.util.CommonUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class ClassicNameTags extends SubModuleWithParent<NametagsModule> {
    public ClassicNameTags(NametagsModule parent) {
        super(parent, "Classic");
    }
    private static final float SCALE = 0.035f;

    public static void onRenderWorld(NametagsModule parent, RenderWorldEvent event) {
        if (mc.player == null || mc.world == null) return;

        float tickDelta = mc.getRenderTickCounter().getTickProgress(false);
        var camera = event.renderState.cameraRenderState;
        var cameraPos = camera.pos;
        var dispatcher = mc.getEntityRenderDispatcher();
        VertexConsumerProvider.Immediate provider = mc.worldRenderer.bufferBuilders.getEntityVertexConsumers();
        MatrixStack matrices = new MatrixStack();
        int backgroundColor = ((int) (mc.options.getTextBackgroundOpacity(0.25f) * 255.0f)) << 24;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player && mc.options.getPerspective().isFirstPerson()) continue;

            String name = player.getName().getString();
            float x = -mc.textRenderer.getWidth(name) / 2.0f;
            int light = dispatcher.getLight(player, tickDelta);

            var pos = player.getLerpedPos(tickDelta);
            double y = pos.y + player.getHeight() + (player.isSneaking() ? 0.5f : 0.6f);
            float scale = getScale(parent, pos, cameraPos);

            matrices.push();
            matrices.translate(
                    pos.x - cameraPos.x,
                    y - cameraPos.y,
                    pos.z - cameraPos.z
            );
            matrices.multiply(camera.orientation);
            matrices.scale(scale, -scale, scale);

            mc.textRenderer.draw(
                    name,
                    x,
                    0.0f,
                    -1,
                    false,
                    matrices.peek().getPositionMatrix(),
                    provider,
                    TextRenderer.TextLayerType.SEE_THROUGH,
                    backgroundColor,
                    light
            );

            drawGradientText(provider, matrices, name, x, light);
            matrices.pop();
        }

        provider.draw();
    }

    private static float getScale(NametagsModule parent, net.minecraft.util.math.Vec3d entityPos, net.minecraft.util.math.Vec3d cameraPos) {
        if (!parent.scaleByDistance.getValue()) return SCALE;

        float distance = (float) entityPos.distanceTo(cameraPos);
        return Math.clamp(SCALE * Math.max(1.0f, distance / 8.0f), SCALE, SCALE * 4.0f);
    }

    private static void drawGradientText(VertexConsumerProvider provider, MatrixStack matrices, String text, float x, int light) {
        for (int i = 0; i < text.length(); i++) {
            float t = text.length() == 1 ? 0.0f : (float) i / (text.length() - 1);
            int color = interpolateColor(CommonUtil.getFirstClientColor(),
                    CommonUtil.getSecondClientColor(), t);
            String character = String.valueOf(text.charAt(i));

            mc.textRenderer.draw(
                    character,
                    x,
                    0.0f,
                    color,
                    false,
                    matrices.peek().getPositionMatrix(),
                    provider,
                    TextRenderer.TextLayerType.SEE_THROUGH,
                    0,
                    light
            );

            x += mc.textRenderer.getWidth(character);
        }
    }

    private static int interpolateColor(int startColor, int endColor, float t) {
        int r = (int) (((startColor >> 16) & 255) * (1.0f - t) + ((endColor >> 16) & 255) * t);
        int g = (int) (((startColor >> 8) & 255) * (1.0f - t) + ((endColor >> 8) & 255) * t);
        int b = (int) ((startColor & 255) * (1.0f - t) + (endColor & 255) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
