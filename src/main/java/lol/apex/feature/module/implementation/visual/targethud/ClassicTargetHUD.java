package lol.apex.feature.module.implementation.visual.targethud;

import lol.apex.Apex;
import lol.apex.event.render.Render2DEvent;
import lol.apex.feature.module.base.SubModuleWithParent;
import lol.apex.feature.module.implementation.combat.AuraRecodeModule;
import lol.apex.feature.module.implementation.visual.TargetHUDModule;
import lol.apex.util.CommonUtil;
import lol.apex.util.render.RenderUtil;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class ClassicTargetHUD extends SubModuleWithParent<TargetHUDModule> {

    public ClassicTargetHUD(TargetHUDModule parent) {
        super(parent, "Default", "Default styling", "Original");
    }

    private static int posX = 10;
    private static int posY = 10;

    private static boolean dragging = false;
    private static int dragOffsetX;
    private static int dragOffsetY;

    public static void onRender2D(@NonNull TargetHUDModule parent, Render2DEvent event) {
        final var fr = mc.textRenderer;
        final var aura = Apex.moduleManager.getByClass(AuraRecodeModule.class);

        var target = aura.target;

        if (target == null && !(mc.currentScreen instanceof ChatScreen)) {
            return;
        }
        
        if (target == null) {
            target = mc.player;
        }

        DrawContext ctx = event.getContext();

        int width = 140;
        int height = 40;

        handleDragging(width, height);

        int x = posX;
        int y = posY;

        // background
        ctx.fill(x, y, x + width, y + height, Color.BLACK.getRGB());

        // border
        RenderUtil.drawBorder(ctx, x, y, width, height, CommonUtil.getFirstClientColor());

        // draw player head
        if (target instanceof AbstractClientPlayerEntity player) {

            Identifier skin = player.getSkin().body().texturePath();

            int size = 32;

            ctx.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    skin,
                    x + 4, y + 4,
                    8, 8,
                    size, size,
                    8, 8,
                    64, 64
            );

            ctx.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    skin,
                    x + 4, y + 4,
                    40, 8,
                    size, size,
                    8, 8,
                    64, 64
            );
        }

        // name
        String name = target.getName().getString();

        RenderUtil.animatedGradientText(
                ctx,
                name, x + 32 + 10,
                y + 5,
                CommonUtil.getFirstClientColor(),
                CommonUtil.getSecondClientColor()
        );

        // health
        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();
        float healthPerc = health / maxHealth;

        int barWidth = 90;
        int barX = x + 32 + 10;
        int barY = y + 20;

        ctx.fill(barX, barY, barX + barWidth, barY + 6, 0xFF2B2B2B);
        ctx.fill(barX, barY, barX + (int)(barWidth * healthPerc), barY + 6, 0xFF00FF55);

        // winning / losing
        boolean winning = mc.player.getHealth() > target.getHealth();
        String result = winning ? "Winning" : "Losing";

        if(parent.showWinning.getValue()) {
            ctx.drawText(fr, result, barX, barY + 10,
                    winning ? 0xFF00FF55 : 0xFFFF5555, false);
        }
    }

    private static void handleDragging(int width, int height) {

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

        boolean hovering =
                mx >= posX &&
                        mx <= posX + width &&
                        my >= posY &&
                        my <= posY + height;

        if (mouseDown && hovering && !dragging) {

            dragging = true;
            dragOffsetX = mx - posX;
            dragOffsetY = my - posY;
        }

        if (!mouseDown) {
            dragging = false;
        }

        if (dragging) {

            posX = mx - dragOffsetX;
            posY = my - dragOffsetY;
        }
    }
}