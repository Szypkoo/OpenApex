package lol.apex.feature.module.setting.implementation;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lol.apex.feature.module.setting.base.BaseSetting;
import lombok.Getter;

public class SliderSetting extends BaseSetting<Float> {

    private static final float MINIMUM_STEP = 0.0001F;

    @Getter
    private float min, max, step;

    public SliderSetting(String name, float value, float min, float max, float step) {
        super(name, value);
        this.min = min;
        this.max = max;
        this.step = Math.max(step, MINIMUM_STEP);
    }

    @Override
    public void setValue(Float value) {
        this.value = Math.clamp(value, this.min, this.max);
        this.value = Math.round(this.value / step) * step;
        super.setValue(value);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void fromJson(JsonElement json) {
        if (json == null || json.isJsonNull() || !json.isJsonPrimitive()) {
            return;
        }

        try {
            setValue(json.getAsFloat());
        } catch (Exception ignored) {
        }
    }

}
