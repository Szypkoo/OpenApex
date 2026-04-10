package lol.apex.manager.implementation;

import lol.apex.Apex;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundManager {

    public enum Sounds {
        SMOOTH_TOGGLE_ON("smooth_on"),
        SMOOTH_TOGGLE_OFF("smooth_off"),

        KITTEN_TOGGLE_ON("kitten_on"),
        KITTEN_TOGGLE_OFF("kitten_off"),

        UI_HOVER("ui_hover"),
        UI_SLIDER_CHANGE("ui_slider"),
        UI_CHANGE("ui_change"),

        NOTIFICATION_NOTIFY("notification_notify"),

        CONTEXT_OPEN("context_open"),
        CONTEXT_CLOSE("context_close");

        private final Identifier identifier;
        private final SoundEvent soundEvent;

        Sounds(String name) {
            this.identifier = Identifier.of(Apex.MOD_ID, name);
            this.soundEvent = SoundEvent.of(identifier);
        }

        public void play() {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;

            if (player != null) {
                player.playSound(this.soundEvent, 1.0F, 1.0F);
            }
        }

        public void play(float volume, float pitch) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;

            if (player != null) {
                player.playSound(this.soundEvent, volume, pitch);
            }
        }
    }

    public void initialize() {
        for (Sounds sound : Sounds.values()) {
            Registry.register(Registries.SOUND_EVENT, sound.identifier, sound.soundEvent);
        }
    }

}
