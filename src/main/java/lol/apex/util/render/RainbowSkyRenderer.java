package lol.apex.util.render;

import lol.apex.util.animation.api.AnimationUtil;
import lol.apex.util.animation.api.Easing;

import java.awt.*;

public class RainbowSkyRenderer {
    public static long speed = 5000L;
    public static AnimationUtil hueAnim = new AnimationUtil(Easing.SMOOTHERSTEP, 500);

    public static float currentHue = 0f;

    public static int getColorInt() {
        float targetHue = (System.currentTimeMillis() % speed) / (float) speed;

        hueAnim.run(targetHue);
        currentHue = hueAnim.getValue();

        return Color.getHSBColor(currentHue, 1f, 1f).getRGB();
    }
}