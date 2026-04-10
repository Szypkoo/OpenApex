package lol.apex.feature.module.implementation.visual;

import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.feature.ui.imgui.ImGuiThemes;
import lol.apex.feature.ui.screen.ImGuiClickGui;
import lol.apex.feature.ui.screen.ImGuiDropdownMenu;
import org.lwjgl.glfw.GLFW;

@ModuleInfo(
        name = "ClickGUI",
        description = "Shows a list of modules and settings to configure.",
        category = Category.VISUAL
)
public class ClickGuiModule extends Module {
    public final EnumSetting<ImGuiThemes.Theme> imguiTheme = new EnumSetting<>("ImGui Theme", ImGuiThemes.Theme.DARK).hide(() -> true);
    public final EnumSetting<Mode> mode = new EnumSetting<Mode>("Mode", Mode.Menu);

    public enum Mode {
        Menu, Dropdown
    }

    private ImGuiClickGui imguiClickGui;
    private ImGuiDropdownMenu imGuiDropdownMenu;

    public ClickGuiModule() {
        setKey(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    @Override
    public void onEnable() {
        if (mode.getValue() == Mode.Menu) {
            if (imguiClickGui == null) {
                imguiClickGui = new ImGuiClickGui();
            }

            mc.setScreen(imguiClickGui);
        } else {
            if (imGuiDropdownMenu == null) {
                imGuiDropdownMenu = new ImGuiDropdownMenu();
            }

            mc.setScreen(imGuiDropdownMenu);
        }
    }
}