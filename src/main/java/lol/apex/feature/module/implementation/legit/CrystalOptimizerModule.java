package lol.apex.feature.module.implementation.legit;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.Module;
import lol.apex.mixin.IPlayerInteractEntityC2SPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

import lol.apex.feature.module.base.*;

@ModuleInfo( 
    name = "CrystalOptimizer",
    description = "Optimizes the way you place crystals.",
    category = Category.LEGIT
)
public class CrystalOptimizerModule extends Module {

    @EventHook
    public void onPacket(PacketEvent.Send event) {
        if(event.getPacket() instanceof PlayerInteractEntityC2SPacket packet && 
        ((IPlayerInteractEntityC2SPacket) packet).getType().getType() == PlayerInteractEntityC2SPacket.InteractType.ATTACK) {
            Entity entity = mc.world.getEntityById(((IPlayerInteractEntityC2SPacket)packet).getEntityId());

            if(entity instanceof EndCrystalEntity && mc.player.getStatusEffect(StatusEffects.WEAKNESS) == null) {
                mc.world.removeEntity(entity.getId(), RemovalReason.KILLED);
            }
        }
    }
}
