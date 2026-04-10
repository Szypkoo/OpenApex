package lol.apex.feature.module.implementation.combat.criticals;

import lol.apex.event.player.PlayerAttackEventPre;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.combat.CriticalsModule;
import net.minecraft.entity.Entity;

public class VisualCriticals extends SubModule {
    public VisualCriticals() {
        super("Visual", "Shows client-side only criticals.");
    }
    public static void onAttack(CriticalsModule parent, PlayerAttackEventPre event) {
        spawnParticles(parent, parent.target);
    }

    private static void spawnParticles(CriticalsModule parent, Entity player) {
        for (int i = 0; i < parent.particles.getValue().intValue(); i++) {
            double offsetX = (Math.random() - 0.5) * player.getWidth();
            double offsetY = Math.random() * player.getHeight();
            double offsetZ = (Math.random() - 0.5) * player.getWidth();   
            

            mc.world.addParticleClient(
                net.minecraft.particle.ParticleTypes.CRIT,
                player.getX() + offsetX,
                player.getY() + offsetY,
                player.getZ() + offsetZ,
                0, 0, 0 // no motion
            );            
        }
    }
}
