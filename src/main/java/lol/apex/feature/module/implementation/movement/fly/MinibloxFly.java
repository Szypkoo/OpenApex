/*
with help from Vape For Miniblox
https://codeberg.org/Miniblox/VapeRewrite/src/branch/main/src/features/modules/impl/blatant/Fly.ts
 */

package lol.apex.feature.module.implementation.movement.fly;

import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModuleWithParent;
import lol.apex.feature.module.implementation.movement.FlyModule;
import lol.apex.util.game.ChatUtil;
import lol.apex.util.player.MoveUtil;

public class MinibloxFly extends SubModuleWithParent<FlyModule> {

    private int ticks = 0;

    public MinibloxFly(FlyModule parent) {
        super(parent, "Miniblox");
    }

    @Override
    public void onEnable() {
        ticks = 0;

        desyncOn();
        super.onEnable();
    }

    private void desyncOn() {
        ChatUtil.sendChatMessage("Desyncing you...");
        mc.player.networkHandler.sendChatMessage("/desync on");
    }

    private void desyncOff() {
        ChatUtil.sendChatMessage("Disabling desync...");
        mc.player.networkHandler.sendChatMessage("/desync off");
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.setVelocity(
                    Math.max(Math.min(mc.player.getVelocity().x, 0.3), -0.3),
                    mc.player.getVelocity().y,
                    Math.max(Math.min(mc.player.getVelocity().z, 0.3), -0.3)
            );
        }
        ticks = 0;

        desyncOff();
        super.onDisable();
    }

    public static void onMove(FlyModule parent, PlayerMoveEvent event) {

        double speed = parent.speed.getValue() * 10;
        double forward = MoveUtil.movementForward();
        double strafe = MoveUtil.movementSideways();
        float yaw = mc.player.getYaw();

        double motionX = 0;
        double motionZ = 0;

        if (forward != 0 || strafe != 0) {
            if (strafe != 0) {
                yaw += (strafe > 0 ? 90 : -90) * (forward != 0 ? forward * 0.5 : 1);
            }

            motionX = -Math.sin(Math.toRadians(yaw)) * speed;
            motionZ = Math.cos(Math.toRadians(yaw)) * speed;

            if (forward < 0) {
                motionX = -motionX;
                motionZ = -motionZ;
            }
        }

        mc.player.setVelocity(motionX, mc.player.getVelocity().y, motionZ);

        double verticalSpeed = parent.increment.getValue();
        double y = 0.0;

        if (mc.options.jumpKey.isPressed()) {
            y += verticalSpeed;
        }

        if (mc.options.sneakKey.isPressed()) {
            y -= verticalSpeed;
        }

        MoveUtil.setMotionY(y);
    }
}