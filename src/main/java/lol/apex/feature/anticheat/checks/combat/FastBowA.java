package lol.apex.feature.anticheat.checks.combat;


import lol.apex.event.packet.PacketEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;

import lol.apex.feature.anticheat.Check;

public class FastBowA extends Check {
    private long bowPullStart;
    private int arrowsInWindow;
    private long windowStart;

    public FastBowA(PlayerEntity player) {
        super(player, "Fast Bow (A)");
    }

    @Override
    public void onSendPacket(PacketEvent.Send event) {

        if (getPlayer() == null) return;

        if (event.getPacket() instanceof PlayerActionC2SPacket packet) {

            if (packet.getAction() == PlayerActionC2SPacket.Action.RELEASE_USE_ITEM) {

                if (!getPlayer().getMainHandStack().isOf(Items.BOW)) {
                    return;
                }

                long now = System.currentTimeMillis();
                long pullTime = now - bowPullStart;

                if (pullTime < 200) {
                    flag("Released bow too quickly ("+pullTime+"ms)");
                }

                if (now - windowStart > 1000) {
                    windowStart = now;
                    arrowsInWindow = 0;
                }

                arrowsInWindow++;

                if (arrowsInWindow >= 6) {
                    flag("Too many arrows in 1s (" + arrowsInWindow + ")");
                }
            }
        }

        if (getPlayer().isUsingItem() && getPlayer().getActiveItem().isOf(Items.BOW)) {

            bowPullStart = System.currentTimeMillis();
        }
    }
}
