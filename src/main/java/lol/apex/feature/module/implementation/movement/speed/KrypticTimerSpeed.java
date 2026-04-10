package lol.apex.feature.module.implementation.movement.speed;

import lol.apex.Apex;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.player.ScaffoldModule;
import lol.apex.util.game.GameTimer;
import lol.apex.util.player.MoveUtil;
import lol.apex.util.rotation.MathUtil;

public class KrypticTimerSpeed extends SubModule {
    public KrypticTimerSpeed() {
        super("KrypticTimer");
    }

    public static void onTick(ClientTickEvent event) {
        var scaffold = Apex.moduleManager.getByClass(ScaffoldModule.class);
        if(scaffold.isEnabled()) {
            GameTimer.reset();
            return;
        }

        if (MoveUtil.isMoving() && mc.player.isOnGround()) {
            GameTimer.setSpeed(MathUtil.nextFloat(1.09f, 1.29f));
            mc.player.jump();
        } else {
            GameTimer.reset();
        }
    }
}
