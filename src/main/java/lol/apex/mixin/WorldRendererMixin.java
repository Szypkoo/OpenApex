package lol.apex.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import lol.apex.Apex;
import lol.apex.event.render.RenderWorldEvent;
import lol.apex.feature.module.implementation.visual.AmbienceModule;
import lol.apex.util.render.RainbowSkyRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.Handle;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.dimension.DimensionType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow
    @Final
    private WorldRenderState worldRenderState;


    @Inject(
            method = "method_62214", //setRenderer() lambda name
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;draw()V",
                    shift = At.Shift.AFTER
            ), remap = false
    )
    private void injectRenderMain(GpuBufferSlice gpuBufferSlice,
         WorldRenderState worldRenderState, Profiler profiler,
          Matrix4f matrix4f, @SuppressWarnings("rawtypes") Handle handle,
           @SuppressWarnings("rawtypes") Handle handle2, boolean bl, 
           @SuppressWarnings("rawtypes") Handle handle3, @SuppressWarnings("rawtypes") 
           Handle handle4, CallbackInfo ci) {

        var event = new RenderWorldEvent(gpuBufferSlice, worldRenderState, matrix4f);
        // bruh var ABUSER
        Apex.eventBus.post(event);
    }

    @Inject(method = "renderSky", at = @At("HEAD"))
    private void redirectSkyType(FrameGraphBuilder frameGraphBuilder, Camera camera, GpuBufferSlice fogBuffer, CallbackInfo ci) {
        AmbienceModule ambienceModule = Apex.moduleManager.getByClass(AmbienceModule.class);
        if (ambienceModule.isEnabled() && ambienceModule.sky.getValue()) {
            switch(ambienceModule.skyType.getValue()) {
                case END -> worldRenderState.skyRenderState.skybox = DimensionType.Skybox.END;
                case OVERWORLD -> worldRenderState.skyRenderState.skybox = DimensionType.Skybox.OVERWORLD;
                case NONE -> worldRenderState.skyRenderState.skybox = DimensionType.Skybox.NONE;
            }
        }

        if(ambienceModule.changeSkyColor.getValue()) {
            if(ambienceModule.skyToFogColor.getValue()) {
                worldRenderState.skyRenderState.skyColor = ambienceModule.fogColor.getValue().getRGB();
            }

            if(ambienceModule.staticColor.getValue()) {
                worldRenderState.skyRenderState.skyColor = ambienceModule.skyColorSetting.getValue().getRGB();
            }
        }
    }
}
