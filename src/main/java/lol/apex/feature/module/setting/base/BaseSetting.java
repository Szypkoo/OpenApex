package lol.apex.feature.module.setting.base;

import lol.apex.feature.file.data.Serializable;
import lombok.Getter;
import lombok.Setter;

import java.util.function.BooleanSupplier;

@Getter
public abstract class BaseSetting<T> implements Serializable {
    protected final String name;

    @Setter
    protected T value;

    public BaseSetting(String name, T value) {
        this.name = name;
        this.value = value;
    }

    private BooleanSupplier hidden = () -> false;

    public boolean isHidden() {
        return hidden.getAsBoolean();
    }

    @SuppressWarnings("unchecked")
    public <I extends BaseSetting<?>> I hide(BooleanSupplier hidden) {
        this.hidden = hidden;
        return (I) this;
    }
}
