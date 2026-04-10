package lol.apex.feature.module.base;

import lol.apex.Apex;
import lol.apex.feature.module.setting.base.BaseSetting;
import lol.apex.util.CommonVars;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class SubModule implements Togglable, CommonVars {
    public final String name;
    public final @Nullable String description;
    public final @Nullable String category;

    private boolean enabled;
    private boolean expanded = false;

    @Getter
    private final List<BaseSetting<?>> baseSettings = new ArrayList<>();

    public SubModule(String name, @Nullable String description) {
        this.name = name;
        this.description = description;
        this.category = null;
    }

    public SubModule(String name) {
        this.name = name;
        this.description = null;
        this.category = null;
    }

    public SubModule(String name, @Nullable String description, @Nullable String category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    private void enable() {
        Apex.eventBus.subscribe(this);
        onEnable();
    }

    /** subscribes the module to the event bus and calls the `onEnable` callback. **/
    public void onEnable() {
    }

    /** unsubscribes the module from the event bus and calls the `onDisable` callback. **/
    private void disable() {
        Apex.eventBus.unsubscribe(this);
        onDisable();
    }

    public void onDisable() {}

    @Override
    public boolean enabled() {
        return this.enabled;
    }


    @Override
    public void enabled(boolean enabled) {
        this.enabled = enabled;
        if(enabled) {
            enable();
        } else {
            disable();
        }
    }

    public String getSuffix() {
        return "";
    }

    public void addSetting(BaseSetting<?> baseSetting) {
        baseSettings.add(baseSetting);
    }

    public void addSettings(BaseSetting<?>... baseSettings) {
        Collections.addAll(this.baseSettings, baseSettings);
    }

    public void toggleExpanded() {
        expanded = !expanded;
    }

}
