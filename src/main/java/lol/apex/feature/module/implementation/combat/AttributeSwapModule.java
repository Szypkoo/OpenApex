package lol.apex.feature.module.implementation.combat;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.player.PlayerAttackEventPre;
import lol.apex.event.player.PlayerAttackEventPost;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;

@ModuleInfo( 
    name = "AttributeSwap",
    description = "Swaps to mace every other attack with axe or sword.",
    category = Category.COMBAT
)
public class AttributeSwapModule extends Module {

    private int lastSlot = -1;
    private boolean swapNext = false;
    private boolean swapped = false;

    @EventHook
    public void onAttack(PlayerAttackEventPre event) {
        if (mc.player == null) return;

        ItemStack heldItem = mc.player.getMainHandStack();

        if (heldItem.getItem() instanceof AxeItem) {
            if (swapNext) {
                int maceSlot = findMaceSlot();
                if (maceSlot != -1) {
                    lastSlot = mc.player.getInventory().getSelectedSlot();
                    mc.player.getInventory().setSelectedSlot(maceSlot);
                    swapped = true;
                }
            }

        } else if (heldItem.isIn(ItemTags.SWORDS)) {
            int maceSlot = findMaceSlot();
            if (maceSlot != -1) {
                lastSlot = mc.player.getInventory().getSelectedSlot();
                mc.player.getInventory().setSelectedSlot(maceSlot);
                swapped = true;
            }
        }
    }

    @EventHook
    public void onPostAttack(PlayerAttackEventPost event) {
        if (swapped && lastSlot != -1) {
            mc.player.getInventory().setSelectedSlot(lastSlot);
            swapped = false;
            lastSlot = -1;
        }

        ItemStack heldItem = mc.player.getMainHandStack();
        if (heldItem.getItem() instanceof AxeItem) {
            swapNext = !swapNext;
        } else {
            swapNext = false;
        }
    }

    private int findMaceSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);

            if (!stack.isEmpty() && stack.isOf(Items.MACE)) {
                return i;
            }
        }
        return -1;
    }
}