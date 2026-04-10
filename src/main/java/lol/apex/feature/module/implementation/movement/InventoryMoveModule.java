package lol.apex.feature.module.implementation.movement;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.packet.PacketEvent;
import lol.apex.event.packet.PacketQueueEvent;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.manager.implementation.BlinkManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.Arrays;

import lol.apex.feature.module.base.Module;

import lol.apex.feature.module.base.*;

@ModuleInfo( 
    name = "InventoryMove",
    description = "Allows you to move while in your inventory.",
    category = Category.MOVEMENT
)
public class InventoryMoveModule extends Module {

    public final BoolSetting sneak = new BoolSetting("Sneak", false);
    public final BoolSetting blink = new BoolSetting("Blink", false);
    public final BoolSetting chestCheck = new BoolSetting("Chest Check", true);
    public final BoolSetting stopOnAction = new BoolSetting("Stop On Action", true);

    private boolean interacting;
    private int interactTicks;

    @SuppressWarnings("unused")
    @EventHook
    public void onTick(ClientTickEvent __) {
        if (!enabled() || skip()) {
            return;
        }

        if (chestCheck.getValue()) {
            if (mc.currentScreen instanceof GenericContainerScreen) return;
        }

        handleInteract();

        KeyBinding[] keys = {
                mc.options.forwardKey,
                mc.options.backKey,
                mc.options.leftKey,
                mc.options.rightKey,
                mc.options.jumpKey,
                mc.options.sprintKey
        };

        if (stopOnAction.getValue() && interacting) {
            stopMovementKeys(sneak.getValue());
            return;
        }


        if (sneak.getValue()) {
            keys = Arrays.copyOf(keys, keys.length + 1);
            keys[keys.length - 1] = mc.options.sneakKey;
        }

        for (KeyBinding key : keys) {
            key.setPressed(isPressed(key));
        }
    }


    @SuppressWarnings("unused")
    @EventHook
    public void onPacket(PacketEvent.Send event) {
        if (!blink.getValue()) return;

        if (event.getPacket() instanceof ClickSlotC2SPacket) {
            interacting = true;
            interactTicks = 5;
        }
    }

    public void stopMovementKeys(boolean sneak) {
        mc.options.forwardKey.setPressed(false);
        mc.options.backKey.setPressed(false);
        mc.options.leftKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
        mc.options.jumpKey.setPressed(false);
        mc.options.sprintKey.setPressed(false);

        if (sneak) {
            mc.options.sneakKey.setPressed(false);
        }
    }

    @SuppressWarnings("unused")
    @EventHook
    private void handleQueue(PacketQueueEvent.Send e) {
        if (shouldBlink() && e.packet() instanceof PlayerMoveC2SPacket) {
            e.action = BlinkManager.Action.QUEUE;
        }
    }

    private boolean shouldBlink() {
        return interacting
                && isMoving()
                && mc.currentScreen instanceof InventoryScreen;
    }

    private void handleInteract() {
        if (interactTicks > 0) {
            interactTicks--;
        } else {
            interacting = false;
        }
    }

    private boolean isMoving() {
        return mc.options.forwardKey.isPressed()
                || mc.options.backKey.isPressed()
                || mc.options.leftKey.isPressed()
                || mc.options.rightKey.isPressed()
                || mc.options.jumpKey.isPressed();
    }

    private boolean skip() {
        return mc.currentScreen instanceof CreativeInventoryScreen
                || mc.currentScreen instanceof ChatScreen
                || mc.currentScreen instanceof SignEditScreen
                || mc.currentScreen instanceof AnvilScreen
                || mc.currentScreen instanceof AbstractCommandBlockScreen
                || mc.currentScreen instanceof StructureBlockScreen;
    }

    private boolean isPressed(KeyBinding key) {
        int code = InputUtil.fromTranslationKey(
                key.getBoundKeyTranslationKey()
        ).getCode();

        return InputUtil.isKeyPressed(mc.getWindow(), code);
    }

    @Override
    public void onDisable() {
        interacting = false;
    }
}