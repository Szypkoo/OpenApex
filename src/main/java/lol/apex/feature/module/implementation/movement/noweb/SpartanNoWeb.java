package lol.apex.feature.module.implementation.movement.noweb;

import lol.apex.event.client.PreUpdateEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.game.GameTimer;
import lol.apex.util.player.MoveUtil;
import lol.apex.util.player.PlayerUtil;

public class SpartanNoWeb extends SubModule {
    public SpartanNoWeb() {
        super("Spartan");
    }

    public static void preUpdate(PreUpdateEvent event) {
        MoveUtil.setSpeedNoEvent(0.30);
        GameTimer.setSpeed(3);

        if(!PlayerUtil.isInWeb(mc.player)) {
            GameTimer.reset();
        }
    }

    @Override
    public void onDisable() {
        GameTimer.reset();
    }
}
