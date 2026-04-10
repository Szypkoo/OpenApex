package lol.apex.feature.module.setting.implementation;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lol.apex.feature.module.setting.base.BaseSetting;
import net.minecraft.client.util.InputUtil;

public class BindSetting extends BaseSetting<InputUtil.Key> {
    public BindSetting(String name, InputUtil.Key defaultKey) {
        super(name, defaultKey);
    }

    public InputUtil.Key getKeybind() {
        return getValue();
    }

    public void setKeybind(InputUtil.Key key) {
        setValue(key);
    }

    public String getKeyName() {
        if(getKeybind() == InputUtil.UNKNOWN_KEY) return "Unknown";
        return getKeybind().getLocalizedText().getString();
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue().getTranslationKey());
    }

    @Override
    public void fromJson(JsonElement json) {
        if (json == null || json.isJsonNull() || !json.isJsonPrimitive()) {
            return;
        }

        try {
            String key = json.getAsString();
            setValue(InputUtil.fromTranslationKey(key));
        } catch (Exception ignored) {
        }
    }
}
