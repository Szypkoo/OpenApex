package lol.apex.feature.module.implementation.other;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;

@ModuleInfo(
        name = "IRC",
        description = "Allows you to chat with other Apex users.",
        category = Category.OTHER
)
public class IRCModule extends Module {
    public final BoolSetting hide = new BoolSetting("Hide from IRC", false);

    //public static String IRC_WEBSITE = "http://getapex.club/irc"";

    @EventHook
    public void onTick(ClientTickEvent event) {
        // make the irc backend first please - remi
        // who the fuck is remi?? - mark
    }
}