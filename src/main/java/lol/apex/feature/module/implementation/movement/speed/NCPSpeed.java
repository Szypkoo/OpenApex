package lol.apex.feature.module.implementation.movement.speed;

import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;
import net.minecraft.entity.effect.StatusEffects;

import static lol.apex.util.player.MoveUtil.movementSideways;

public class NCPSpeed extends SubModule {
    public NCPSpeed() {
        super("NCP");
    }

    private static double moveSpeed = 0;
    private static boolean wasSlow;

    public static void onMove(PlayerMoveEvent event) {
        if(!mc.player.isTouchingWater()) {
            if(wasSlow && MoveUtil.isMoving()) {

                moveSpeed -= (moveSpeed - MoveUtil.getBaseMoveSpeed()) * 0.6;
                if(mc.player.hasStatusEffect(StatusEffects.SLOWNESS)) {
                    moveSpeed *= 0.9;
                }
                wasSlow = false;
            }

            if(mc.player.isOnGround() && MoveUtil.isMoving()) {
                mc.player.setVelocity(mc.player.getVelocity().x,  0.41999998688697815, mc.player.getVelocity().z);
                event.setY(0.41999998688697815);

                moveSpeed = MoveUtil.getBaseMoveSpeed() * 1.4;

                if(mc.player.hasStatusEffect(StatusEffects.SLOWNESS)) {
                    moveSpeed *= 0.9;
                }

                wasSlow = true;
            }

            moveSpeed -= moveSpeed / 80.0;

            if(MoveUtil.movementForward() != 0.0f || movementSideways() != 0.0f) {
                MoveUtil.modifySpeed(event, moveSpeed);
            } else {
                mc.player.setVelocity(0.0, mc.player.getVelocity().y, 0.0);
            }
        }
    }
}
