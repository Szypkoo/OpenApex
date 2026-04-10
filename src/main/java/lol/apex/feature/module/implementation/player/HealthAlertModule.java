package lol.apex.feature.module.implementation.player;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.Apex;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.CommonUtil;

@ModuleInfo(
        name = "HealthAlert",
        description = "Alerts you when your health or soups are low.",
        category = Category.PLAYER
)
public class HealthAlertModule extends Module {
    public final SliderSetting minHealth = new SliderSetting("Min. Health", 4f, 0, 20f, 0.1f);
    public final SliderSetting minSoups = new SliderSetting("Min. Soups", 3f, 0f, 36f, 1);

    private boolean alerted = false;

    @EventHook
    public void onTick(ClientTickEvent event) {
        if (mc.player == null) return;

        float health = mc.player.getHealth();
        int soups = countSoups();

        boolean lowHealth = health <= minHealth.getValue();
        boolean lowSoup = soups <= minSoups.getValue();

        if (!lowHealth && !lowSoup) {
            alerted = false;
            return;
        }

        if (alerted) return;
        alerted = true;

        if (lowHealth) {
        //    Apex.sendChatMessage("Your health is low!");
            Apex.notificationRenderer.push("Health Alert", "Your health is low!");
        }

        if (lowSoup) {
        //    Apex.sendChatMessage("You're low on soup! (" + soups + " left)");
            Apex.notificationRenderer.push("Health Alert", "You're low on soup! (" + soups + " left)");

        }

        CommonUtil.warningSound();
    }

    private int countSoups() {
        int count = 0;

        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            if (!mc.player.getInventory().getStack(i).isEmpty()
                    && mc.player.getInventory().getStack(i).getItem() == net.minecraft.item.Items.MUSHROOM_STEW) {
                count += mc.player.getInventory().getStack(i).getCount();
            }
        }

        return count;
    }
}
