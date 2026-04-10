package lol.apex.feature.module.implementation.player.nofall;

import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class CubeCraftNoFall extends SubModule {
    public CubeCraftNoFall() {
        super("CubeCraft", null, "Generic");
    }

    public static void onPacket(PacketEvent.Send event) {
        if(event.getPacket() instanceof PlayerMoveC2SPacket && mc.player.fallDistance >= 2.5) {
            mc.player.setPos(mc.player.getX(), mc.player.getY(), mc.player.getZ()); 
            MoveUtil.setMotionY(MoveUtil.getMotionY() + 0.1); 
            mc.player.fallDistance = 0;
        }
    }
}
