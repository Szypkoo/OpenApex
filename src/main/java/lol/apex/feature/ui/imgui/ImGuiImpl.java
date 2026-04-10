package lol.apex.feature.ui.imgui;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import lol.apex.Apex;
import lol.apex.util.CommonUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.texture.GlTexture;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL30C;

import java.io.File;
import java.util.function.Consumer;

public final class ImGuiImpl {
    private final static ImGuiImplGlfw GLFW_BACKEND = new ImGuiImplGlfw();
    private final static ImGuiImplGl3 GL3_BACKEND = new ImGuiImplGl3();

    public static boolean inputEnabled = true;

    public static void create(final long handle) {
        ImGui.createContext();
        ImPlot.createContext();

        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(CommonUtil.getClientDir() + File.separator + "ImGui.ini");

        ImGuiFonts.init();

        GLFW_BACKEND.init(handle, false);
        GL3_BACKEND.init();

        io.setFontDefault(ImGuiFonts.mainFont);
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable);
    }

    public static void render(final Consumer<IImWrapper> consumer) {
        final MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player == null || consumer == null || mc.currentScreen instanceof RenderInterface) return;

        beginImGuiRendering();

        consumer.accept(new ImWrapper(ImGui.getForegroundDrawList()));

        endImGuiRendering();
    }

    public static void beginImGuiRendering() {
        ImGuiFonts.processPendingFonts(GL3_BACKEND);

        final Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
        GlStateManager._glBindFramebuffer(GL30C.GL_FRAMEBUFFER,
                ((GlTexture) framebuffer.getColorAttachment())
                        .getOrCreateFramebuffer(((GlBackend) RenderSystem.getDevice()).getBufferManager(), null));
        GL11C.glViewport(0, 0, framebuffer.textureWidth, framebuffer.textureHeight);

        GL3_BACKEND.newFrame();
        GLFW_BACKEND.newFrame();
        ImGui.newFrame();
    }

    public static void endImGuiRendering() {
        ImGui.render();
        GL3_BACKEND.renderDrawData(ImGui.getDrawData());

        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long pointer = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();

            GLFW.glfwMakeContextCurrent(pointer);
        }
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (!inputEnabled) return;
        GLFW_BACKEND.keyCallback(window, key, scancode, action, mods);
    }

    public static void charCallback(long window, int c) {
        if (!inputEnabled) return;
        GLFW_BACKEND.charCallback(window, c);
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (!inputEnabled) return;
        GLFW_BACKEND.mouseButtonCallback(window, button, action, mods);
    }

    public static void scrollCallback(long window, double xoffset, double yoffset) {
        if (!inputEnabled) return;
        GLFW_BACKEND.scrollCallback(window, xoffset, yoffset);
    }

    public static void cursorPosCallback(long window, double xpos, double ypos) {
        if (!inputEnabled) return;
        GLFW_BACKEND.cursorPosCallback(window, xpos, ypos);
    }

    public static void dispose() {
        GL3_BACKEND.shutdown();
        GLFW_BACKEND.shutdown();

        ImPlot.destroyContext();
        ImGui.destroyContext();
    }
}