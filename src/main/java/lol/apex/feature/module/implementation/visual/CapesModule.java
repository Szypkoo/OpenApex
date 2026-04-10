package lol.apex.feature.module.implementation.visual;

import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lombok.RequiredArgsConstructor;

@ModuleInfo(
        name = "Capes",
        description = "Custom Client Capes.",
        category = Category.VISUAL
)
public class CapesModule extends Module {
    public static String capeId;

    public EnumSetting<CapeType> mode = new EnumSetting<>("Cape", CapeType.APEX);

    @RequiredArgsConstructor
    public enum CapeType {

        APEX("Apex");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    public void onEnable() {
        switch(mode.getValue()) {
            case APEX -> capeId = "apex";
            default -> capeId = null;
        }
    }

    @Override
    public void onDisable() {
        capeId = null;
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }
}
