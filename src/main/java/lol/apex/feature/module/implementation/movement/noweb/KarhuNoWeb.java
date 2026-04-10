package lol.apex.feature.module.implementation.movement.noweb;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.movement.NoWebModule;
import lol.apex.util.player.MoveUtil;

public class KarhuNoWeb extends SubModule {
    public KarhuNoWeb() {
        super("Karhu");
    }
    public static void onTick(NoWebModule parent, ClientTickEvent event) {
        MoveUtil.setSpeedNoEvent(1);
    }
}
