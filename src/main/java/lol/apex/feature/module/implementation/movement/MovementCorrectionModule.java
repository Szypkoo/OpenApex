package lol.apex.feature.module.implementation.movement;

import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lombok.RequiredArgsConstructor;

@ModuleInfo(
        name = "MovementCorrection",
        description = "Corrects your strafe movement.",
        category = Category.MOVEMENT
)
public class MovementCorrectionModule extends Module {
    public final EnumSetting<Mode> mode = new EnumSetting<>("MoveFix Mode", Mode.STRAFE);

    @RequiredArgsConstructor
    public enum Mode {
        STRAFE("Strafe"),
        SILENT("Silent");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }
}
