package lol.apex.feature.module.implementation.player.nofall;

import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.player.NoFallModule;
import lol.apex.mixin.PlayerMoveC2SPacketAccessor;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class OnGroundNoFall extends SubModule {
    public OnGroundNoFall() {
        super("OnGroundNoFall", "Spoofs being on ground when you are falling.", "Generic");
    }
    public static void onPacket(NoFallModule parent, PacketEvent.Send event) {
        Packet<?> packet = event.getPacket();
        if(packet instanceof PlayerMoveC2SPacket && mc.player.fallDistance >= 2) {
            PlayerMoveC2SPacketAccessor ip = (PlayerMoveC2SPacketAccessor)packet;
            ip.setOnGround(true);
        }
    }
}
