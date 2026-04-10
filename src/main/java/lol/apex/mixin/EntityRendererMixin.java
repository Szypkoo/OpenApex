package lol.apex.mixin;

import lol.apex.Apex;
import lol.apex.feature.module.implementation.visual.NametagsModule;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Inject(method = "hasLabel", at = @At("HEAD"), cancellable = true)
    private void hideVanillaNametags(Entity entity, double squaredDistanceToCamera, CallbackInfoReturnable<Boolean> cir) {
        NametagsModule nametags = Apex.moduleManager.getByClass(NametagsModule.class);
        if (nametags != null && nametags.enabled()) {
            cir.setReturnValue(false);
        }
    }
}
