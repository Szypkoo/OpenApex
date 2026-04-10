package lol.apex.feature.module.implementation.combat.regen;

import lol.apex.event.client.PreMotionEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.combat.RegenModule;
import lol.apex.util.game.PacketUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class VanillaRegen extends SubModule {
    public VanillaRegen() {
        super("Vanilla", "Normal regen, unlikely to bypass servers. Has potential to bypass servers on 1.17+ due to exempts.");
    }
    public static void onMotion(RegenModule parent, PreMotionEvent event) {
        if(mc.player == null) {
            return;
        }

        if (mc.player.getHealth() < parent.minHealth.getValue().floatValue()) {
            PacketUtil.sendSilentPacket(
                    new PlayerMoveC2SPacket.Full(
                            mc.player.getX(),
                            mc.player.getY(),
                            mc.player.getZ(),
                            mc.player.getYaw(),
                            mc.player.getPitch(),
                            mc.player.isOnGround(),
                            mc.player.horizontalCollision
                    )
            );
        }
    }
}
