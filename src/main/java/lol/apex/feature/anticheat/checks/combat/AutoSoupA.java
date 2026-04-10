package lol.apex.feature.anticheat.checks.combat;

import lol.apex.feature.anticheat.Check;
import lol.apex.util.math.TimerUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import lol.apex.event.packet.PacketEvent;

public class AutoSoupA extends Check {
    private final TimerUtil soupTimer = new TimerUtil();
    private float lastHealth;

    public AutoSoupA(PlayerEntity player) {
        super(player, "Auto Soup (A)");
        lastHealth = player.getHealth();
    }

    @Override
    public void onSendPacket(PacketEvent.Send event) {
        if(!(event.getPacket() instanceof PlayerInteractItemC2SPacket)) {
            return;
        }

        if (getPlayer() == null) {
            return;
        }

        ItemStack stack = getPlayer().getMainHandStack();

        if (stack.getItem() != Items.MUSHROOM_STEW) {
            return;
        }

        float currentHealth = getPlayer().getHealth();

        if(!soupTimer.passed(150L, false)) {
            flag("Used soup too quickly!");
        }

        if (currentHealth > lastHealth && !soupTimer.passed(200L, false)) {
            flag("Instant soup heal");
        }

        lastHealth = currentHealth;
        soupTimer.reset();

        super.onSendPacket(event);
    }
}
