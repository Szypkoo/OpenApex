package lol.apex.util.render;

import com.mojang.blaze3d.systems.RenderSystem;
import lol.apex.util.CommonVars;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class RenderUtil implements CommonVars {

    public static final int[] lastViewport = new int[4];
    public static final Matrix4f lastProjMat = new Matrix4f();
    public static final Matrix4f lastModMat = new Matrix4f();
    public static final Matrix4f lastWorldSpaceMatrix = new Matrix4f();

    public static int getTextureId(Identifier id) {
        var texture = mc.getTextureManager().getTexture(id);
        if (texture.getGlTexture() instanceof GlTexture glTexture) {
            return glTexture.getGlId();
        }

        return -1;
    }

    public static int getPlayerSkin(PlayerEntity player) {
        if (player instanceof AbstractClientPlayerEntity clientPlayer) {
            var textures = clientPlayer.getSkin();

            Identifier skinId = textures.body().texturePath();

            return getTextureId(skinId);
        }

        return -1;
    }


    public static void drawBorder(DrawContext ctx, int x, int y, int w, int h, int color) {

        ctx.fill(x, y, x + w, y + 1, color);         // top
        ctx.fill(x, y + h - 1, x + w, y + h, color); // bottom
        ctx.fill(x, y, x + 1, y + h, color);         // left
        ctx.fill(x + w - 1, y, x + w, y + h, color); // right
    }

    public static void verticalGradientRect(DrawContext ctx, int x1, int y1, int x2, int y2, int top, int bottom) {
        ctx.fillGradient(x1, y1, x2, y2, top, bottom);
    }

    public static void verticalGradientText(DrawContext ctx, String text, int x, int y, int topColor, int bottomColor) {

        TextRenderer tr = mc.textRenderer;

        for (int i = 0; i < text.length(); i++) {

            float t = (float) i / (text.length() - 1);

            int r = (int) (((topColor >> 16) & 255) * (1 - t) + ((bottomColor >> 16) & 255) * t);
            int g = (int) (((topColor >> 8) & 255) * (1 - t) + ((bottomColor >> 8) & 255) * t);
            int b = (int) ((topColor & 255) * (1 - t) + (bottomColor & 255) * t);

            int color = 0xFF000000 | (r << 16) | (g << 8) | b;

            String c = String.valueOf(text.charAt(i));

            ctx.drawTextWithShadow(tr, c, x, y, color);

            x += tr.getWidth(c);
        }
    }

    public static void verticalGradientTextScaled(DrawContext ctx, String text, int x, int y, float size, int topColor, int bottomColor) {
        TextRenderer tr = mc.textRenderer;


        ctx.getMatrices().pushMatrix();
        ctx.getMatrices().scale(size, size);

        float scaledX = x / size;
        float scaledY = y / size;

        for (int i = 0; i < text.length(); i++) {

            float t = (float) i / (text.length() - 1);

            int r = (int) (((topColor >> 16) & 255) * (1 - t) + ((bottomColor >> 16) & 255) * t);
            int g = (int) (((topColor >> 8) & 255) * (1 - t) + ((bottomColor >> 8) & 255) * t);
            int b = (int) ((topColor & 255) * (1 - t) + (bottomColor & 255) * t);

            int color = 0xFF000000 | (r << 16) | (g << 8) | b;

            String c = String.valueOf(text.charAt(i));

            ctx.drawTextWithShadow(tr, c, (int) scaledX, (int) scaledY, color);

            scaledX += tr.getWidth(c);
        }

        ctx.getMatrices().popMatrix();
    }

    /**
     * Applies the vanilla blur post effect to a rectangular region.
     *
     * <p>The coordinates are measured in framebuffer pixels with the origin at the top-left corner
     * so it matches how Minecraft lays out GUI elements.</p>
     */
    public static void drawBlur(int x, int y, int width, int height) {
        drawBlur(x, y, width, height, 0);
    }

    public static void drawBlur(int x, int y, int width, int height, float rounding) {
        drawBlur(x, y, width, height, rounding, true, true, true, true);
    }

    public static void drawBlur(int x, int y, int width, int height, float rounding, boolean tl, boolean tr, boolean br, boolean bl) {
        if (width <= 0 || height <= 0) {
            return;
        }

        Framebuffer framebuffer = mc.getFramebuffer();
        if (framebuffer == null) {
            return;
        }

        int fbWidth = framebuffer.textureWidth;
        int fbHeight = framebuffer.textureHeight;

        int left = MathHelper.clamp(x, 0, fbWidth);
        int top = MathHelper.clamp(y, 0, fbHeight);
        int right = MathHelper.clamp(x + width, 0, fbWidth);
        int bottom = MathHelper.clamp(y + height, 0, fbHeight);

        int scissorWidth = right - left;
        int scissorHeight = bottom - top;
        if (scissorWidth <= 0 || scissorHeight <= 0) {
            return;
        }

        RenderSystem.assertOnRenderThread();

        float rTL = tl ? rounding : 0;
        float rTR = tr ? rounding : 0;
        float rBR = br ? rounding : 0;
        float rBL = bl ? rounding : 0;

        CustomBlur.drawBlur(10.0f, rTR, rBR, rTL, rBL, left, top, scissorWidth, scissorHeight);
    }

    public static int interpolateColor(int color1, int color2, float t) {

        int r = (int) (((color1 >> 16) & 255) * (1 - t) + ((color2 >> 16) & 255) * t);
        int g = (int) (((color1 >> 8) & 255) * (1 - t) + ((color2 >> 8) & 255) * t);
        int b = (int) ((color1 & 255) * (1 - t) + (color2 & 255) * t);

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public static void animatedGradientText(DrawContext ctx, String text, int x, int y, int startColor, int endColor) {
        TextRenderer tr = mc.textRenderer;

        double time = System.currentTimeMillis() / 600.0;

        for (int i = 0; i < text.length(); i++) {
            float t = (float) ((Math.sin(time + i * 0.45) + 1) / 2);
            int color = interpolateColor(startColor, endColor, t);
            String c = String.valueOf(text.charAt(i));
            ctx.drawTextWithShadow(tr, c, x, y, color);
            x += tr.getWidth(c);
        }
    }

    public static void animatedGradientTextScaled(
            DrawContext ctx,
            String text, int x, int y,
            float size, int startColor,
            int endColor) {
        TextRenderer tr = mc.textRenderer;

        ctx.getMatrices().pushMatrix();
        ctx.getMatrices().scale(size, size);

        float scaledX = x / size;
        float scaledY = y / size;

        double time = System.currentTimeMillis() / 600.0;

        for (int i = 0; i < text.length(); i++) {

            float t = (float) ((Math.sin(time + i * 0.45) + 1) / 2);
            int r = (int) (((startColor >> 16) & 255) * (1 - t) + ((endColor >> 16) & 255) * t);
            int g = (int) (((startColor >> 8) & 255) * (1 - t) + ((endColor >> 8) & 255) * t);
            int b = (int) ((startColor & 255) * (1 - t) + (endColor & 255) * t);
            int color = 0xFF000000 | (r << 16) | (g << 8) | b;
            String c = String.valueOf(text.charAt(i));
            ctx.drawTextWithShadow(tr, c, (int) scaledX, (int) scaledY, color);
            scaledX += tr.getWidth(c);
        }

        ctx.getMatrices().popMatrix();
    }

    public static void animatedVerticalGradientRect(
            DrawContext ctx,
            int x1, int y1, int x2, int y2, int color1, int color2,
            int speed
    ) {

        double time = System.currentTimeMillis() / (double) speed;

        float t = (float) ((Math.sin(time) + 1) / 2);

        int animatedTop = interpolateColor(color1, color2, t);
        int animatedBottom = interpolateColor(color2, color1, t);

        ctx.fillGradient(x1, y1, x2, y2, animatedTop, animatedBottom);
    }

    public static void animatedVerticalGradientRectWave(
            DrawContext ctx,
            int x1, int y1, int x2, int y2,
            int color1,
            int color2
    ) {
        double time = System.currentTimeMillis() / 500.0;

        float offset = (float) ((Math.sin(time) + 1) / 2);

        int top = interpolateColor(color1, color2, offset);
        int bottom = interpolateColor(color1, color2, 1 - offset);

        ctx.fillGradient(x1, y1, x2, y2, top, bottom);
    }


    public static Vec3d worldToScreen(Vec3d pos) {
        Vector4f clip = new Vector4f((float) pos.x, (float) pos.y, (float) pos.z, 1.0f);

        Matrix4f proj = new Matrix4f(lastProjMat);
        Matrix4f model = new Matrix4f(lastModMat);

        model.transform(clip);
        proj.transform(clip);

        if (clip.w <= 0.0f) {
            return new Vec3d(0, 0, -1);
        }

        float ndcX = clip.x / clip.w;
        float ndcY = clip.y / clip.w;
        float ndcZ = clip.z / clip.w;

        int width = mc.getWindow().getScaledWidth();
        int height = mc.getWindow().getScaledHeight();

        double screenX = (ndcX + 1.0) / 2.0 * width;
        double screenY = (1.0 - ndcY) / 2.0 * height;

        return new Vec3d(screenX, screenY, ndcZ);
    }

    public static void drawSolidBox(MatrixStack matrices, VertexConsumer buffer, Box box, int color) {
        MatrixStack.Entry entry = matrices.peek();

        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;

        buffer.vertex(entry, x1, y1, z1).color(color);
        buffer.vertex(entry, x2, y1, z1).color(color);
        buffer.vertex(entry, x2, y1, z2).color(color);
        buffer.vertex(entry, x1, y1, z2).color(color);

        buffer.vertex(entry, x1, y2, z1).color(color);
        buffer.vertex(entry, x1, y2, z2).color(color);
        buffer.vertex(entry, x2, y2, z2).color(color);
        buffer.vertex(entry, x2, y2, z1).color(color);

        buffer.vertex(entry, x1, y1, z1).color(color);
        buffer.vertex(entry, x1, y2, z1).color(color);
        buffer.vertex(entry, x2, y2, z1).color(color);
        buffer.vertex(entry, x2, y1, z1).color(color);

        buffer.vertex(entry, x2, y1, z1).color(color);
        buffer.vertex(entry, x2, y2, z1).color(color);
        buffer.vertex(entry, x2, y2, z2).color(color);
        buffer.vertex(entry, x2, y1, z2).color(color);

        buffer.vertex(entry, x1, y1, z2).color(color);
        buffer.vertex(entry, x2, y1, z2).color(color);
        buffer.vertex(entry, x2, y2, z2).color(color);
        buffer.vertex(entry, x1, y2, z2).color(color);

        buffer.vertex(entry, x1, y1, z1).color(color);
        buffer.vertex(entry, x1, y1, z2).color(color);
        buffer.vertex(entry, x1, y2, z2).color(color);
        buffer.vertex(entry, x1, y2, z1).color(color);
    }
}
