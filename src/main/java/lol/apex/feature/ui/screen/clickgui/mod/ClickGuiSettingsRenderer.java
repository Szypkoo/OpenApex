package lol.apex.feature.ui.screen.clickgui.mod;

import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiSliderFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.setting.base.BaseSetting;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.ColorSetting;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.feature.module.setting.implementation.ModeSetting;
import lol.apex.feature.module.setting.implementation.RangeSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.feature.module.setting.implementation.StringSetting;
import lol.apex.feature.module.setting.implementation.TextInputSetting;
import org.joml.Vector2f;

import java.util.Collection;
import java.util.Arrays;

public final class ClickGuiSettingsRenderer {
    public void render(Module module) {
        if (!module.isExpanded()) {
            return;
        }

        render(module.getBaseSettings());
    }

    public void render(Collection<BaseSetting<?>> settings) {
        for (var setting : settings) {
            if (setting.isHidden()) {
                continue;
            }

            ImGui.text(setting.getName());

            switch (setting) {
                case ModeSetting<?> modeSetting -> renderModeSetting(modeSetting);
                case SliderSetting sliderSetting -> renderSliderSetting(sliderSetting);
                case RangeSetting rangeSetting -> renderRangeSetting(rangeSetting);
                case BoolSetting boolSetting -> renderBoolSetting(boolSetting);
                case ColorSetting colorSetting -> renderColorSetting(colorSetting);
                case EnumSetting<?> enumSetting -> renderEnumSetting(enumSetting);
                case StringSetting stringSetting -> renderStringSetting(stringSetting);
                case TextInputSetting textInputSetting -> renderTextInputSetting(textInputSetting);
                default -> ImGui.text("Unimplemented: " + setting.getName());
            }
        }
    }

    private void renderModeSetting(ModeSetting<?> setting) {
        alignWideControl();
        var current = new ImInt(setting.getIndex());
        String[] values = setting.getValues().stream().map(mode -> mode.name).toArray(String[]::new);
        ImGui.combo("##" + setting.getName(), current, values);
        if (current.get() != setting.getIndex()) {
            setting.setValue(current.get());
        }
    }

    private void renderSliderSetting(SliderSetting setting) {
        alignWideControl();
        float current = setting.getValue();
        float[] newValue = new float[]{current};
        int decimals = (int) Math.ceil(-Math.log10(setting.getStep()));
        String format = "%." + Math.max(decimals, 0) + "f";

        ImGui.sliderFloat("##" + setting.getName(), newValue, setting.getMin(), setting.getMax(), format, ImGuiSliderFlags.NoInput);
        if (current != newValue[0]) {
            float step = setting.getStep();
            float snapped = Math.round(newValue[0] / step) * step;
            snapped = Math.clamp(snapped, setting.getMin(), setting.getMax());
            setting.setValue(snapped);
        }
    }

    private void renderRangeSetting(RangeSetting setting) {
        alignWideControl();
        Vector2f current = setting.getValue();
        float[] newValue = new float[]{current.x, current.y};
        int decimals = (int) Math.ceil(-Math.log10(setting.getStep()));
        String format = "%." + Math.max(decimals, 0) + "f";

        ImGui.sliderFloat2("##" + setting.getName(), newValue, setting.getMin(), setting.getMax(), format, ImGuiSliderFlags.NoInput);
        if (current.x != newValue[0] || current.y != newValue[1]) {
            setting.setValue(new Vector2f(newValue[0], newValue[1]));
        }
    }

    private void renderBoolSetting(BoolSetting setting) {
        var checked = new ImBoolean(setting.getValue());
        float checkboxWidth = ImGui.getFrameHeight();
        float checkboxX = ImGui.getCursorPosX() + ImGui.getContentRegionAvailX() - checkboxWidth;
        ImGui.sameLine(checkboxX);
        if (ImGui.checkbox("##" + setting.getName(), checked)) {
            setting.toggle();
        }
    }

    private void renderColorSetting(ColorSetting setting) {
        float colorWidth = ImGui.getFrameHeight();
        float colorX = ImGui.getCursorPosX() + ImGui.getContentRegionAvailX() - colorWidth;
        ImGui.sameLine(colorX);
        float[] color = setting.getValueAsArray1_0();
        int flags = ImGuiColorEditFlags.PickerHueWheel
                | ImGuiColorEditFlags.NoInputs
                | ImGuiColorEditFlags.AlphaBar
                | ImGuiColorEditFlags.NoDragDrop
                | ImGuiColorEditFlags.AlphaPreview;

        if (ImGui.colorEdit4("##" + setting.getName(), color, flags)) {
            setting.setValue(color);
        }
    }

    private void renderEnumSetting(EnumSetting<?> setting) {
        alignWideControl();
        var current = new ImInt(setting.getIndex());
        String[] values = Arrays.stream(setting.getValues()).map(Enum::toString).toArray(String[]::new);
        ImGui.combo("##" + setting.getName(), current, values);
        if (current.get() != setting.getIndex()) {
            setting.setValue(current.get());
        }
    }

    private void renderStringSetting(StringSetting setting) {
        alignWideControl();
        var current = new ImInt(setting.getIndex());
        String[] values = setting.getValues().toArray(String[]::new);
        ImGui.combo("##" + setting.getName(), current, values);
        if (current.get() != setting.getIndex()) {
            setting.setValue(current.get());
        }
    }

    private void renderTextInputSetting(TextInputSetting setting) {
        alignWideControl();
        var value = setting.asImString();
        if (ImGui.inputText("##" + setting.getName(), value)) {
            setting.setValue(value.get());
        }
        if (ImGui.isItemDeactivatedAfterEdit()) {
            setting.setValue(value.get());
        }
    }

    private void alignWideControl() {
        float controlWidth = ImGui.getContentRegionAvailX() * 0.6f;
        float controlX = ImGui.getCursorPosX() + ImGui.getContentRegionAvailX() - controlWidth;
        ImGui.sameLine(controlX);
        ImGui.setNextItemWidth(controlWidth);
    }
}
