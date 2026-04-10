package lol.apex.feature.anticheat.checks.combat;

import lol.apex.feature.anticheat.Check;
import lol.apex.util.CommonVars;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.util.Hand;
import lol.apex.event.packet.PacketEvent;

import java.util.LinkedList;
import java.util.List;

public class AutoClickerA extends Check implements CommonVars {
    private static final int maxClickHistory = 10;
    private static final long minDelayThreshold = 50;

    private List<Long> clickTimes = new LinkedList<>();

    public AutoClickerA(PlayerEntity player) {
        super(player, "Auto Clicker (A)");
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
                handleClick();
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
            handleClick();
        }
        super.onReceivePacket(event);
    }

    private void handleClick() {
        long currentTime = System.currentTimeMillis();

        if(!clickTimes.isEmpty() && (currentTime - clickTimes.get(clickTimes.size() - 1) < minDelayThreshold)) {
            return;
        }

        clickTimes.add(currentTime);

        if(clickTimes.size() > maxClickHistory) {
            clickTimes.remove(0);
        }

        if(clickTimes.size() == maxClickHistory) {
            checkForSameDelays();
        }
    }

    private void checkForSameDelays() {
        long firstInterval = clickTimes.get(1) - clickTimes.get(0);
        boolean exactIntervals = true;

        for (int i = 1; i < clickTimes.size() - 1; i++) {
            long interval = clickTimes.get(i + 1) - clickTimes.get(i);
            if(interval != firstInterval) {
                exactIntervals = false;
                break;
            }
        }
        if(exactIntervals) {
            flag("identical intervals");
        }
    }
}
