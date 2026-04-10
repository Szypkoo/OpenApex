package lol.apex.feature.module.implementation.combat.criticals;

import lol.apex.event.player.PlayerAttackEventPre;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.game.PacketUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class MospixelCriticals extends SubModule {
    public MospixelCriticals() {
        super("Mospixel", null, "Specfic");
    }
    public static void onAttack(PlayerAttackEventPre event) {
        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();


        PacketUtil.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.000000271875, z, false, false));
        PacketUtil.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0., z, false, false));
    }
}
