package lol.apex.feature.module.implementation.combat.regen;

import lol.apex.event.client.PreMotionEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.combat.RegenModule;
import lol.apex.util.game.PacketUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class VerusRegen extends SubModule {
    public VerusRegen() {
        super("Verus");
    }
    public static void onMotion(RegenModule parent, PreMotionEvent event) {
        if(mc.player == null) {
            return;
        }

        if (mc.player.getHealth() < parent.minHealth.getValue().floatValue()) {
            for (int i = 0; i > 30; i++) {
                if(!event.onGround) continue;

                PacketUtil.sendSilentPacket(
                        new PlayerMoveC2SPacket.PositionAndOnGround(
                                mc.player.getX(),
                                mc.player.getY(),
                                mc.player.getZ(),
                                true,
                                mc.player.horizontalCollision
                        )
                );
            }
        }
    }
}
