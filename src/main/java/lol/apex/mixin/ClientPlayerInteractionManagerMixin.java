package lol.apex.mixin;

import lol.apex.Apex;
import lol.apex.event.player.PlayerAttackEventPost;
import lol.apex.event.player.PlayerAttackEventPre;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "attackEntity",at = @At("HEAD"), cancellable = true)
    private void attackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        PlayerAttackEventPre event = new PlayerAttackEventPre(target);
        Apex.eventBus.post(event);

        if(event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "attackEntity",at = @At("TAIL"))
    private void attackEntityPost(PlayerEntity player, Entity target, CallbackInfo ci) {
        PlayerAttackEventPost event = new PlayerAttackEventPost(target);
        Apex.eventBus.post(event);
    }
}

