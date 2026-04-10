package lol.apex.feature.module.implementation.other;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.Apex;
import lol.apex.event.client.ClientPostEvent;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.player.WorldChangeEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.feature.module.setting.implementation.TextInputSetting;
import lol.apex.util.CommonUtil;

@ModuleInfo(
        name = "Spammer",
        description = "Spams the chat with certain words.",
        category = Category.OTHER
)
public class SpammerModule extends Module {
    public final TextInputSetting message = new TextInputSetting("Text", "Apex Client on Top!", 256);
    public final SliderSetting delay = new SliderSetting("Delay", 20, 0, 200, 1);
    public final BoolSetting autoDisable = new BoolSetting("Auto Disable", true);
    public final BoolSetting bypass = new BoolSetting("Bypass", true);
    public final SliderSetting bypassLength = new SliderSetting("Bypass Length", 16, 1, 256, 1).hide(() -> !bypass.getValue());

    public boolean isBlacklistedServer() {
        String[] servers = {"test.ccbluex.net", "mc.loyisa.cn", "anticheat-test.com"};

        String currentServer = CommonUtil.getServerIP();

        if (currentServer == null) return false;

        for (String server : servers) {
            if (currentServer.equalsIgnoreCase(server)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onEnable() {
        if(isBlacklistedServer()) {
            Apex.sendChatMessage("Spammer has been disabled because " + CommonUtil.getServerIP() + " is blacklisted from spammer.");
            toggle();
        }
    }

    @EventHook
    public void onWorldChange(WorldChangeEvent event) {
        if (autoDisable.getValue()) {
        //    Apex.sendChatMessage("Spammer has been disabled due to world change.");
            Apex.notificationRenderer.push("Spammer", "Disabled on world change.");

            toggle();
        }

        if(isBlacklistedServer()) {
            Apex.sendChatMessage("Spammer has been disabled because " + CommonUtil.getServerIP() + " is blacklisted from spammer.");
            toggle();
        }
    }

    private int timer;

    @EventHook
    public void onTick(ClientPostEvent event) {
        if(mc.player == null) return; if(mc.world == null) {
            return;
        }
        if(timer <= 0) {
            String text = message.getValue();

            if(bypass.getValue()) {
                String random = randomAlphabetic(bypassLength.getValue().intValue());
                text += " " + random;
            }

            if(text.length() > 256) {
                text = text.substring(0, 256);
            }

            mc.player.networkHandler.sendChatMessage(text);

            timer = delay.getValue().intValue();
        } else {
            timer--;
        }
    }

    private String randomAlphabetic(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }
}
