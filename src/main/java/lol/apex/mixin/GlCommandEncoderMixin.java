package lol.apex.mixin;

import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.gl.GlCommandEncoder;
import net.minecraft.client.gl.ScissorState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Supplier;

@Mixin(GlCommandEncoder.class)
public abstract class GlCommandEncoderMixin {
    @Inject(method = "createRenderPass(Ljava/util/function/Supplier;Lcom/mojang/blaze3d/textures/GpuTextureView;Ljava/util/OptionalInt;Lcom/mojang/blaze3d/textures/GpuTextureView;Ljava/util/OptionalDouble;)Lcom/mojang/blaze3d/systems/RenderPass;", at = @At("RETURN"))
    private void apex$applyGlobalScissor(Supplier<String> supplier, GpuTextureView colorView, OptionalInt clearColor, GpuTextureView depthView, OptionalDouble clearDepth, CallbackInfoReturnable<RenderPass> cir) {
        RenderPass pass = cir.getReturnValue();
        ScissorState state = RenderSystem.getScissorStateForRenderTypeDraws();
        if (state.isEnabled()) {
            pass.enableScissor(state.getX(), state.getY(), state.getWidth(), state.getHeight());
        }
    }
}
