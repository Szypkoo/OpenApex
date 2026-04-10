package lol.apex.util.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.GlTextureView;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL20C;

public class ShaderUtil {
    private final int programId;

    public ShaderUtil(String vertexSource, String fragmentSource) {
        RenderSystem.assertOnRenderThread();
        int vertexShader = createShader(vertexSource, GL20.GL_VERTEX_SHADER);
        int fragmentShader = createShader(fragmentSource, GL20.GL_FRAGMENT_SHADER);

        this.programId = GlStateManager.glCreateProgram();
        GlStateManager.glAttachShader(programId, vertexShader);
        GlStateManager.glAttachShader(programId, fragmentShader);
        GlStateManager.glLinkProgram(programId);

        if (GlStateManager.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Shader link failed: " + GlStateManager.glGetProgramInfoLog(programId, 1024));
        }

        GlStateManager.glDeleteShader(vertexShader);
        GlStateManager.glDeleteShader(fragmentShader);
    }

    public static int getTextureId(Framebuffer fbo) {
        if (fbo.colorAttachment instanceof GlTexture glTexture) {
            return glTexture.glId;
        }
        return -1;
    }

    public static int getFboId(Framebuffer fbo) {
        GlTextureView view = (GlTextureView) fbo.colorAttachmentView;
        var backend = (GlBackend) RenderSystem.getDevice();
        return view.getOrCreateFramebuffer(backend.bufferManager, fbo.depthAttachment);
    }

    private int createShader(String source, int type) {
        int shaderId = GlStateManager.glCreateShader(type);
        GlStateManager.glShaderSource(shaderId, source);
        GlStateManager.glCompileShader(shaderId);

        if (GlStateManager.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Shader compilation failed (" + type + "): " + GlStateManager.glGetShaderInfoLog(shaderId, 1024));
        }
        return shaderId;
    }

    public void bind() {
        GlStateManager._glUseProgram(programId);
    }

    public void unbind() {
        GlStateManager._glUseProgram(0);
    }

    public int getUniformLocation(String name) {
        return GlStateManager._glGetUniformLocation(programId, name);
    }

    public void setUniform(String name, int value) {
        GlStateManager._glUniform1i(getUniformLocation(name), value);
    }

    public void setUniform(String name, float value) {
        GL20C.glUniform1f(getUniformLocation(name), value);
    }

    public void setUniform(String name, float x, float y) {
        GL20C.glUniform2f(getUniformLocation(name), x, y);
    }

    public void setUniform(String name, float x, float y, float z, float w) {
        GL20C.glUniform4f(getUniformLocation(name), x, y, z, w);
    }
}
