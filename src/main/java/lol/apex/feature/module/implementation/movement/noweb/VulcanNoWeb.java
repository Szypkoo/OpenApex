package lol.apex.feature.module.implementation.movement.noweb;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.movement.NoWebModule;
import lol.apex.util.player.MoveUtil;

public class VulcanNoWeb extends SubModule {
    public VulcanNoWeb() {
        super("Vulcan");
    }
    public static void onTick(NoWebModule parent, ClientTickEvent event) {
        if (mc.player.age % 1 == 0) {
            MoveUtil.setSpeedNoEvent(0.24d);
        }
    }
}
