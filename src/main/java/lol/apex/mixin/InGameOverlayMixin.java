package lol.apex.mixin;

import lol.apex.Apex;
import lol.apex.feature.module.implementation.visual.NoRenderModule;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import org.spongepowered.asm.mixin.injection.At;
@SuppressWarnings("all")

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayMixin {

    @Inject(method={"renderFireOverlay"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V")}, cancellable=true)
    private static void renderFireOverlay(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Sprite sprite, CallbackInfo info) {
        if(Apex.moduleManager.getByClass(NoRenderModule.class).fire.getValue()
                && Apex.moduleManager.getByClass(NoRenderModule.class).enabled()
        ) {
            info.cancel();
        }
    }
}
