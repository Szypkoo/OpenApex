package lol.apex.feature.module.implementation.movement.speed;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModuleWithParent;
import lol.apex.feature.module.implementation.movement.SpeedModule;
import lol.apex.util.game.GameTimer;
import lol.apex.util.player.MoveUtil;
import lol.apex.util.player.PlayerUtil;

public class CustomSpeed extends SubModuleWithParent<SpeedModule> {

    public CustomSpeed(SpeedModule parent) {
        super(parent, "Custom");
    }

    public static void onMove(SpeedModule parent, PlayerMoveEvent event) {
        MoveUtil.modifySpeed(event, parent.speed.getValue());
    }

    public static void onTick(SpeedModule parent, ClientTickEvent event) {

        if(parent.custom_changeTimerSpeed.getValue()) {
            GameTimer.setSpeed(parent.custom_timerAmount.getValue().floatValue());
        }

        if(MoveUtil.isMoving()) {

            if((!parent.custom_checkGround.getValue() || mc.player.isOnGround()) &&
                    parent.custom_jumpFromGround.getValue()) {
                PlayerUtil.jump();

                if(parent.custom_changeJumpHeight.getValue()) {
                    MoveUtil.setMotionY(parent.custom_jumpHeight.getValue().floatValue());
                }

                if(parent.custom_useJumpBoost.getValue()) {
                    MoveUtil.boost(parent.custom_jumpBoost.getValue());
                }
            }

            if(!mc.player.isOnGround()) {
                double motionY = MoveUtil.getMotionY();
                if(parent.custom_usePulldown.getValue()) {
                    if(motionY > 0) {
                        MoveUtil.addMotionY(-parent.custom_pullDown.getValue());
                    } else {
                        MoveUtil.addMotionY(-parent.custom_fallPullDown.getValue());
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        GameTimer.reset();
    }
}
