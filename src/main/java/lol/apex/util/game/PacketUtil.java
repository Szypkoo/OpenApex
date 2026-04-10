package lol.apex.util.game;

import lol.apex.util.CommonVars;
import lombok.experimental.UtilityClass;
import net.minecraft.network.listener.ClientPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@UtilityClass
public class PacketUtil implements CommonVars {
    public static void sendSilentPacket(Packet<?> packet) {
        final var nh = mc.getNetworkHandler();
        if (nh == null) return;
        nh.getConnection().send(packet, null, true);
    }

    /** do NOT call this outside of the network thread **/
    @SuppressWarnings("unchecked")
    public static void handlePacketSilently(Packet<?> packet) {
        ((Packet<ClientPacketListener>)packet).apply(mc.getNetworkHandler());
    }


    public static void sendPacket(Packet<?> packet) {
        final var nh = mc.getNetworkHandler();
        if (nh == null) return;
        nh.sendPacket(packet);
    }

    public static PlayerMoveC2SPacket modifyOnGround(PlayerMoveC2SPacket packet, boolean onGround) {
        if(packet instanceof PlayerMoveC2SPacket.Full)
            return new PlayerMoveC2SPacket.Full(packet.getX(0), packet.getY(0), packet.getZ(0),
                    packet.getYaw(0), packet.getPitch(0), onGround,
                    packet.horizontalCollision());

        if(packet instanceof PlayerMoveC2SPacket.PositionAndOnGround)
            return new PlayerMoveC2SPacket.PositionAndOnGround(packet.getX(0), packet.getY(0),
                    packet.getZ(0), onGround, packet.horizontalCollision());

        if(packet instanceof PlayerMoveC2SPacket.LookAndOnGround)
            return new PlayerMoveC2SPacket.LookAndOnGround(packet.getYaw(0), packet.getPitch(0),
                    onGround, packet.horizontalCollision());

        return new PlayerMoveC2SPacket.OnGroundOnly(onGround, packet.horizontalCollision());
    }
}
