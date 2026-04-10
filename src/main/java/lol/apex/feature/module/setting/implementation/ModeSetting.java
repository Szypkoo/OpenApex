package lol.apex.feature.module.setting.implementation;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.base.Togglable;
import lol.apex.feature.module.setting.base.BaseSetting;
import lombok.Getter;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModeSetting<T extends SubModule> extends BaseSetting<T> {
    @Getter
    private final List<T> values;

    @Getter
    private int index;
    private final @NonNull Togglable parent;

    @SafeVarargs
    public <M extends Togglable> ModeSetting(@NonNull M parent, String name, T... modes) {
        super(name, modes[0]);
        this.values = List.of(modes);
        this.index = 0;
        this.parent = parent;
    }

    @SafeVarargs
    public <M extends Togglable> ModeSetting(@NonNull M parent, String name, Supplier<T>... modes) {
        super(name, modes[0].get());
        this.values = Arrays
                .stream(modes)
                .map(Supplier::get)
                .toList();
        this.index = 0;
        this.parent = parent;
    }

    @SafeVarargs
    public <M extends Togglable> ModeSetting(@NonNull M parent, String name, Function<M, T>... modes) {
        super(name, modes[0].apply(parent));
        this.values = Arrays
                .stream(modes)
                .map(a -> a.apply(parent))
                .toList();
        this.index = 0;
        this.parent = parent;
    }

    @Override
    public void setValue(T value) {
        this.value.enabled(parent.enabled());
        super.setValue(value);
        final var idx = values.indexOf(value);
        assert idx != -1;
        this.index = idx;
        this.value.enabled(parent.enabled());
    }

    public void setValue(int index) {
        if (index >= 0 && index <= values.size() - 1) {
            setValue(values.get(index));
        }
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(index);
    }

    @Override
    public void fromJson(JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return;
        }

        try {
            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
                setValue(json.getAsInt());
                return;
            }

            if (json.isJsonPrimitive()) {
                String name = json.getAsString();
                for (int i = 0; i < values.size(); i++) {
                    T mode = values.get(i);
                    if (mode.name.equalsIgnoreCase(name)) {
                        setValue(i);
                        return;
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}
