package lol.apex.feature.ui.imgui;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDir;
import lol.apex.Apex;
import lol.apex.feature.module.implementation.visual.ClickGuiModule;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class ImGuiThemes {
    private static final String THEME_RESOURCE_ROOT = "/assets/apex/themes/";
    private static Theme lastAppliedTheme;

    public enum Theme {
        DARK("Dark", "dark.toml"),
        LIGHT("Light", "light.toml"),
        CLASSIC("Classic", "classic.toml"),
        CHERRY("Cherry", "cherry.toml"),
        CLEAN_DARK("Clean dark", "clean-dark.toml"),
        EVERFOREST("Everforest", "everforest.toml"),
        HALF_LIFE("Half-Life", "half-life.toml"),
        HAZY_DARK("Hazy Dark", "hazy-dark.toml"),
        PHOTOSHOP("Photoshop", "photoshop.toml"),
        ROUNDED_VS_STUDIO("Rounded VS Studio", "rounded-vs-studio.toml"),
        VS_STUDIO("VS Studio", "vs-studio.toml"),
        WINDARK("WinDark", "windark.toml");

        private final String name;
        private final String resourceName;

        Theme(String name, String resourceName) {
            this.name = name;
            this.resourceName = resourceName;
        }

        public String getResourceName() {
            return resourceName;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static void apply() {
        apply(Apex.moduleManager.getByClass(ClickGuiModule.class).imguiTheme.getValue());
    }

    public static void apply(Theme theme) {
        if (theme == null || theme == lastAppliedTheme) {
            return;
        }

        if (theme.getResourceName() != null) {
            applyResourceTheme(theme.getResourceName());
            lastAppliedTheme = theme;
        }
    }

    private static void applyResourceTheme(String resourceName) {
        var style = getVanillaStyle();
        var resourcePath = THEME_RESOURCE_ROOT + resourceName;

        try (var stream = ImGuiThemes.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                Apex.LOGGER.warn("Missing ImGui theme resource {}", resourcePath);
                return;
            }

            try (var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String section = "";
                String line;

                while ((line = reader.readLine()) != null) {
                    line = stripComments(line).trim();
                    if (line.isEmpty()) {
                        continue;
                    }

                    if (line.startsWith("[") && line.endsWith("]")) {
                        section = line.substring(1, line.length() - 1).trim().toLowerCase(Locale.ROOT);
                        continue;
                    }

                    int separatorIndex = line.indexOf('=');
                    if (separatorIndex < 0) {
                        continue;
                    }

                    var key = line.substring(0, separatorIndex).trim();
                    var value = line.substring(separatorIndex + 1).trim();

                    if ("colors".equals(section)) {
                        applyColor(style, key, value);
                    } else {
                        applyStyleValue(style, key, value);
                    }
                }
            }

        } catch (Exception e) {
            Apex.LOGGER.error("Failed to load ImGui theme from {}", resourcePath, e);
        }
    }

    private static String stripComments(String line) {
        int commentIndex = line.indexOf('#');
        return commentIndex >= 0 ? line.substring(0, commentIndex) : line;
    }

    private static void applyStyleValue(ImGuiStyle style, String key, String rawValue) {
        switch (key) {
            case "alpha" -> style.setAlpha(parseFloat(rawValue));
            case "disabledAlpha" -> style.setDisabledAlpha(parseFloat(rawValue));
            case "windowPadding" -> style.setWindowPadding(parseVec2(rawValue));
            case "windowRounding" -> style.setWindowRounding(parseFloat(rawValue));
            case "windowBorderSize" -> style.setWindowBorderSize(parseFloat(rawValue));
            case "windowMinSize" -> style.setWindowMinSize(parseVec2(rawValue));
            case "windowTitleAlign" -> style.setWindowTitleAlign(parseVec2(rawValue));
            case "windowMenuButtonPosition" -> style.setWindowMenuButtonPosition(parseDir(rawValue));
            case "childRounding" -> style.setChildRounding(parseFloat(rawValue));
            case "childBorderSize" -> style.setChildBorderSize(parseFloat(rawValue));
            case "popupRounding" -> style.setPopupRounding(parseFloat(rawValue));
            case "popupBorderSize" -> style.setPopupBorderSize(parseFloat(rawValue));
            case "framePadding" -> style.setFramePadding(parseVec2(rawValue));
            case "frameRounding" -> style.setFrameRounding(parseFloat(rawValue));
            case "frameBorderSize" -> style.setFrameBorderSize(parseFloat(rawValue));
            case "itemSpacing" -> style.setItemSpacing(parseVec2(rawValue));
            case "itemInnerSpacing" -> style.setItemInnerSpacing(parseVec2(rawValue));
            case "cellPadding" -> style.setCellPadding(parseVec2(rawValue));
            case "indentSpacing" -> style.setIndentSpacing(parseFloat(rawValue));
            case "columnsMinSpacing" -> style.setColumnsMinSpacing(parseFloat(rawValue));
            case "scrollbarSize" -> style.setScrollbarSize(parseFloat(rawValue));
            case "scrollbarRounding" -> style.setScrollbarRounding(parseFloat(rawValue));
            case "grabMinSize" -> style.setGrabMinSize(parseFloat(rawValue));
            case "grabRounding" -> style.setGrabRounding(parseFloat(rawValue));
            case "tabRounding" -> style.setTabRounding(parseFloat(rawValue));
            case "tabBorderSize" -> style.setTabBorderSize(parseFloat(rawValue));
            case "tabMinWidthForCloseButton" -> style.setTabMinWidthForCloseButton(parseFloat(rawValue));
            case "colorButtonPosition" -> style.setColorButtonPosition(parseDir(rawValue));
            case "buttonTextAlign" -> style.setButtonTextAlign(parseVec2(rawValue));
            case "selectableTextAlign" -> style.setSelectableTextAlign(parseVec2(rawValue));
            default -> Apex.LOGGER.warn("Unknown ImGui theme style key {}", key);
        }
    }

    private static void applyColor(ImGuiStyle style, String key, String rawValue) {
        int colorId = getImGuiColorId(key);
        float[] rgba = parseRgba(rawValue);
        style.setColor(colorId, rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    private static int getImGuiColorId(String key) {
        return switch (key) {
            case "Text" -> ImGuiCol.Text;
            case "TextDisabled" -> ImGuiCol.TextDisabled;
            case "WindowBg" -> ImGuiCol.WindowBg;
            case "ChildBg" -> ImGuiCol.ChildBg;
            case "PopupBg" -> ImGuiCol.PopupBg;
            case "Border" -> ImGuiCol.Border;
            case "BorderShadow" -> ImGuiCol.BorderShadow;
            case "FrameBg" -> ImGuiCol.FrameBg;
            case "FrameBgHovered" -> ImGuiCol.FrameBgHovered;
            case "FrameBgActive" -> ImGuiCol.FrameBgActive;
            case "TitleBg" -> ImGuiCol.TitleBg;
            case "TitleBgActive" -> ImGuiCol.TitleBgActive;
            case "TitleBgCollapsed" -> ImGuiCol.TitleBgCollapsed;
            case "MenuBarBg" -> ImGuiCol.MenuBarBg;
            case "ScrollbarBg" -> ImGuiCol.ScrollbarBg;
            case "ScrollbarGrab" -> ImGuiCol.ScrollbarGrab;
            case "ScrollbarGrabHovered" -> ImGuiCol.ScrollbarGrabHovered;
            case "ScrollbarGrabActive" -> ImGuiCol.ScrollbarGrabActive;
            case "CheckMark" -> ImGuiCol.CheckMark;
            case "SliderGrab" -> ImGuiCol.SliderGrab;
            case "SliderGrabActive" -> ImGuiCol.SliderGrabActive;
            case "Button" -> ImGuiCol.Button;
            case "ButtonHovered" -> ImGuiCol.ButtonHovered;
            case "ButtonActive" -> ImGuiCol.ButtonActive;
            case "Header" -> ImGuiCol.Header;
            case "HeaderHovered" -> ImGuiCol.HeaderHovered;
            case "HeaderActive" -> ImGuiCol.HeaderActive;
            case "Separator" -> ImGuiCol.Separator;
            case "SeparatorHovered" -> ImGuiCol.SeparatorHovered;
            case "SeparatorActive" -> ImGuiCol.SeparatorActive;
            case "ResizeGrip" -> ImGuiCol.ResizeGrip;
            case "ResizeGripHovered" -> ImGuiCol.ResizeGripHovered;
            case "ResizeGripActive" -> ImGuiCol.ResizeGripActive;
            case "Tab" -> ImGuiCol.Tab;
            case "TabHovered" -> ImGuiCol.TabHovered;
            case "TabActive" -> ImGuiCol.TabActive;
            case "TabUnfocused" -> ImGuiCol.TabUnfocused;
            case "TabUnfocusedActive" -> ImGuiCol.TabUnfocusedActive;
            case "PlotLines" -> ImGuiCol.PlotLines;
            case "PlotLinesHovered" -> ImGuiCol.PlotLinesHovered;
            case "PlotHistogram" -> ImGuiCol.PlotHistogram;
            case "PlotHistogramHovered" -> ImGuiCol.PlotHistogramHovered;
            case "TableHeaderBg" -> ImGuiCol.TableHeaderBg;
            case "TableBorderStrong" -> ImGuiCol.TableBorderStrong;
            case "TableBorderLight" -> ImGuiCol.TableBorderLight;
            case "TableRowBg" -> ImGuiCol.TableRowBg;
            case "TableRowBgAlt" -> ImGuiCol.TableRowBgAlt;
            case "TextSelectedBg" -> ImGuiCol.TextSelectedBg;
            case "DragDropTarget" -> ImGuiCol.DragDropTarget;
            case "NavHighlight" -> ImGuiCol.NavHighlight;
            case "NavWindowingHighlight" -> ImGuiCol.NavWindowingHighlight;
            case "NavWindowingDimBg" -> ImGuiCol.NavWindowingDimBg;
            case "ModalWindowDimBg" -> ImGuiCol.ModalWindowDimBg;
            default -> throw new IllegalArgumentException("Unknown ImGui color key: " + key);
        };
    }

    private static float parseFloat(String rawValue) {
        return Float.parseFloat(unquote(rawValue));
    }

    private static ImVec2 parseVec2(String rawValue) {
        String value = rawValue.trim();
        if (!value.startsWith("[") || !value.endsWith("]")) {
            throw new IllegalArgumentException("Expected vec2 array but got: " + rawValue);
        }

        String[] parts = value.substring(1, value.length() - 1).split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Expected vec2 array but got: " + rawValue);
        }

        return new ImVec2(
                Float.parseFloat(parts[0].trim()),
                Float.parseFloat(parts[1].trim())
        );
    }

    private static int parseDir(String rawValue) {
        String value = unquote(rawValue);
        return switch (value) {
            case "None" -> ImGuiDir.None;
            case "Left" -> ImGuiDir.Left;
            case "Right" -> ImGuiDir.Right;
            case "Up" -> ImGuiDir.Up;
            case "Down" -> ImGuiDir.Down;
            default -> throw new IllegalArgumentException("Unknown ImGui direction: " + value);
        };
    }

    private static float[] parseRgba(String rawValue) {
        String value = unquote(rawValue);
        if (!value.startsWith("rgba(") || !value.endsWith(")")) {
            throw new IllegalArgumentException("Expected rgba color but got: " + rawValue);
        }

        String[] parts = value.substring(5, value.length() - 1).split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Expected rgba color but got: " + rawValue);
        }

        return new float[]{
                Integer.parseInt(parts[0].trim()) / 255.0f,
                Integer.parseInt(parts[1].trim()) / 255.0f,
                Integer.parseInt(parts[2].trim()) / 255.0f,
                Float.parseFloat(parts[3].trim())
        };
    }

    private static String unquote(String value) {
        String trimmed = value.trim();
        if (trimmed.length() >= 2 && trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed;
    }

    private static ImGuiStyle getVanillaStyle() {
        var style = ImGui.getStyle();

        style.setAlpha(1.0f);
        style.setDisabledAlpha(0.6000000238418579f);
        style.setWindowPadding(new ImVec2(8.0f, 8.0f));
        style.setWindowRounding(0.0f);
        style.setWindowBorderSize(1.0f);
        style.setWindowMinSize(new ImVec2(32.0f, 32.0f));
        style.setWindowTitleAlign(new ImVec2(0.0f, 0.5f));
        style.setWindowMenuButtonPosition(ImGuiDir.Left);
        style.setChildRounding(0.0f);
        style.setChildBorderSize(1.0f);
        style.setPopupRounding(0.0f);
        style.setPopupBorderSize(1.0f);
        style.setFramePadding(new ImVec2(4.0f, 3.0f));
        style.setFrameRounding(0.0f);
        style.setFrameBorderSize(0.0f);
        style.setItemSpacing(new ImVec2(8.0f, 4.0f));
        style.setItemInnerSpacing(new ImVec2(4.0f, 4.0f));
        style.setCellPadding(new ImVec2(4.0f, 2.0f));
        style.setIndentSpacing(21.0f);
        style.setColumnsMinSpacing(6.0f);
        style.setScrollbarSize(14.0f);
        style.setScrollbarRounding(9.0f);
        style.setGrabMinSize(10.0f);
        style.setGrabRounding(0.0f);
        style.setTabRounding(4.0f);
        style.setTabBorderSize(0.0f);
        style.setTabMinWidthForCloseButton(0.0f);
        style.setColorButtonPosition(ImGuiDir.Right);
        style.setButtonTextAlign(new ImVec2(0.5f, 0.5f));
        style.setSelectableTextAlign(new ImVec2(0.0f, 0.0f));
        return style;
    }
}
