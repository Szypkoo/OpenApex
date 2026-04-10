package lol.apex.feature.module.setting.implementation;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lol.apex.feature.module.setting.base.BaseSetting;

public class BoolSetting extends BaseSetting<Boolean> {

    public BoolSetting(String name, boolean value) {
        super(name, value);
    }

    public void toggle() {
        value = !value;
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
            setValue(json.getAsBoolean());
        } catch (Exception ignored) {
        }
    }
}
