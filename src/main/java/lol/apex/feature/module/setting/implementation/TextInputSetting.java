package lol.apex.feature.module.setting.implementation;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import imgui.type.ImString;
import lol.apex.feature.module.setting.base.BaseSetting;

public class TextInputSetting extends BaseSetting<String> {
    private final int maxLength;

    public TextInputSetting(String name, String value, int maxLength) {
        super(name, value);
        this.maxLength = maxLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public ImString asImString() {
        return new ImString(getValue(), maxLength);
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
            setValue(json.getAsString());
        } catch (Exception ignored) {
        }
    }
}
