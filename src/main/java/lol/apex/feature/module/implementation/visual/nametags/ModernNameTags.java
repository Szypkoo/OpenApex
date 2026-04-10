package lol.apex.feature.module.implementation.visual.nametags;

import imgui.ImGui;
import lol.apex.Apex;
import lol.apex.event.render.Render2DEvent;
import lol.apex.feature.module.base.SubModuleWithParent;
import lol.apex.feature.module.implementation.visual.NametagsModule;
import lol.apex.feature.module.setting.implementation.ColorSetting;
import lol.apex.feature.ui.imgui.IImWrapper;
import lol.apex.feature.ui.imgui.ImGuiFonts;
import lol.apex.feature.ui.imgui.ImGuiImpl;
import lol.apex.util.skidded.ProjectionUtil;
import net.minecraft.client.render.Frustum;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.awt.*;

public class ModernNameTags extends SubModuleWithParent<NametagsModule> {

    public ModernNameTags(NametagsModule parent) {
        super(parent, "Modern");
    }

    private static Frustum frustum;

    public static void onRender2D(NametagsModule parent, Render2DEvent event) {
        if (mc.world == null) return;

        frustum = ProjectionUtil.createFrustum(event.getTicks().getTickProgress(true));
        if (frustum == null) return;

        float scale = mc.getWindow().getScaleFactor();

        ImGuiImpl.render(wrapper -> {
            ImGui.pushFont(ImGuiFonts.getFont("product-regular", (int) (20 * scale)));
            for (var plr : mc.world.getPlayers()) {
                nametaggedEntity2D(parent, wrapper, plr, event.getTicks().getTickProgress(false));
            }
            ImGui.popFont();
        });
    }

    private static void nametaggedEntity2D(NametagsModule parent, IImWrapper wrapper, PlayerEntity entity, float tickDelta) {
        if (!frustum.isVisible(entity.getBoundingBox())) return;

        var pos = ProjectionUtil.getEntityPositionsOn2D(wrapper, entity, tickDelta);
        float scale = mc.getWindow().getScaleFactor();

        float x = (float) pos.x * scale;
        float y = (float) pos.y * scale;
        float z = (float) pos.z  * scale;

        var nameText = entity.getName().getLiteralString() != null
                ? entity.getName().getLiteralString()
                : entity.getName().getString();

        if(entity == mc.player) return;

        float yOffset = y - 15 * scale;
        float width = ImGui.calcTextSizeX(nameText);
        float xOffset = (x + z / 2 - width / 2f);

        Color theColor;

        if(Apex.friendManager.isFriend(entity)) {
            theColor = parent.friendColor.getValue();
        } else {
            theColor = parent.defaultColor.getValue();
        }

        wrapper.drawBlur(xOffset - 2, yOffset - 2, width + 4, 20 * scale, 5);
        wrapper.drawRoundedRect(xOffset - 2, yOffset - 2, width + 4, 20 * scale + 4, 5, new Color(0, 0, 0, 100));
        wrapper.drawStringShadow("product-regular", (int) (20 * scale), nameText, xOffset, yOffset, theColor);
    }
}
