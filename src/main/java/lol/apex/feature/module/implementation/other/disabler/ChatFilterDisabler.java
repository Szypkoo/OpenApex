package lol.apex.feature.module.implementation.other.disabler;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.packet.SendMessageEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

public class ChatFilterDisabler extends SubModule {
    public static final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.REVERSE);

    @RequiredArgsConstructor
    public enum Mode {
        REVERSE("Reverse"),
        DIFF("Diff"),
        DIFF2("Diff2");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    public ChatFilterDisabler() {
        super("Chat Filter", "Bypasses the chat filter", "Generic");
        addSetting(mode);
    }

    public static String bypass(String msg) {
        return switch (mode.getValue()) {
            case REVERSE -> "\u202E" + new StringBuilder(msg).reverse();
            case DIFF -> applyDiff(msg, '\u2800');
            case DIFF2 -> applyDiff(msg, '\u200B');
        };
    }

    @EventHook
    public static void onSendMessage(SendMessageEvent e) {
        e.message = bypass(e.message);
    }

    private static @NonNull String applyDiff(@NonNull String msg, char chr) {
        StringBuilder sb = new StringBuilder(msg.length() * 2);

        for (int i = 0; i < msg.length(); i++) {
            sb.append(msg.charAt(i));

            if (i != msg.length() - 1) {
                sb.append(chr);
            }
        }

        return sb.toString();
    }
}