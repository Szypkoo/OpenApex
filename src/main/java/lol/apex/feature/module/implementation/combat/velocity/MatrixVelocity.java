package lol.apex.feature.module.implementation.combat.velocity;

import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.combat.VelocityModule;
import lol.apex.mixin.EntityVelocityUpdateS2CPacketAccessor;
import lol.apex.util.player.MoveUtil;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;

public class MatrixVelocity extends SubModule {
    public MatrixVelocity() {
        super("Matrix", null, "Specific");
    }
    public static void onPacket(VelocityModule parent, PacketEvent.Receive event) {
        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket s12
                && s12.getEntityId() == mc.player.getId() && MoveUtil.isMoving()) {

            EntityVelocityUpdateS2CPacketAccessor accessor =
                    (EntityVelocityUpdateS2CPacketAccessor) s12;

            double newX = MoveUtil.getMotionX() * 0.06;
            double newZ = MoveUtil.getMotionZ() * 0.06;

            Vec3d current = s12.getVelocity();

            accessor.setVelocity(new Vec3d(
                    newX, current.y,
                    newZ
            ));
        }
    }
}
