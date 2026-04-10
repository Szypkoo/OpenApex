package lol.apex.feature.module.implementation.movement.noweb;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.movement.NoWebModule;
import lol.apex.util.player.MoveUtil;

public class GodsEyeNoWeb extends SubModule {
    public GodsEyeNoWeb() {
        super("GodsEye");
    }

    public static void onTick(NoWebModule parent, ClientTickEvent event) {
        MoveUtil.setSpeedNoEvent(0.40);
    }
}
