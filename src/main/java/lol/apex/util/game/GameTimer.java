package lol.apex.util.game;

import lombok.Getter;
import lombok.Setter;

// Minecraft Timer
public class GameTimer {

    @Getter
    @Setter
    public static float speed = 1.0f;

    public static void reset() {
        speed = 1.0f;
    }
}
