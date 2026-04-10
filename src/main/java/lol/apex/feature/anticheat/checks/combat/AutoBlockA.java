package lol.apex.feature.anticheat.checks.combat;

import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.anticheat.Check;
import lol.apex.util.CommonVars;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.util.Hand;

import java.util.LinkedList;
import java.util.List;

public class AutoBlockA extends Check implements CommonVars {


    public AutoBlockA(PlayerEntity player) {
        super(player, "Auto Block (A)");
    }

    @Override
    public void onSendPacket(PacketEvent.Send event) {
        if (getPlayer() == null) {
            return;
        }

        Packet<?> packet = event.getPacket();

        if(shouldCheckSelf() && getPlayer() == mc.player) {
            if (packet instanceof HandSwingC2SPacket handSwingC2SPacket) {
                if(handSwingC2SPacket.getHand() != Hand.MAIN_HAND) {
                    return;
                }
                if (getPlayer().isBlocking() && getPlayer().handSwinging){
                    flag("Swinging while blocking");
                }
            }
        }
        super.onSendPacket(event);
    }

    @Override
    public void onReceivePacket(PacketEvent.Receive event) {
        if(getPlayer() == null) {
            return;
        }

        Packet<?> packet = event.getPacket();

        if (packet instanceof EntityAnimationS2CPacket entityAnimationS2CPacket) {
            if(entityAnimationS2CPacket.getEntityId() != getPlayer().getId()) {
                return;
            }
            if(entityAnimationS2CPacket.getAnimationId() != 0) {
                return;
            }
            if (getPlayer().isBlocking() && getPlayer().handSwinging){
                flag("Swinging while blocking");
            }
        }
        super.onReceivePacket(event);
    }

}
