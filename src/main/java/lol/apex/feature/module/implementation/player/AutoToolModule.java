package lol.apex.feature.module.implementation.player;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.util.animation.ItemAnimationUtil;
import net.minecraft.block.Block;
import net.minecraft.util.hit.BlockHitResult;

@ModuleInfo(
        name = "AutoTool",
        description = "Automatically swaps to the best tool.",
        category = Category.PLAYER
)
public class AutoToolModule extends Module {
    public final BoolSetting spoofSlot = new BoolSetting("Spoof Slot", true);

    private int lastSlot = -1;

    @EventHook
    public void onTick(ClientTickEvent event) {
        if (mc.player == null || mc.world == null || mc.player.isCreative()) {
            lastSlot = -1;
            ItemAnimationUtil.setSpoofedItem(null);
            return;
        }

        boolean breaking = mc.interactionManager.isBreakingBlock();

        if (breaking) {
            int bestSlot = getBestToolSlot();

            if (spoofSlot.getValue()) {
                if (bestSlot != -1) {
                    ItemAnimationUtil.setSpoofedItem(mc.player.getInventory().getStack(bestSlot));
                }
            } else {
                if (lastSlot == -1) {
                    lastSlot = mc.player.getInventory().getSelectedSlot();
                }

                if (bestSlot != -1) {
                    mc.player.getInventory().setSelectedSlot(bestSlot);
                }
            }
        } else {
            if (!spoofSlot.getValue() && lastSlot != -1) {
                mc.player.getInventory().setSelectedSlot(lastSlot);
            }

            lastSlot = -1;
            ItemAnimationUtil.setSpoofedItem(null);
        }
    }
    private int getBestToolSlot() {
        if (!(mc.crosshairTarget instanceof BlockHitResult hit)) return mc.player.getInventory().getSelectedSlot();
        Block block = mc.world.getBlockState(hit.getBlockPos()).getBlock();
        int bestSlot = mc.player.getInventory().getSelectedSlot();
        float bestSpeed = 1.0f;
        for (int i = 0; i < 9; i++) {
            float speed = mc.player.getInventory().getStack(i).getMiningSpeedMultiplier(block.getDefaultState());
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }
        return bestSlot;
    }
}
