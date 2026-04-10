package lol.apex.mixin;

import imgui.ImGui;
import lol.apex.Apex;
import lol.apex.event.render.Render3DEvent;
import lol.apex.feature.module.implementation.visual.NoRenderModule;
import lol.apex.feature.ui.imgui.ImGuiImpl;
import lol.apex.feature.ui.imgui.RenderInterface;
import lol.apex.util.render.RenderUtil;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow public abstract Matrix4f getBasicProjectionMatrix(float fov);
    @Shadow protected abstract float getFov(Camera camera, float tickDelta, boolean changingFov);
    @Shadow @Final private Camera camera;

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "render", at = @At("RETURN"))
    private void render(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        if (client.currentScreen instanceof final RenderInterface renderInterface) {
            ImGuiImpl.beginImGuiRendering();
            renderInterface.render(ImGui.getIO());
            ImGuiImpl.endImGuiRendering();
        }
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
                    ordinal = 1
            )
    )
    private void onWorldRender(RenderTickCounter tickCounter, CallbackInfo ci) {

        float tickDelta = tickCounter.getTickProgress(true);

        float fov = this.getFov(camera, tickDelta, true);

        RenderUtil.lastProjMat.set(this.getBasicProjectionMatrix(fov));

        RenderUtil.lastModMat.set(RenderSystem.getModelViewMatrix());

        GL11.glGetIntegerv(GL11.GL_VIEWPORT, RenderUtil.lastViewport);

        MatrixStack stack = new MatrixStack();
        stack.multiplyPositionMatrix(RenderSystem.getModelViewMatrix());
        RenderUtil.lastWorldSpaceMatrix.set(stack.peek().getPositionMatrix());

        Apex.eventBus.post(new Render3DEvent(tickDelta, stack));
    }



    @Inject(at = {@At(value = "HEAD")}, method = {"tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V"}, cancellable = true)
    public void tiltWhenHurt(MatrixStack stack, float blah, CallbackInfo info) {
        if(Apex.moduleManager.getByClass(NoRenderModule.class).enabled()
                && Apex.moduleManager.getByClass(NoRenderModule.class).hurtCamera.getValue()
        ) {
            info.cancel();
        }
    }
}