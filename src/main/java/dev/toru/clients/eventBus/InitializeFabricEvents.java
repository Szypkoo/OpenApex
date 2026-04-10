package dev.toru.clients.eventBus;

import dev.toru.clients.keybind.KeybindRegistry;
import dev.toru.clients.keybind.KeybindState;
import dev.toru.clients.keybind.Keybindable;
import lol.apex.Apex;
import lol.apex.event.client.ClientPostEvent;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.render.Render2DEvent;
import lol.apex.util.CommonVars;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.util.Identifier;

public class InitializeFabricEvents implements CommonVars {
    public static final Identifier RENDER_IDENTIFIER = Identifier.of("apex", "nigger");

    public static void initialize() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if(client.player == null) return;
            Apex.eventBus.post(new ClientTickEvent());
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (mc.currentScreen != null) return;

            Apex.eventBus.post(new ClientPostEvent());

            for (Keybindable keybindable : KeybindRegistry.getKeybindables()) {

                int key = keybindable.getKey();
                if (key <= 0) continue;

                boolean pressed = org.lwjgl.glfw.GLFW.glfwGetKey(
                        mc.getWindow().getHandle(),
                        key
                ) == org.lwjgl.glfw.GLFW.GLFW_PRESS;

                if (pressed && !KeybindState.wasPressed(keybindable.getKeybindId())) {
                    keybindable.onBindPress();
                }

                KeybindState.setPressed(keybindable.getKeybindId(), pressed);
            }
        });

        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, RENDER_IDENTIFIER, (drawContext, tickDelta) -> {

            if (mc.player == null) return;
            if (mc.getOverlay() instanceof SplashOverlay) return;

            int scaledWidth = mc.getWindow().getScaledWidth();
            int scaledHeight = mc.getWindow().getScaledHeight();

            Apex.eventBus.post(new Render2DEvent(drawContext, tickDelta, scaledWidth, scaledHeight));
        });
    }
}
