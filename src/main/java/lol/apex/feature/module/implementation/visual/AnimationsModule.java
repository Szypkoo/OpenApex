package lol.apex.feature.module.implementation.visual;

import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lombok.RequiredArgsConstructor;

@ModuleInfo(
        name = "Animations",
        description = "Gives swords an animation when blocking.",
        category = Category.VISUAL
)
public class AnimationsModule extends Module {
    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.VANILLA);
    public final BoolSetting useSwingSpeed = new BoolSetting("Use Swing Speed", false); 
    public final SliderSetting swingSpeed = new SliderSetting("Swing Speed", 1, 0, 100, 1).hide(()-> !useSwingSpeed.getValue());

    @RequiredArgsConstructor
    public enum Mode {
        VANILLA("Vanilla"),
        EXHIBITION("Exhibition");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }
}