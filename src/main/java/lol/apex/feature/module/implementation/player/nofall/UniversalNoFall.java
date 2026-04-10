package lol.apex.feature.module.implementation.player.nofall;

import lol.apex.event.client.PreMotionEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.player.NoFallModule;
import lol.apex.util.player.PlayerUtil;

public class UniversalNoFall extends SubModule {
    public UniversalNoFall() {
        super("Universal");
    }

    public static void onPreMotion(NoFallModule parent, PreMotionEvent event) {
        if(PlayerUtil.fallDistance(parent.distance.getValue())) {
            event.onGround = true;
        }
    }
}
