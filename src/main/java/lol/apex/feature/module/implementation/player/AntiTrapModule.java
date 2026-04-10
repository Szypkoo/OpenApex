package lol.apex.feature.module.implementation.player;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

@ModuleInfo(
        name = "AntiTrap",
        description = "Avoids nearby traps.",
        category = Category.PLAYER
)
public class AntiTrapModule extends Module {
    public final BoolSetting boats = new BoolSetting("Boats", true); 
    public final BoolSetting armorStands = new BoolSetting("Armor Stands", true);

    @EventHook
    public void onTick(ClientTickEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        for (Entity entity : mc.world.getEntities()) {

            if (armorStands.getValue() && entity.getType() == EntityType.ARMOR_STAND) {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }

            if (boats.getValue() && entity instanceof net.minecraft.entity.vehicle.BoatEntity) {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }
}
