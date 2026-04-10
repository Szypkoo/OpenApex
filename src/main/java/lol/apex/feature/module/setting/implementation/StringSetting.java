package lol.apex.feature.module.setting.implementation;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lol.apex.feature.module.setting.base.BaseSetting;
import lombok.Getter;

import java.util.List;

public class StringSetting extends BaseSetting<String> {
    @Getter
    private final List<String> values;
    @Getter
    private int index = 0;

    public StringSetting(String name, String... values) {
        super(name, values[0]);
        this.values = List.of(values);
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        this.index = values.indexOf(value);
    }

    public void setValue(int index) {
        if(index >= 0 && index <= values.size() - 1) {
            setValue(values.get(index));
        }
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

        var primitive = json.getAsJsonPrimitive();
        try {
            if (primitive.isNumber()) {
                setValue(primitive.getAsInt());
                return;
            }

            setValue(primitive.getAsString());
        } catch (Exception ignored) {
        }
    }
}
