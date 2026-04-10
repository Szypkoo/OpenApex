package lol.apex.feature.module.implementation.combat;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.player.PlayerAttackEventPost;
import lol.apex.feature.module.base.Module;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MaceItem;
import net.minecraft.util.Hand;

import lol.apex.feature.module.base.*;

@ModuleInfo( 
    name = "ElytraSwap",
    description = "Automatically elytra swaps in PvP.",
    category = Category.COMBAT
)
public class ElytraSwapModule extends Module {

    @EventHook
    public void onAttack(PlayerAttackEventPost event) {
        ItemStack currentItem = mc.player.getMainHandStack();
        int current = mc.player.getInventory().getSelectedSlot();

        if (currentItem.getItem() instanceof MaceItem) {
            ItemStack chest = mc.player.getEquippedStack(EquipmentSlot.CHEST);

            if (!chest.isEmpty() && chest.getItem() == Items.ELYTRA) {
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = mc.player.getInventory().getStack(i);

                    if (!stack.isEmpty() &&
                        (stack.getItem() == Items.NETHERITE_CHESTPLATE ||
                        stack.getItem() == Items.DIAMOND_CHESTPLATE)) {

                        mc.player.getInventory().setSelectedSlot(i);
                        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                        break;
                    }
                }
            }
        }

        if (mc.player.getInventory().getSelectedSlot() != current) {
            mc.player.getInventory().setSelectedSlot(current);;
        }
    }
}
