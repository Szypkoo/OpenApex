package lol.apex.feature.module.implementation.combat.velocity;

import lol.apex.Apex;
import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.combat.VelocityModule;
import lol.apex.util.player.PlayerUtil;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class ResetVelocity extends SubModule {
    public ResetVelocity() {
        super("Reset", "Abuses the legit boost from jumping to reduce your knockback.", "Generic");
    }
    public static void onPacket(VelocityModule parent, PacketEvent.Receive event) {
        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet && packet.getEntityId() == mc.player.getId()) {
            if(checkChances(parent) && mc.player.isOnGround()) {
                if (parent.ignoreSPress.getValue() && mc.options.backKey.isPressed()) return;
                if (parent.ignoreOnFire.getValue() && mc.player.isOnFire()) return;
                if (mc.currentScreen != null) return;
                PlayerUtil.jump();
                Apex.sendChatMessage("Reduced Knockback taken!");
            }
        }
    } 

    private static boolean checkChances(VelocityModule parent) {
        return (Math.random() * 100 < parent.chance.getValue().floatValue());
    } 
}
