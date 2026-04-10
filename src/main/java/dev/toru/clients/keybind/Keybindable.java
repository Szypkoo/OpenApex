package dev.toru.clients.keybind;

public interface Keybindable {
    int getKey();
    void onBindPress();
    String getKeybindId();
}
