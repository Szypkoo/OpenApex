package lol.apex.feature.module.implementation.combat;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.player.PlayerRotationEvent;
import lol.apex.feature.module.base.*;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.player.InventoryUtil;
import lol.apex.util.rotation.MathUtil;
import lol.apex.util.rotation.RotationUtil;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

import lol.apex.feature.module.base.Module;

@ModuleInfo(
        name = "AutoPearlCatch",
        description = "Automatically throws wind charges and pearls.",
        category = Category.COMBAT
)
public class AutoPearlCatchModule extends Module {

    public final BoolSetting autoDisable = new BoolSetting("Auto Disable", true);
    public final SliderSetting windDelay = new SliderSetting("Wind Delay", 3, 0, 10, 1);
    public final SliderSetting rotationSpeed = new SliderSetting("Rotation Speed", 180f, 10f, 360f, 1f);

    private int stage = 0;
    private int tickCounter = 0;
    private int previousSlot = -1;

    private boolean shouldThrowPearl = false;
    private boolean shouldThrowWindCharge = false;
    private boolean rotateDown = false;

    @Override
    public void onEnable() {
        stage = 0;
        tickCounter = 0;
        previousSlot = -1;
        shouldThrowPearl = false;
        shouldThrowWindCharge = false;
        rotateDown = false;

        if (mc.player == null) return;

        int pearlSlot = InventoryUtil.findItemInHotbar(Items.ENDER_PEARL);
        int windSlot = InventoryUtil.findItemInHotbar(Items.WIND_CHARGE);

        if (pearlSlot == -1 || windSlot == -1) {
            toggle();
            return;
        }

        previousSlot = mc.player.getInventory().getSelectedSlot();
    }

    @Override
    public void onDisable() {
        if (mc.player == null) return;

        restore();
    }

    @EventHook
    public void onTick(ClientTickEvent event) {
        if (mc.player == null) return;

        if (shouldThrowPearl) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
            shouldThrowPearl = false;
        }

        if (shouldThrowWindCharge) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
            shouldThrowWindCharge = false;
        }

        switch (stage) {

            case 0 -> {
                int pearlSlot = InventoryUtil.findItemInHotbar(Items.ENDER_PEARL);
                if (pearlSlot == -1) return;

                mc.player.getInventory().setSelectedSlot(pearlSlot);
                rotateDown = true;

                stage = 1;
            }

            case 1 -> {
                rotateDown = true;

                if (isLookingDown()) {
                    shouldThrowPearl = true;
                    stage = 2;
                    tickCounter = 0;
                }
            }

            case 2 -> {
                rotateDown = true;
                tickCounter++;

                if (tickCounter >= Math.max(1, windDelay.getValue().intValue())) {
                    int windSlot = InventoryUtil.findItemInHotbar(Items.WIND_CHARGE);
                    if (windSlot == -1) {
                        restore();
                        return;
                    }

                    mc.player.getInventory().setSelectedSlot(windSlot);
                    stage = 3;
                }
            }

            case 3 -> {
                rotateDown = true;

                if (isLookingDown()) {
                    shouldThrowWindCharge = true;
                    stage = 4;
                    tickCounter = 0;
                }
            }

            case 4 -> {
                tickCounter++;

                if (tickCounter >= 5) {
                    restore();

                    if (autoDisable.getValue()) {
                        toggle();
                    } else {
                        stage = 5;
                    }
                }
            }
        }
    }

    private boolean isLookingDown() {
        return Math.abs(mc.player.getPitch() - (-90f)) < 3f;
    }

    private void restore() {
        rotateDown = false;

        if (previousSlot != -1) {
            mc.player.getInventory().setSelectedSlot(previousSlot);
        }
    }

    @EventHook
    public void onRotation(PlayerRotationEvent event) {
        if (mc.player == null || !rotateDown) return;

        float targetPitch = -90f;

        float pitch = RotationUtil.smoothRot(
                mc.player.getPitch(),
                targetPitch,
                rotationSpeed.getValue().floatValue()
        );

        pitch += MathUtil.nextFloat(-0.1f, 0.1f);

        event.pitch(pitch);
        event.yaw(mc.player.getYaw());
    }
}