package lol.apex.feature.module.implementation.visual.targethud;

import com.mojang.blaze3d.opengl.GlStateManager;
import lol.apex.Apex;
import lol.apex.event.render.Render2DEvent;
import lol.apex.feature.module.base.SubModuleWithParent;
import lol.apex.feature.module.implementation.combat.AuraRecodeModule;
import lol.apex.feature.module.implementation.visual.TargetHUDModule;
import lol.apex.feature.ui.imgui.ImGuiImpl;
import lol.apex.util.CommonUtil;
import lol.apex.util.render.RenderUtil;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.player.PlayerEntity;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;

// TODO: clickgui head
public class ModernTargetHUD extends SubModuleWithParent<TargetHUDModule> {

    public ModernTargetHUD(TargetHUDModule parent) {
        super(parent, "Default", "Default styling", "Original");
    }

    private static int posX = 50;
    private static int posY = 50;

    private static boolean dragging = false;
    private static int dragOffsetX;
    private static int dragOffsetY;

    public static void onRender2D(@NonNull TargetHUDModule parent, Render2DEvent event) {
        final var aura = Apex.moduleManager.getByClass(AuraRecodeModule.class);
        var target = aura.target;

        if (target == null && !(mc.currentScreen instanceof ChatScreen)) return;
        if (target == null) target = mc.player;

        boolean winLosingBool = parent.showWinning.getValue();
        net.minecraft.entity.LivingEntity finalTarget = target;

        if (!(finalTarget instanceof PlayerEntity)) {
            return;
        }

        handleDragging();

        ImGuiImpl.render(wrapper -> {
            float x = posX;
            float y = posY;

            float width = 240;
            float height = 90;

            wrapper.drawRoundedRect(x, y, width, height, 10f, new Color(0, 0, 0, 150));

            float headSize = 50;

            int texId = RenderUtil.getPlayerSkin((PlayerEntity) finalTarget);
            if (texId == -1) {
                wrapper.drawRoundedRect(x + 10, y + 20, headSize, headSize, 8f, new Color(40, 40, 40, 255));
            } else {
                GlStateManager._bindTexture(texId);
                GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                wrapper.drawImageRoundedWithUV(
                        texId,
                        x + 10, y + 20,
                        headSize, headSize,
                        8f / 64f, 8f / 64f,
                        16f / 64f, 16f / 64f,
                        8f
                );
            }

            wrapper.drawStringGradient(
                    "product-bold",
                    24,
                    finalTarget.getName().getString(),
                    x + 70,
                    y + 15,
                    new Color(CommonUtil.getFirstClientColor()),
                    new Color(CommonUtil.getSecondClientColor()),
                    true
            );

            float health = finalTarget.getHealth();
            float maxHealth = finalTarget.getMaxHealth();
            float hpPercent = Math.max(0, Math.min(1, health / maxHealth));

            float barX = x + 70;
            float barY = y + 45;
            float barWidth = 150;
            float barHeight = 10;

            wrapper.drawRoundedRect(barX, barY, barWidth, barHeight, 5f, new Color(30, 30, 30, 200));
            Color hpColor = new Color((int) (255 * (1 - hpPercent)), (int) (255 * hpPercent), 100);
            wrapper.drawRoundedRect(barX, barY, barWidth * hpPercent, barHeight, 5f, hpColor);

            wrapper.drawString(
                    "product-bold",
                    14,
                    String.format("%.1f / %.1f", health, maxHealth),
                    barX,
                    barY + 14,
                    new Color(200, 200, 200),
                    true
            );

            float playerHealth = mc.player.getHealth();
            boolean winning = playerHealth >= health;
            if (winLosingBool) {
                wrapper.drawString(
                        "product-bold",
                        14,
                        winning ? "Winning" : "Losing",
                        barX + 90,
                        barY + 14,
                        winning ? new Color(0, 255, 100) : new Color(255, 80, 80),
                        true
                );
            }
        });
    }

    private static void handleDragging() {
        if (!(mc.currentScreen instanceof ChatScreen)) {
            dragging = false;
            return;
        }

        long window = mc.getWindow().getHandle();

        double[] mouseX = new double[1];
        double[] mouseY = new double[1];

        GLFW.glfwGetCursorPos(window, mouseX, mouseY);

        int mx = (int) (mouseX[0] * mc.getWindow().getFramebufferWidth() / mc.getWindow().getWidth());
        int my = (int) (mouseY[0] * mc.getWindow().getFramebufferHeight() / mc.getWindow().getHeight());

        boolean mouseDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS;

        int width = 240;
        int height = 90;

        boolean hovering = mx >= posX && mx <= posX + width && my >= posY && my <= posY + height;

        if (mouseDown && hovering && !dragging) {
            dragging = true;
            dragOffsetX = mx - posX;
            dragOffsetY = my - posY;
        }

        if (!mouseDown) dragging = false;

        if (dragging) {
            posX = mx - dragOffsetX;
            posY = my - dragOffsetY;
        }
    }
}