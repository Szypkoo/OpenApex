package lol.apex.feature.module.implementation.other;

import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;

@ModuleInfo(
        name = "AntiCheat",
        description = "Detects if other players are blatantly cheating.",
        category = Category.OTHER
)
public class AntiCheatModule extends Module {
    public final BoolSetting checkSelf = new BoolSetting("Check Self", false);
}
