package lol.apex.util.game;

import lol.apex.util.CommonUtil;
import lol.apex.util.CommonVars;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.awt.*;

public class ChatUtil implements CommonVars {
    public static void sendChatMessage(String message) {
        if (mc.player == null || mc.world == null) return;

        Text base = ChatUtil.gradient(
                "Apex",
                CommonUtil.getFirstClientColor(),
                CommonUtil.getSecondClientColor()
        );

        Text main = Text.literal("")
                .append(base)
                .append(Text.literal(" > ").styled(s -> s.withColor(0x2B2B2B)))
                .append(Text.literal(message));

        mc.player.sendMessage(main, false);
    }

    public static void sendCommandErrorMessage(String message) {
        if(mc.player == null || mc.world == null) return;

        Text base = Text.literal("Apex").styled(s -> s.withColor(0x2B2B2B));

        Text main = Text.literal("")
                .append(base)
                .append(Text.literal(" > ").styled(s -> s.withColor(0x2B2B2B)))
                .append(Text.literal(message).styled(s -> s.withColor(Color.RED.getRGB())));


        mc.player.sendMessage(main, false);
    }

    public static void sendACMessage(String message) {
        if (mc.player == null) return;

        Text prefix = Text.literal("Anti Cheat")
                .styled(s -> s.withColor(0xFF5555).withBold(true));

        Text arrow = Text.literal(" > ")
                .styled(s -> s.withColor(0xAAAAAA).withBold(true));

        Text mainMessage = Text.literal(message)
                .styled(s -> s.withColor(0xFFFFFF));

        boolean overlay = false;
        mc.player.sendMessage(prefix.copy().append(arrow).append(mainMessage), overlay);
    }

    public static MutableText gradient(String text, Color start, Color end) {
        MutableText result = Text.literal("");

        int length = text.length();
        if (length == 0) return result;

        for (int i = 0; i < length; i++) {
            float ratio = length == 1 ? 0 : (float) i / (length - 1);

            int r = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
            int g = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
            int b = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));

            int rgb = new Color(r, g, b).getRGB();

            result.append(
                    Text.literal(String.valueOf(text.charAt(i)))
                            .styled(s -> s.withColor(rgb))
            );
        }

        return result;
    }

    public static MutableText gradient(String text, int startColor, int endColor) {
        MutableText result = Text.literal("");

        int length = text.length();
        if (length == 0) return result;

        int sr = (startColor >> 16) & 0xFF;
        int sg = (startColor >> 8) & 0xFF;
        int sb = startColor & 0xFF;

        int er = (endColor >> 16) & 0xFF;
        int eg = (endColor >> 8) & 0xFF;
        int eb = endColor & 0xFF;

        for (int i = 0; i < length; i++) {
            float ratio = length == 1 ? 0 : (float) i / (length - 1);

            int r = (int) (sr + (er - sr) * ratio);
            int g = (int) (sg + (eg - sg) * ratio);
            int b = (int) (sb + (eb - sb) * ratio);

            int rgb = (r << 16) | (g << 8) | b;

            result.append(
                    Text.literal(String.valueOf(text.charAt(i)))
                            .styled(s -> s.withColor(rgb))
            );
        }

        return result;
    }
}
