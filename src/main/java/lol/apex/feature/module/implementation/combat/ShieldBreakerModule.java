package lol.apex.feature.module.implementation.combat;


import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;

import lol.apex.feature.module.base.Module;
import lol.apex.util.player.InventoryUtil;
import lol.apex.util.player.PlayerUtil;
import lol.apex.util.world.WorldUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;

@ModuleInfo(
        name = "ShieldBreaker",
        description = "Automatically assists you in disabling targets shields.",
        category = Category.COMBAT
)
public class ShieldBreakerModule extends Module {
    public final SliderSetting hitDelay = new SliderSetting("Hit Delay", 0, 1, 20, 1);
    public final SliderSetting switchDelay = new SliderSetting("Switch Delay", 0, 1, 20, 1);
    public final BoolSetting stun = new BoolSetting("Stun", true);
    public final BoolSetting requireAxe = new BoolSetting("Require Axe", false);
    public final BoolSetting switchBack = new BoolSetting("Switch Back", false);

    private int previousSlot, hitClock, switchClock;

    @Override
    public void onEnable() {
        hitClock = hitDelay.getValue().intValue();
        switchClock = switchDelay.getValue().intValue();
        previousSlot = -1;
        super.onEnable();
    }

    @EventHook
    public void onTick(ClientTickEvent event) {
        if (mc.currentScreen != null)
            return;

        if (requireAxe.getValue() && !(mc.player.getMainHandStack().getItem() instanceof AxeItem))
            return;

        if (mc.crosshairTarget instanceof EntityHitResult entityHit) {
            PlayerEntity entity = (PlayerEntity) entityHit.getEntity();

            if (mc.player.isUsingItem())
                return;

            if (entity instanceof PlayerEntity player) {
                if (WorldUtil.isShieldFacingAway(player))
                    return;

                if (player.isHolding(Items.SHIELD) && player.isBlocking()) {
                    if (switchClock > 0) {
                        //if (previousSlot == -1)
                        //	previousSlot = mc.player.getInventory().selectedSlot;

                        switchClock--;
                        return;
                    }

                    if (previousSlot == -1)
                        previousSlot = mc.player.getInventory().getSelectedSlot();

                    if (InventoryUtil.selectAxe()) {
                        if (hitClock > 0) {
                            hitClock--;
                        } else {

                            PlayerUtil.attack(player);
                            PlayerUtil.swingMainHand();

                            if (stun.getValue()) {

                                PlayerUtil.attack(player);
                                PlayerUtil.swingMainHand();
                            }

                            hitClock = hitDelay.getValue().intValue();
                            switchClock = switchDelay.getValue().intValue();
                        }
                    }
                } else if (previousSlot != -1) {
                    if (switchBack.getValue())
                        InventoryUtil.setInventorySlot(previousSlot);

                    previousSlot = -1;
                }
            }
        }
    }
}
