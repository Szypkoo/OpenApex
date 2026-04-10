package lol.apex.feature.module.implementation.movement.speed;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;

public class CubeCraftSpeed extends SubModule {
    public CubeCraftSpeed() {
        super("CubeCraft");
    }

    private static int offGroundTicks;

    public static void onMove(PlayerMoveEvent event) {
        if (!MoveUtil.isMoving())
            return;

        if (mc.player.isOnGround()) {
            offGroundTicks = 0;
        } else {
            offGroundTicks ++;
        }

        if (!mc.player.horizontalCollision) {
            switch (offGroundTicks) {
                case 1 -> MoveUtil.setMotionY(MoveUtil.getMotionY() - 0.5);
                case 5 -> MoveUtil.setMotionY(MoveUtil.getMotionY() - 0.4);
            }
        }

        /*
                This can be used for a cc fly btw
         */
    //    if (mc.player.hurtTime != 0) {
   //     //    MoveUtil.modifySpeed(event, 5);
     //   else {
       //     MoveUtil.modifySpeed(event, mc.player.isOnGround() ? 0.55 : MoveUtil.getBaseMoveSpeed());
        MoveUtil.modifySpeed(event, mc.player.isOnGround() ? 0.55 : MoveUtil.getBaseMoveSpeed());
    }

    public static void onTick(ClientTickEvent event) {
        if (!MoveUtil.isMoving())
            return;

        if (mc.player.isOnGround()) mc.player.jump();
    }
}
