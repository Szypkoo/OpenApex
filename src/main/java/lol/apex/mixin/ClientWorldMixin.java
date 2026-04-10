package lol.apex.mixin;

import lol.apex.Apex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

import lol.apex.event.entity.EntityRemovedEvent;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
    @Shadow @Mutable public abstract Entity getEntityById(int id); 

  //  @Inject(method = "addEntity", at = @At("TAIL"))
  //  private void onSpawnEntity(Entity entity, CallbackInfo info) {
  //      EntitySpawnEvent event = new EntitySpawnEvent(entity);
   //     Integrity.eventBus.post(event);
  //  }

    @Inject(method = "removeEntity", at = @At("HEAD"))
    private void onRemoveEntity(int entityId, Entity.RemovalReason removalReason, CallbackInfo info) {
        if (getEntityById(entityId) != null)  
            Apex.eventBus.post(new EntityRemovedEvent(getEntityById(entityId)));
    }
}