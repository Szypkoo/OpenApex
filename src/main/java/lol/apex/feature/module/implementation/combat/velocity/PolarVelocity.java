package lol.apex.feature.module.implementation.combat.velocity;

import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.combat.VelocityModule;
import lol.apex.util.entity.simulation.SimulatedPlayer;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;

public class PolarVelocity extends SubModule {
    public PolarVelocity() {
        super("Polar");
    }

    public static void onPacket(VelocityModule parent, PacketEvent.Receive event) {
        if (!(event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet)) return;
        if (packet.getEntityId() != mc.player.getId()) return;
        if (mc.currentScreen != null) return;

        final var sim = SimulatedPlayer.fromPlayer(mc.player);

        final var kbX = packet.velocity.getX();
        final var kbY = packet.velocity.getY();
        final var kbZ = packet.velocity.getZ();

        sim.velocity = new Vec3d(kbX, kbY, kbZ);

        for (int i = 0; i < 3; i++) {
            sim.tick(false, 0f, 0f);
        }

        double divergence = Math.sqrt(
                        Math.pow(sim.pos.x - mc.player.getEntityPos().x, 2) +
                        Math.pow(sim.pos.z - mc.player.getEntityPos().z, 2)
        );

        if (divergence > 0.1) {
            event.setCancelled(true);
            mc.player.setVelocity(0, kbY, 0);
        }
    }
}
