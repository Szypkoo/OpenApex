package lol.apex.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import lol.apex.feature.module.implementation.visual.AmbienceModule;
import lol.apex.feature.module.implementation.visual.NoRenderModule;
import lol.apex.Apex;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.render.Camera;
import org.joml.Vector4f;

import java.awt.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @ModifyVariable(method = "applyFog(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("HEAD"), argsOnly = true)
    private Vector4f modifyFogColor(Vector4f original) {
        AmbienceModule ambience = Apex.moduleManager.getByClass(AmbienceModule.class);
        if (ambience != null && ambience.isEnabled() && ambience.changeFog.getValue()) {
            Color c = ambience.fogColor.getValue();
            return new Vector4f(
                    c.getRed() / 255f,
                    c.getGreen() / 255f,
                    c.getBlue() / 255f,
                    1f
            );
        }
        return original;
    }

    @ModifyExpressionValue(method = "getFogBuffer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/fog/FogRenderer;fogEnabled:Z"))
    private boolean modifyFogEnabled(boolean original) {
        NoRenderModule noRender = Apex.moduleManager.getByClass(NoRenderModule.class);
        return original && (noRender == null || !noRender.fog.getValue());
    }
}