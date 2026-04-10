package lol.apex.feature.module.implementation.combat.criticals;

import lol.apex.event.player.PlayerAttackEventPre;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.game.PacketUtil;
import net.minecraft.item.Item;
import net.minecraft.item.MaceItem;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;


public class PacketCriticals extends SubModule {
    public PacketCriticals() {
        super("Packet", "Criticals for servers with no AntiCheats, also supports the Mace.");
    }

    public static void onAttack(PlayerAttackEventPre event) {
        if (mc.player.getMainHandStack() != null) {
            Item item = mc.player.getMainHandStack().getItem();
            double x = mc.player.getX();
            double y = mc.player.getY();
            double z = mc.player.getZ();

            if (item instanceof MaceItem) {
                PacketUtil.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false, mc.player.horizontalCollision));
                PacketUtil.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 1.501 + 15, z, false, mc.player.horizontalCollision));
                PacketUtil.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false, mc.player.horizontalCollision));

            } else {
                PacketUtil.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.0625, z, false, mc.player.horizontalCollision));
                PacketUtil.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false, mc.player.horizontalCollision));
            }
        }
    }
}
