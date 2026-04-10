package lol.apex.feature.module.implementation.movement.noweb;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;

public class ThemisNoWeb extends SubModule {

    public ThemisNoWeb() {
        super("Themis");
    }

    public static void onTick(ClientTickEvent event) {
        MoveUtil.setSpeedNoEvent(0.90d);
    }
}
