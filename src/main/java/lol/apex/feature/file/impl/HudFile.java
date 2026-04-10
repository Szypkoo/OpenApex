package lol.apex.feature.file.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lol.apex.Apex;
import lol.apex.feature.file.BaseFile;
import lol.apex.feature.module.setting.base.BaseSetting;
import lol.apex.feature.ui.hud.HudComponent;
import lol.apex.feature.ui.hud.impl.custom.CustomHudComponent;
import lol.apex.feature.ui.hud.impl.custom.CustomModuleListHudComponent;
import lol.apex.feature.ui.hud.impl.custom.CustomRectangleHudComponent;
import lol.apex.feature.ui.hud.impl.custom.CustomTextHudComponent;
import org.jspecify.annotations.NonNull;

public class HudFile extends BaseFile<JsonElement> {
    public static final HudFile DEFAULT = new HudFile("Hud.json");

    private static final String COMPONENTS_KEY = "components";
    private static final String TYPE_KEY = "type";
    private static final String NAME_KEY = "name";
    private static final String ENABLED_KEY = "enabled";
    private static final String X_KEY = "x";
    private static final String Y_KEY = "y";
    private static final String HORIZONTAL_ANCHOR_KEY = "horizontalAnchor";
    private static final String VERTICAL_ANCHOR_KEY = "verticalAnchor";
    private static final String ANCHOR_OFFSET_X_KEY = "anchorOffsetX";
    private static final String ANCHOR_OFFSET_Y_KEY = "anchorOffsetY";
    private static final String SETTINGS_KEY = "settings";

    public HudFile(String file) {
        super(file, JsonElement.class);
    }

    @Override
    protected void load(JsonElement el) {
        if (el.isJsonObject()) { // compatibility
            final var obj = el.getAsJsonObject();
            if (!obj.has(COMPONENTS_KEY) || !obj.get(COMPONENTS_KEY).isJsonArray()) {
                return;
            }
            el = obj.getAsJsonArray(COMPONENTS_KEY);
        }

        Apex.hudRenderer.clear();

        final var components = el.getAsJsonArray();

        for (JsonElement element : components) {
            if (!element.isJsonObject()) {
                continue;
            }

            JsonObject componentObject = element.getAsJsonObject();
            String type = getString(componentObject, TYPE_KEY, "");
            String name = getString(componentObject, NAME_KEY, "Custom Element");
            float x = getFloat(componentObject, X_KEY, 0.0f);
            float y = getFloat(componentObject, Y_KEY, 0.0f);

            CustomHudComponent component = createComponent(type, name, x, y);
            if (component == null) {
                Apex.LOGGER.warn("Skipping unknown HUD component type {}", type);
                continue;
            }
            component.enabled = getBoolean(componentObject, ENABLED_KEY, true);
            component.horizontalAnchor = getEnum(componentObject, HORIZONTAL_ANCHOR_KEY, HudComponent.HorizontalAnchor.class, component.horizontalAnchor);
            component.verticalAnchor = getEnum(componentObject, VERTICAL_ANCHOR_KEY, HudComponent.VerticalAnchor.class, component.verticalAnchor);
            component.anchorOffsetX = getFloat(componentObject, ANCHOR_OFFSET_X_KEY, x);
            component.anchorOffsetY = getFloat(componentObject, ANCHOR_OFFSET_Y_KEY, y);
            component.setPosition(x, y);

            JsonElement settingsElement = componentObject.get(SETTINGS_KEY);
            if (settingsElement != null && settingsElement.isJsonObject()) {
                JsonObject settingsObject = settingsElement.getAsJsonObject();
                for (BaseSetting<?> setting : component.getSettings()) {
                    JsonElement settingElement = settingsObject.get(setting.getName());
                    if (settingElement == null) {
                        continue;
                    }

                    try {
                        setting.fromJson(settingElement);
                    } catch (Exception e) {
                        Apex.LOGGER.warn("Failed to load HUD setting {} for {}", setting.getName(), name, e);
                    }
                }
            }

            Apex.hudRenderer.register(component);
        }
    }

    @Override
    protected JsonArray save() {
        final var components = new JsonArray();

        for (HudComponent component : Apex.hudRenderer.getComponents()) {
            if (!(component instanceof CustomHudComponent customComponent)) {
                Apex.LOGGER.warn("Skipping unsupported HUD component {}", component.name);
                continue;
            }

            final var componentObject = serializeComponentObject(component, customComponent);

            JsonObject settingsObject = new JsonObject();
            for (BaseSetting<?> setting : customComponent.getSettings()) {
                settingsObject.add(setting.getName(), setting.toJson());
            }
            componentObject.add(SETTINGS_KEY, settingsObject);

            components.add(componentObject);
        }

        return components;
    }

    private static @NonNull JsonObject serializeComponentObject(HudComponent it, CustomHudComponent custom) {
        final var componentObject = new JsonObject();
        componentObject.addProperty(TYPE_KEY, custom.getComponentType());
        componentObject.addProperty(NAME_KEY, it.name);
        componentObject.addProperty(ENABLED_KEY, it.enabled);
        componentObject.addProperty(X_KEY, it.position.x);
        componentObject.addProperty(Y_KEY, it.position.y);
        componentObject.addProperty(HORIZONTAL_ANCHOR_KEY, it.horizontalAnchor.toString());
        componentObject.addProperty(VERTICAL_ANCHOR_KEY, it.verticalAnchor.toString());
        componentObject.addProperty(ANCHOR_OFFSET_X_KEY, it.anchorOffsetX);
        componentObject.addProperty(ANCHOR_OFFSET_Y_KEY, it.anchorOffsetY);
        return componentObject;
    }

    private static CustomHudComponent createComponent(String type, String name, float x, float y) {
        return switch (type) {
            case "custom_text" -> new CustomTextHudComponent(name, name, x, y);
            case "custom_rectangle" -> new CustomRectangleHudComponent(name, x, y);
            case "custom_module_list" -> new CustomModuleListHudComponent(name, x, y);
            default -> null;
        };
    }

    private static String getString(JsonObject object, String key, String fallback) {
        JsonElement element = object.get(key);
        if (element != null && element.isJsonPrimitive()) {
            try {
                return element.getAsString();
            } catch (Exception ignored) {
            }
        }

        return fallback;
    }

    private static boolean getBoolean(JsonObject object, String key, boolean fallback) {
        JsonElement element = object.get(key);
        if (element != null && element.isJsonPrimitive()) {
            try {
                return element.getAsBoolean();
            } catch (Exception ignored) {
            }
        }

        return fallback;
    }

    private static float getFloat(JsonObject object, String key, float fallback) {
        JsonElement element = object.get(key);
        if (element != null && element.isJsonPrimitive()) {
            try {
                return element.getAsFloat();
            } catch (Exception ignored) {
            }
        }

        return fallback;
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> E getEnum(JsonObject object, String key, Class<E> enumType, E fallback) {
        JsonElement element = object.get(key);
        if (element != null && element.isJsonPrimitive()) {
            try {
                if (HudComponent.HorizontalAnchor.class.isAssignableFrom(enumType)) {
                    return (E) HudComponent.HorizontalAnchor.fromString(element.getAsString());
                } else if (HudComponent.VerticalAnchor.class.isAssignableFrom(enumType)) {
                    return (E) HudComponent.VerticalAnchor.fromString(element.getAsString());
                }

                return Enum.valueOf(enumType, element.getAsString().toUpperCase());
            } catch (Exception e) {
                Apex.LOGGER.error("Failed to parse {} - {} as enum!", key, element);
            }
        }

        return fallback;
    }

}
