package lol.apex.feature.module.implementation.visual;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.Apex;
import lol.apex.event.player.PlayerOutlineEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.implementation.other.AntiBotModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;

@ModuleInfo(
        name = "OutlineESP",
        description = "Shows entities' hitboxes with an outline visible behind blocks.",
        category = Category.VISUAL
)
public class OutlineESPModule extends Module {
    private boolean isValidTarget(Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;

        if (entity == mc.player) return false;
        if (living.isDead()) return false;
        if (entity instanceof ArmorStandEntity) return false;

        if (living instanceof PlayerEntity player) {
            AntiBotModule antiBot = Apex.moduleManager.getByClass(AntiBotModule.class);

            if (antiBot != null && antiBot.enabled() && antiBot.isBot(player)) {
                return false;
            }
        }

        return living instanceof HostileEntity || living instanceof PlayerEntity;
    }

    @EventHook
    public void onOutline(PlayerOutlineEvent event) {
        if (isValidTarget(event.getEntity())) {
            event.setCancelled(true);
        }
    }
}