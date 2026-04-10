package lol.apex.feature.module.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.toru.clients.keybind.KeybindRegistry;
import dev.toru.clients.keybind.Keybindable;
import lol.apex.Apex;
import lol.apex.feature.file.data.Serializable;
import lol.apex.feature.module.implementation.visual.InterfaceModule;
import lol.apex.feature.module.setting.base.BaseSetting;
import lol.apex.feature.ui.notification.Notification;
import lol.apex.util.CommonVars;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
public class Module implements Keybindable, Togglable, CommonVars, Serializable {
    private final String name;
    private final String description;
    private final Category category;

    @Setter
    private int key; // OVERRIDEABLE // SETTABLE

    private boolean enabled, expanded = true;

    @Getter
    private final List<BaseSetting<?>> baseSettings = new ArrayList<>();

    public Module() {
        ModuleInfo info = this.getClass().getAnnotation(ModuleInfo.class);
        if (info != null) {
            this.name = info.name();
            this.description = info.description();
            this.category = info.category();
        } else {
            throw new IllegalStateException("Module class " + getClass().getSimpleName() + " must have @ModuleInfo");
        }

        KeybindRegistry.subscribe(this);
    }

    /**
     * subscribes the module to the event bus and calls the `onEnable` callback.
     **/
    public void onEnable() {
    }

    public void onDisable() {
    }

    private void enable() {
        Apex.eventBus.subscribe(this);
        onEnable();
    }

    /**
     * unsubscribes the module from the event bus and calls the `onDisable` callback.
     **/
    private void disable() {
        Apex.eventBus.unsubscribe(this);
        onDisable();
    }

    @Override
    public boolean enabled() {
        return this.enabled;
    }

    @Override
    public void enabled(boolean enabled) {
        this.enabled = enabled;

        if (enabled) {
            enable();
        } else {
            disable();
        }

        Apex.notificationRenderer.push(getName(), enabled ? "Enabled" : "Disabled");

        if (Apex.moduleManager.getByClass(InterfaceModule.class) != null && Apex.moduleManager.getByClass(InterfaceModule.class).useToggleSounds.getValue()) {
            Apex.moduleManager.getByClass(InterfaceModule.class).toggleSound.getValue().run(enabled);
        }
    }

    public void enabledNoNoise(boolean enabled) {
        this.enabled = enabled;

        if (enabled) {
            enable();
        } else {
            disable();
        }
    }

    public void toggle() {
        enabled(!enabled);
    }

    public String getSuffix() {
        return "";
    }

    public void toggleExpanded() {
        expanded = !expanded;
    }

    @Override
    public void onBindPress() {
        toggle();
    }

    @Override
    public String getKeybindId() {
        return name;
    }

    public void toggleMsg() {
        Apex.sendChatMessage(name + " " + (enabled ? "was enabled" : "was disabled"));
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public JsonElement toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("enabled", enabled);
        object.addProperty("expanded", expanded);
        object.addProperty("key", key);

        JsonObject settingsObject = new JsonObject();
        for (BaseSetting<?> setting : baseSettings) {
            settingsObject.add(setting.getName(), setting.toJson());
        }
        object.add("settings", settingsObject);

        return object;
    }

    @Override
    public void fromJson(JsonElement json) {
        if (json == null || json.isJsonNull() || !json.isJsonObject()) {
            return;
        }

        JsonObject object = json.getAsJsonObject();

        JsonElement keyElement = object.get("key");
        if (keyElement != null && keyElement.isJsonPrimitive()) {
            try {
                setKey(keyElement.getAsInt());
            } catch (Exception ignored) {
            }
        }

        JsonElement expandedElement = object.get("expanded");
        if (expandedElement != null && expandedElement.isJsonPrimitive()) {
            try {
                expanded = expandedElement.getAsBoolean();
            } catch (Exception ignored) {
            }
        }

        JsonObject settingsObject = null;
        JsonElement settingsElement = object.get("settings");
        if (settingsElement != null && settingsElement.isJsonObject()) {
            settingsObject = settingsElement.getAsJsonObject();
        }

        for (BaseSetting<?> setting : baseSettings) {
            JsonElement settingElement = null;

            if (settingsObject != null && settingsObject.has(setting.getName())) {
                settingElement = settingsObject.get(setting.getName());
            } else if (object.has(setting.getName())) {
                settingElement = object.get(setting.getName());
            }

            if (settingElement != null) {
                try {
                    setting.fromJson(settingElement);
                } catch (Exception e) {
                    Apex.LOGGER.warn("Failed to load setting {} for module {}", setting.getName(), name, e);
                }
            }
        }

        JsonElement enabledElement = object.get("enabled");
        if (enabledElement != null && enabledElement.isJsonPrimitive()) {
            try {
                enabledNoNoise(enabledElement.getAsBoolean());
            } catch (Exception ignored) {
            }
        }
    }
}
