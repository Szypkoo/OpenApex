package lol.apex.feature.module.setting.implementation;

import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import lol.apex.feature.module.setting.base.BaseSetting;
import lombok.Getter;
import org.joml.Vector2f;

public class RangeSetting extends BaseSetting<Vector2f> {
    private static final float MINIMUM_STEP = 0.0001F;

    @Getter
    private float min, max, step;

    public RangeSetting(String name, Vector2f value, float min, float max, float step) {
        super(name, value);
        this.min = min;
        this.max = max;
        this.step = Math.max(step, MINIMUM_STEP);
        setValue(value);
    }

    @Override
    public void setValue(Vector2f value) {
        if (value == null) {
            return;
        }

        float lower = snapAndClamp(value.x);
        float upper = snapAndClamp(value.y);

        if (lower > upper) {
            float swap = lower;
            lower = upper;
            upper = swap;
        }

        super.setValue(new Vector2f(lower, upper));
    }

    private float snapAndClamp(float value) {
        float snapped = Math.round(value / step) * step;
        return Math.clamp(snapped, min, max);
    }

    @Override
    public JsonElement toJson() {
        JsonArray array = new JsonArray(2);
        array.add(getValue().x);
        array.add(getValue().y);
        return array;
    }

    @Override
    public void fromJson(JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return;
        }

        try {
            if (json.isJsonArray() && json.getAsJsonArray().size() >= 2) {
                JsonArray array = json.getAsJsonArray();
                setValue(new Vector2f(array.get(0).getAsFloat(), array.get(1).getAsFloat()));
                return;
            }

            if (json.isJsonPrimitive()) {
                Vector2f parsed = parseVector(json.getAsString());
                if (parsed != null) {
                    setValue(parsed);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private Vector2f parseVector(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        String[] parts = raw.replaceAll("[^0-9,\\-.]+", " ").trim().split("[,\\s]+");
        if (parts.length < 2) {
            return null;
        }

        return new Vector2f(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]));
    }
}