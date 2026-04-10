package lol.apex.feature.module.implementation.combat;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.PreUpdateEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.*;
import lol.apex.util.game.PacketUtil;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

@ModuleInfo(
        name = "AutoSoup",
        description = "Automatically helps you eat soup in kit-pvp",
        category = Category.COMBAT
)
public class AutoSoupModule extends Module {

    public final SliderSetting minHealth  = new SliderSetting("Min. Health", 14f, 0f, 20f, 0.5f);
    public final SliderSetting theDelay   = new SliderSetting("Delay", 50f, 0f, 500f, 1f);
    public final EnumSetting<ClickingType> clickType = new EnumSetting<>("Click Type", ClickingType.INTERACT);
    public final BoolSetting throwBowls = new BoolSetting("Throw Bowls", true);
    public final BoolSetting autoRefill = new BoolSetting("Auto Refill", true);

    @RequiredArgsConstructor
    public enum ClickingType {
        LEGIT("Legit"),
        INTERACT("Interact");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    private int lastSlot = -1;
    private int delay = 0;
    private boolean waitingForRefill = false;

    @Override
    public void onDisable() {
        delay = 0;
        waitingForRefill = false;
        lastSlot = -1;
    }

    @EventHook
    public void onPreUpdate(PreUpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (delay > 0) {
            delay--;
            return;
        }

        if (mc.player.getHealth() >= minHealth.getValue().floatValue()) return;

        int soupSlot = findSoup();

        if (soupSlot == -1) {
            if (autoRefill.getValue()) {
                if (!waitingForRefill) {
                    boolean found = refill();
                    if (found) {
                        waitingForRefill = true;
                        delay = theDelay.getValue().intValue();
                    }
                } else {
                    waitingForRefill = false;
                }
            }
            return;
        }

        waitingForRefill = false;
        eatSoup(soupSlot);

        if (throwBowls.getValue()) {
            throwBowls();
        }

        delay = theDelay.getValue().intValue();
    }

    private void eatSoup(int slot) {
        int originalSlot = mc.player.getInventory().getSelectedSlot();

        switch (clickType.getValue()) {
            case INTERACT -> {
                mc.player.getInventory().setSelectedSlot(slot);
                PacketUtil.sendPacket(new UpdateSelectedSlotC2SPacket(slot));

                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);

                mc.player.getInventory().setSelectedSlot(originalSlot);
                PacketUtil.sendPacket(new UpdateSelectedSlotC2SPacket(originalSlot));
            }
            case LEGIT -> {
                if (originalSlot != slot) {
                    lastSlot = originalSlot;
                    mc.player.getInventory().setSelectedSlot(slot);
                    PacketUtil.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                }
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                if (lastSlot != -1 && lastSlot != slot) {
                    mc.player.getInventory().setSelectedSlot(lastSlot);
                    PacketUtil.sendPacket(new UpdateSelectedSlotC2SPacket(lastSlot));
                    lastSlot = -1;
                }
            }
        }
    }

    private int findSoup() {
        for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == Items.MUSHROOM_STEW) {
                return i;
            }
        }
        return -1;
    }

    private boolean refill() {
        int syncId = mc.player.currentScreenHandler.syncId;

        for (int i = 9; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == Items.MUSHROOM_STEW) {
                mc.interactionManager.clickSlot(syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
                return true;
            }
        }
        return false;
    }

    private void throwBowls() {
        int syncId = mc.player.currentScreenHandler.syncId;

        for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == Items.BOWL) {
                mc.interactionManager.clickSlot(
                        syncId,
                        i + 36,
                        0,
                        SlotActionType.THROW,
                        mc.player
                );
            }
        }
    }

    @Override
    public String getSuffix() {
        return clickType.getValue().toString();
    }
}