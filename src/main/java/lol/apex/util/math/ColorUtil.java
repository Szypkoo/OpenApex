package lol.apex.util.math;

import imgui.ImColor;

import java.awt.*;

public class ColorUtil {
    public static int rgb(int r, int g, int b) {
        return 0xff000000 | (r << 16) | (g << 8) | b;
    }

    public static Color getTwoColorGradient(int index, float speed, Color c1, Color c2) {
        double timeOffset = System.currentTimeMillis() / 350.0;
        float wave = (float) ((Math.sin(index * speed - timeOffset) + 1.0) / 2.0);
        return lerpColor(c1, c2, wave);
    }

    public static Color getTwoColorGradientMix(int index, float speed, Color c1, Color c2) {
        double t = System.currentTimeMillis() / 350.0;

        float wave1 = (float) Math.sin(index * speed - t);
        float wave2 = (float) Math.sin(index * speed * 0.5 - t * 1.3);
        float wave3 = (float) Math.sin(index * speed * 1.7 - t * 0.7);

        float combined = (wave1 + wave2 + wave3) / 3f;
        float normalized = (combined + 1f) / 2f;

        return lerpColor(c1, c2, normalized);
    }

    public static Color lerpColor(Color a, Color b, float t) {
        t = Math.clamp(t, 0, 1);

        return new Color(
                (int) (a.getRed() + (b.getRed() - a.getRed()) * t),
                (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t),
                (int) (a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t)
        );
    }

    //larp
    public static int lerpColorInt(Color start, Color end, float t) {
        float clampedT = Math.clamp(t, 0.0f, 1.0f);

        int r = (int) (start.getRed() + (end.getRed() - start.getRed()) * clampedT);
        int g = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * clampedT);
        int b = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * clampedT);
        int a = (int) (start.getAlpha() + (end.getAlpha() - start.getAlpha()) * clampedT);

        return ImColor.rgba(r, g, b, a);
    }

    public static int rainbow(float offset) {
        float hue = (System.currentTimeMillis() % 2000L) / 2000f + offset;
        hue %= 1.0f;

        Color color = Color.getHSBColor(hue, 1f, 1f);
        return rgb(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static int conv(final Color color) {
        return ImColor.rgba(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static Color getShadowColor(final Color color) {
        int r = (color.getRed() >> 2);
        int g = (color.getGreen() >> 2);
        int b = (color.getBlue() >> 2);
        int a = color.getAlpha();

        return new Color(r, g, b, a);
    }
}
