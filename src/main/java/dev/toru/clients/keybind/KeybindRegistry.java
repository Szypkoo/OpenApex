package dev.toru.clients.keybind;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class KeybindRegistry {
    @Getter
    private static final List<Keybindable> keybindables = new ArrayList<>();

    public static void subscribe(Keybindable keybindable) {
        if(!keybindables.contains(keybindable)) {
            keybindables.add(keybindable);
        }
    } 

    public static void unsubscribe(Keybindable kb) {
        keybindables.remove(kb);
    }

    public static void clear() {
        keybindables.clear();
    }
}
