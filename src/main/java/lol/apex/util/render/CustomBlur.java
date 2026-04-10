package lol.apex.util.render;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import lol.apex.util.io.FileUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

public class CustomBlur {
    private static ShaderUtil shader;
    private static Framebuffer intermediateFbo;
    private static int vao;
    private static int vbo;

    private static void init() {
        if (shader == null) {
            String vertexSource = FileUtil.loadResource("vertex.glsl");
            String fragmentSource = FileUtil.loadResource("fragment.glsl");

            shader = new ShaderUtil(vertexSource, fragmentSource);

            vao = GlStateManager._glGenVertexArrays();
            vbo = GlStateManager._glGenBuffers();

            GlStateManager._glBindVertexArray(vao);
            GlStateManager._glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);

            float[] vertices = {
                    -1, -1,
                    1, -1,
                    -1, 1,
                    1, 1
            };

            var buffer = MemoryUtil.memAlloc(vertices.length * 4);
            for (float f : vertices) buffer.putFloat(f);
            buffer.flip();

            GlStateManager._glBufferData(GL30.GL_ARRAY_BUFFER, buffer, GL30.GL_STATIC_DRAW);
            MemoryUtil.memFree(buffer);

            GlStateManager._enableVertexAttribArray(0);
            GlStateManager._vertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 8, 0);

            GlStateManager._glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
            GlStateManager._glBindVertexArray(0);
        }
    }

    public static void drawBlur(float radius, float rTR, float rBR, float rTL, float rBL, int x, int y, int width, int height) {
        var mc = MinecraftClient.getInstance();
        var mainFbo = mc.getFramebuffer();

        if (intermediateFbo == null || intermediateFbo.textureWidth != mainFbo.textureWidth || intermediateFbo.textureHeight != mainFbo.textureHeight) {
            if (intermediateFbo != null) intermediateFbo.delete();
            intermediateFbo = new SimpleFramebuffer("Apex Intermediate FBO", mainFbo.textureWidth, mainFbo.textureHeight, false);
        }

        init();

        RenderSystem.assertOnRenderThread();
        int fbHeight = mainFbo.textureHeight;
        int rad = (int) Math.ceil(radius);

        int intermediateFboId = ShaderUtil.getFboId(intermediateFbo);

        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, intermediateFboId);
        GL11C.glViewport(0, 0, intermediateFbo.textureWidth, intermediateFbo.textureHeight);

        GL11C.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager._clear(GL11.GL_COLOR_BUFFER_BIT);

        GL11C.glEnable(GL11.GL_SCISSOR_TEST);
        GL11C.glScissor(x, fbHeight - y - height - rad, width, height + 2 * rad);
        GlStateManager._clear(GL11.GL_COLOR_BUFFER_BIT);

        shader.bind();
        shader.setUniform("uPass", 0);
        shader.setUniform("uTexture", 0);
        shader.setUniform("uRadius", radius);
        shader.setUniform("uDirection", 1.0f, 0.0f);

        shader.setUniform("uLocation", (float) x, (float) y);
        shader.setUniform("uSize", (float) width, (float) height);
        shader.setUniform("uRounding", rTR, rBR, rTL, rBL);

        GlStateManager._activeTexture(GL30.GL_TEXTURE0);
        GlStateManager._bindTexture(ShaderUtil.getTextureId(mainFbo));

        drawFullScreenQuad();

        int mainFboId = ShaderUtil.getFboId(mainFbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, mainFboId);
        GL11C.glViewport(0, 0, mainFbo.textureWidth, mainFbo.textureHeight);

        GL11C.glScissor(x, fbHeight - y - height, width, height);

        shader.setUniform("uPass", 1);
        shader.setUniform("uDirection", 0.0f, 1.0f);

        GlStateManager._bindTexture(ShaderUtil.getTextureId(intermediateFbo));

        drawFullScreenQuad();

        GL11C.glDisable(GL11.GL_SCISSOR_TEST);
        shader.unbind();

        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, mainFboId);
    }

    public static void drawFullScreenQuad() {
        GlStateManager._glBindVertexArray(vao);
        GlStateManager._drawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
        GlStateManager._glBindVertexArray(0);
    }
}