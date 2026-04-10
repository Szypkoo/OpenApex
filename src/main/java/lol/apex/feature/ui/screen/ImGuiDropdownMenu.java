package lol.apex.feature.ui.screen;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.*;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import lol.apex.Apex;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.setting.base.BaseSetting;
import lol.apex.feature.module.setting.implementation.*;
import lol.apex.feature.ui.imgui.ImGuiScreen;
import net.minecraft.text.Text;
import lol.apex.feature.module.base.Module;
import org.joml.Vector2f;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
this looks just so ugly i kinda wish we had a dropdown like packet or something lol
since our visuals kinda look like bedrock client visuals
*/

public class ImGuiDropdownMenu extends ImGuiScreen {

    private final Map<Category, float[]> catPositions = new HashMap<>();
    private Category dragging = null;
    private float dragOffsetX = 0, dragOffsetY = 0;
    private Module selectedModule = null; // tracks the selected module for settings

    public ImGuiDropdownMenu() {
        super(Text.of("a"));
        int i = 0;
        for (Category category : Category.values()) {
            catPositions.put(category, new float[]{10 + i * 200, 10});
            i++;
        }
    }

    @Override
    public void renderScreen(ImGuiIO io) {
        int windowFlags = ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.AlwaysAutoResize |
                ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoDocking;

        for (Category category : Category.values()) {
            float[] pos = catPositions.get(category);

            ImGui.setNextWindowPos(pos[0], pos[1], ImGuiCond.Always);
            ImGui.setNextWindowSize(190, 0);

            if (ImGui.begin("##cat_" + category, windowFlags)) {
                ImGui.setNextItemOpen(true, ImGuiCond.Once);

                if (ImGui.collapsingHeader(category.toString())) {
                    ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 5);

                    for (Module module : Apex.moduleManager.getModulesByCategory(category)) {
                        boolean hasSettings = !module.getBaseSettings().isEmpty();
                        boolean enabled = module.isEnabled();

                        ImGui.text(module.getName());
                        ImGui.sameLine(163);

                        ImBoolean moduleValue = new ImBoolean(enabled);
                        if (ImGui.checkbox("##" + module.getName(), moduleValue)) {
                            module.toggle();
                        }

                        if (hasSettings && ImGui.isItemClicked(ImGuiMouseButton.Right)) {
                            selectedModule = selectedModule == module ? null : module;
                        }

                        ImGui.spacing();
                    }

                    ImGui.popStyleVar();
                }

                // dragging
                float winX = ImGui.getWindowPosX();
                float winY = ImGui.getWindowPosY();
                float winW = ImGui.getWindowSizeX();
                float mouseX = io.getMousePosX();
                float mouseY = io.getMousePosY();
                boolean mouseOverHeader = mouseX >= winX && mouseX <= winX + winW && mouseY >= winY && mouseY <= winY + 24;

                if (ImGui.isMouseDown(ImGuiMouseButton.Left)) {
                    if (mouseOverHeader && dragging == null) {
                        dragging = category;
                        dragOffsetX = mouseX - pos[0];
                        dragOffsetY = mouseY - pos[1];
                    }
                } else {
                    if (dragging == category) dragging = null;
                }

                if (dragging == category) {
                    pos[0] = mouseX - dragOffsetX;
                    pos[1] = mouseY - dragOffsetY;
                    catPositions.put(category, pos);
                }
            }

            ImGui.end();
        }

        drawSettingsWindow();
    }

    private void drawSettings(Module module) {
        for (BaseSetting<?> setting : module.getBaseSettings()) {
            if (setting.isHidden()) continue;

            ImGui.text(setting.getName());

            switch (setting) {
                case BoolSetting boolSetting -> {
                    ImBoolean value = new ImBoolean(boolSetting.getValue());
                    ImGui.sameLine(150);
                    if (ImGui.checkbox("##" + setting.getName(), value)) {
                        boolSetting.setValue(value.get());
                    }
                }

                case SliderSetting sliderSetting -> {
                    float[] value = {sliderSetting.getValue()};
                    if (ImGui.sliderFloat("##" + setting.getName(), value, sliderSetting.getMin(), sliderSetting.getMax())) {
                        sliderSetting.setValue(value[0]);
                    }
                }

                case RangeSetting rangeSetting -> {
                    float[] val = {rangeSetting.getValue().x, rangeSetting.getValue().y};
                    if (ImGui.dragFloat2("##" + setting.getName(), val, rangeSetting.getStep(), rangeSetting.getMin(), rangeSetting.getMax())) {
                        rangeSetting.setValue(new Vector2f(val[0], val[1]));
                    }
                }

                case StringSetting stringSetting -> {
                    ImInt current = new ImInt(stringSetting.getIndex());
                    String[] options = stringSetting.getValues().toArray(new String[0]);
                    if (ImGui.combo("##" + setting.getName(), current, options)) {
                        stringSetting.setValue(current.get());
                    }
                }

                case EnumSetting<?> enumSetting -> {
                    ImInt current = new ImInt(enumSetting.getIndex());
                    String[] options = Arrays.stream(enumSetting.getValues()).map(Enum::toString).toArray(String[]::new);
                    if (ImGui.combo("##" + setting.getName(), current, options)) {
                        enumSetting.setValue(current.get());
                    }
                }

                case ModeSetting<?> modeSetting -> {
                    ImInt current = new ImInt(modeSetting.getIndex());
                    String[] options = modeSetting.getValues().stream().map(m -> m.name).toArray(String[]::new);
                    if (ImGui.combo("##" + setting.getName(), current, options)) {
                        modeSetting.setValue(current.get());
                    }
                }

                case ColorSetting colorSetting -> {
                    float[] value = colorSetting.getValueAsArray1_0();
                    if (ImGui.colorEdit4("##" + setting.getName(), value)) {
                        colorSetting.setValue(value);
                    }

                    ImGui.text("Rainbow:");
                    ImBoolean rainbow = new ImBoolean(colorSetting.isRainbow());
                    if (ImGui.checkbox("##rainbow_" + setting.getName(), rainbow)) {
                        colorSetting.setRainbow(!rainbow.get());
                    }
                }

                case TextInputSetting textInputSetting -> {
                    ImString imString = textInputSetting.asImString();
                    if (ImGui.inputText("##" + setting.getName(), imString)) {
                        textInputSetting.setValue(imString.get());
                    }
                }

                default -> ImGui.text("Unimplemented setting " + setting.getName());
            }

            ImGui.spacing();
        }
    }

    private void drawSettingsWindow() {
        if (selectedModule != null) {
            ImGui.setNextWindowSize(250, 0, ImGuiCond.Once);
            ImBoolean open = new ImBoolean(true);

            if (ImGui.begin(selectedModule.getName() + " Settings", open, ImGuiWindowFlags.AlwaysAutoResize)) {
                drawSettings(selectedModule);
            }

            ImGui.end();

            if (!open.get()) selectedModule = null;
        }
    }
}