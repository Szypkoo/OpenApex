package lol.apex.feature.module.implementation.movement.speed.spartan;

import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;

public class SpartanFastSpeed extends SubModule {
    public SpartanFastSpeed() {
        super("Spartan");
    }

    public static void onMove(PlayerMoveEvent event) {
        if(!mc.player.input.playerInput.forward()) {
            return;
        }

        boolean wearingLeatherBoots = mc.player.getEquippedStack(EquipmentSlot.FEET).isOf(Items.LEATHER_BOOTS);
        double horizontalMove = wearingLeatherBoots ? 1.8 : 1.3;

        if (mc.player.isOnGround()) {
            double currentSpeed = MoveUtil.getSpeed();
            MoveUtil.modifySpeed(event, currentSpeed * horizontalMove);

            for (int i = 0; i < 4; i++) {
                mc.player.jump();
            }

            MoveUtil.setMotionY(0.42f);
        }
    }
}
