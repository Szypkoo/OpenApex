package lol.apex.feature.module.implementation.movement.speed.spartan;

import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;

public class SpartanFastLatestSpeed extends SubModule {
    public SpartanFastLatestSpeed() {
        super("SpartanFastLatest");
    }

    public static void onMove(PlayerMoveEvent event) {
        if(!mc.player.input.playerInput.forward()) {
            return;
        }

        double horizontalMove = 1.3;

        if (mc.player.isOnGround()) {
            double currentSpeed = MoveUtil.getSpeed();
            MoveUtil.modifySpeed(event, currentSpeed * horizontalMove);

            mc.player.jump();

            MoveUtil.setMotionY(0.42f);
        }
    }
}
