package lol.apex.util.skidded;

import lol.apex.util.annotation.Pasted;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

@Pasted("From Prism")
public class ClickUtil {
    private static final MinecraftClient MC = MinecraftClient.getInstance();

    public static void action(Button button, boolean action) {
        KeyBinding bind = switch (button) {
            case LEFT -> MC.options.attackKey;
            case RIGHT -> MC.options.useKey;
        };

        if (action) {
            press(bind);
        } else {
            release(bind);
        }
    }

    private static void press(KeyBinding key) {
        if (key == null) return;

        key.setPressed(true);

        int currentPresses = key.timesPressed;
        key.timesPressed = currentPresses + 1;
    }

    private static void release(KeyBinding key) {
        if (key == null) return;

        key.setPressed(false);
    }

    public enum Button {
        LEFT,
        RIGHT
    }
}
