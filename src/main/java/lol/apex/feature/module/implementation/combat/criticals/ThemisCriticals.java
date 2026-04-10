package lol.apex.feature.module.implementation.combat.criticals;

import lol.apex.event.player.PlayerAttackEventPre;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.game.PacketUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class ThemisCriticals extends SubModule {
    public ThemisCriticals() {
        super("Themis");
    }

    public static void onAttack(PlayerAttackEventPre event) {

        PacketUtil.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getEntityPos().x, mc.player.getEntityPos().y + 0.3, mc.player.getEntityPos().z, false, false));
        PacketUtil.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getEntityPos().x, mc.player.getEntityPos().y + 0.2, mc.player.getEntityPos().z, false, false));
    }
}
