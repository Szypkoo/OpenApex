package lol.apex.feature.module.setting.implementation;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lol.apex.feature.module.setting.base.BaseSetting;
import lombok.Getter;

@Getter
public class EnumSetting<T extends Enum<T>> extends BaseSetting<T> {
    private final T[] values;
    private int index;

    public EnumSetting(String name, T value) {
        super(name, value);
        this.values = value.getDeclaringClass().getEnumConstants();
        this.index = value.ordinal();
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
        this.index = value.ordinal();
    }

    public void setValue(int index) {
        if(index >= 0 && index <= values.length - 1) {
            setValue(values[index]);
        }
    }

    public boolean is(T value){
        return super.getValue() == value;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue().name());
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

            String name = primitive.getAsString();
            for (T possible : values) {
                if (possible.name().equalsIgnoreCase(name)) {
                    setValue(possible);
                    return;
                }
            }
        } catch (Exception ignored) {
        }
    }
}
