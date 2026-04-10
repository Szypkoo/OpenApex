package lol.apex.feature.module.implementation.visual;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.ColorSetting;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.manager.implementation.SoundManager;
import lombok.RequiredArgsConstructor;

import java.awt.*;

@ModuleInfo(
        name = "Interface",
        description = "Changes options about the client's interface.",
        category = Category.VISUAL
)
public class InterfaceModule extends Module {
    public final BoolSetting useToggleSounds = new BoolSetting("Toggle Sound", true);
    public final EnumSetting<ToggleSound> toggleSound = new EnumSetting<>("Toggle Sound", ToggleSound.SMOOTH);
    public final ColorSetting firstClientColor = new ColorSetting("First Client Color", new Color(0xFF4DA6FF));
    public final ColorSetting secondClientColor = new ColorSetting("Second Client Color", new Color(0xFF8A2BE2));

    public InterfaceModule() {
        enabledNoNoise(true);
    }

    @RequiredArgsConstructor
    public enum ToggleSound {
        SMOOTH("Smooth", enable -> {
            if(enable) {
                SoundManager.Sounds.SMOOTH_TOGGLE_ON.play(1.0F, 1.05F);

            } else {
                SoundManager.Sounds.SMOOTH_TOGGLE_OFF.play(1.0F, 0.95F);
            }
        }),
        KITTEN("Kitten", enable -> {
            if(enable) {
                SoundManager.Sounds.KITTEN_TOGGLE_ON.play();

            } else {
                SoundManager.Sounds.KITTEN_TOGGLE_OFF.play();
            }
        });

        private final String name;
        private final BooleanConsumer run;

        @Override
        public String toString() {
            return name;
        }

        public void run(boolean enable) {
            this.run.accept(enable);
        }
    }

}
