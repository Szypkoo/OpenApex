package lol.apex.feature.module.implementation.visual;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.render.Render2DEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.ui.imgui.ImGuiImpl;

import java.awt.*;

@ModuleInfo(
        name = "NoRender",
        description = "Cancels certain rendering events",
        category = Category.VISUAL
)
public class NoRenderModule extends Module {
    public final BoolSetting fire = new BoolSetting("Fire", true);
    public final BoolSetting fog = new BoolSetting("Fog", true);
    public final BoolSetting armor = new BoolSetting("Armor", false);
    public final BoolSetting hurtCamera = new BoolSetting("Hurt Camera", true);
    public final BoolSetting scoreboard = new BoolSetting("Scoreboard", true);
    public final BoolSetting activeEffects = new BoolSetting("Active Effects Box", true);

    @EventHook
    public void onRender2D(Render2DEvent event) {
        if(!fire.getValue()) return;
        if(!mc.player.isOnFire()) return;

        ImGuiImpl.render(wrapper -> {
            int centerX = event.getScaledWidth() / 2;
            int centerY = event.getScaledHeight() / 2;

            int textX = centerX;
            int textY = centerY;

            wrapper.drawString("product-regular", 25, "You are on fire!", textX, textY, Color.ORANGE);
        });
    }
}