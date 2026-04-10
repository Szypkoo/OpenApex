package lol.apex.feature.module.implementation.visual;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.render.WorldFogColorEvent;
import lol.apex.event.render.WorldFogStrengthEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.ColorSetting;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lombok.RequiredArgsConstructor;

import java.awt.*;

@ModuleInfo(
        name = "Ambience",
        description = "Changes the way the minecraft world looks.",
        category = Category.VISUAL
)
public class AmbienceModule extends Module {
    public final BoolSetting changeFog = new BoolSetting("Change Fog", true);
    public final ColorSetting fogColor = new ColorSetting("Fog Color", Color.PINK).hide(()-> !changeFog.getValue());

    public final BoolSetting sky = new BoolSetting("Sky", true);
    public final EnumSetting<SkyType> skyType = new EnumSetting<SkyType>("Sky Type", SkyType.END).hide(()-> !sky.getValue());

    public final BoolSetting changeSkyColor = new BoolSetting("Change Sky Color", false).hide(()->!sky.getValue());
    public final BoolSetting skyToFogColor = new BoolSetting("Sky To Fog Color", false).hide(()->!changeSkyColor.getValue());
    public final BoolSetting staticColor = new BoolSetting("Static Color Sky", true).hide(()->!changeSkyColor.getValue());
    public final ColorSetting skyColorSetting = new ColorSetting("Sky Color", Color.PINK).hide(()->!staticColor.getValue());

    @RequiredArgsConstructor
    public enum SkyType {
        END("End"),
        OVERWORLD("Overworld"),
        NONE("None");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @EventHook
    public void onFogColor(WorldFogColorEvent event) {
        event.setColor(fogColor.getValue());
    }
}
