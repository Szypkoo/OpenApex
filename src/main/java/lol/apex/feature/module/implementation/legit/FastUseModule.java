package lol.apex.feature.module.implementation.legit;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.client.ItemFastUseEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lombok.Getter;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;

@ModuleInfo(
        name = "FastUse",
        description = "Allows you to use items faster.",
        category = Category.LEGIT
)
public class FastUseModule extends Module {
    @Getter
    public final BoolSetting xp = new BoolSetting("Experiance Bottles", false);
    @Getter
    public final BoolSetting blocks = new BoolSetting("Blocks", true);
    @Getter
    private SliderSetting delay = new SliderSetting("Delay", 25, 0f, 500f, 1);

    private long lastUseTime = 0L;

    public boolean doXPThrow, doBlocksThrow;

    @EventHook
    public void onTick(ClientTickEvent event) {
        if (mc.player == null) return;
        if (!this.enabled()) return;

        long currentTime = System.currentTimeMillis();
        long delayMs = (long) getDelay().getValue().longValue();

        if (getXp().getValue() &&
                mc.player.getMainHandStack().getItem() == Items.EXPERIENCE_BOTTLE) {

            if (currentTime - lastUseTime >= delayMs) {
                doXPThrow = true;
                lastUseTime = currentTime;
            } else {
                doXPThrow = false;
            }

        } else {
            doXPThrow = false;
        }

        // Blocks
        if (getBlocks().getValue() &&
                (mc.player.getMainHandStack().getItem() instanceof BlockItem ||
                        mc.player.getOffHandStack().getItem() instanceof BlockItem)) {

            if (currentTime - lastUseTime >= delayMs) {
                doBlocksThrow = true;
                lastUseTime = currentTime;
            } else {
                doBlocksThrow = false;
            }

        } else {
            doBlocksThrow = false;
        }
    }

    @EventHook
    public void changeItemCooldownEvent(ItemFastUseEvent event) {
        if (doBlocksThrow || doXPThrow) {
            event.setCooldown(delay.getValue().intValue());
            event.setCancelled(true);
        }
    }
}
