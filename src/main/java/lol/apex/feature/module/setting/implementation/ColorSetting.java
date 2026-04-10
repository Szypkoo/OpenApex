package lol.apex.feature.module.setting.implementation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lol.apex.feature.module.setting.base.BaseSetting;
import lol.apex.util.math.ColorUtil;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public class ColorSetting extends BaseSetting<Color> {
    @Getter
    @Setter
    private boolean rainbow;

    public ColorSetting(String name, Color value) {
        super(name, value);
    }

    @Override
    public Color getValue() {
        if (rainbow) {
            return new Color(ColorUtil.rainbow(0), true);
        }
        return super.getValue();
    }

    public void setValue(float[] color) {
        super.setValue(new Color(
                (int)(color[0] * 255),
                (int)(color[1] * 255),
                (int)(color[2] * 255),
                (int)(color[3] * 255)
        ));
    }

    public float[] getValueAsArray() {
        return new float[]{getValue().getRed(), getValue().getGreen(), getValue().getBlue(), getValue().getAlpha()};
    }

    public float[] getValueAsArray1_0() {
        Color c = getValue();
        return new float[]{
                c.getRed() / 255.0f,
                c.getGreen() / 255.0f,
                c.getBlue() / 255.0f,
                c.getAlpha() / 255.0f
        };
    }

    @Override
    public JsonElement toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("value", value.getRGB());
        object.addProperty("rainbow", rainbow);
        return object;
    }

    @Override
    public void fromJson(JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return;
        }

        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
            setValue(new Color(json.getAsInt(), true));
            return;
        }

        if (!json.isJsonObject()) {
            return;
        }

        JsonObject object = json.getAsJsonObject();

        JsonElement valueElement = object.get("value");
        if (valueElement != null && valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
            setValue(new Color(valueElement.getAsInt(), true));
        }

        JsonElement rainbowElement = object.get("rainbow");
        if (rainbowElement != null && rainbowElement.isJsonPrimitive()) {
            setRainbow(rainbowElement.getAsBoolean());
        }
    }
}
