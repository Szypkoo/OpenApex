package lol.apex.mixin;

import lol.apex.Apex;
import lol.apex.feature.module.implementation.visual.NoRenderModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void cancelScoreboard(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        NoRenderModule noRender = Apex.moduleManager.getByClass(NoRenderModule.class);

        if (noRender != null && noRender.enabled() && noRender.scoreboard.getValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderStatusEffectOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (Apex.moduleManager.getByClass(NoRenderModule.class).activeEffects.getValue()) {
            ci.cancel();
        }
    }
}
