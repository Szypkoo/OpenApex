package lol.apex.feature.module.implementation.combat.criticals;

import lol.apex.event.player.PlayerAttackEventPre;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.game.PacketUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class SpartanCriticals extends SubModule {

    public SpartanCriticals() {
        super("Spartan");

    }

    public static void onAttack(PlayerAttackEventPre event) {

        double offset1 = 0.0005;
        double offset2 = 0.00025;
        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();

            PacketUtil.sendPacket(
                    new PlayerMoveC2SPacket.PositionAndOnGround(x, y + offset1, z, false, false)
                );
            PacketUtil.sendPacket(
                    new PlayerMoveC2SPacket.PositionAndOnGround(x, y + offset2, z, false, false)
                );
            PacketUtil.sendPacket(
                    new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false, false)
                );
    }
}
