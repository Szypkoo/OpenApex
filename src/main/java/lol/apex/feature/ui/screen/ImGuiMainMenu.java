package lol.apex.feature.ui.screen;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import lol.apex.Apex;
import lol.apex.feature.ui.Icons;
import lol.apex.feature.ui.imgui.ImGuiScreen;
import lol.apex.feature.ui.imgui.ImWrapper;
import lol.apex.util.CommonUtil;
import lol.apex.util.render.RenderUtil;
import lol.apex.util.animation.api.AnimationUtil;
import lol.apex.util.animation.api.Easing;
import lol.apex.util.math.ColorUtil;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImGuiMainMenu extends ImGuiScreen {
    public static ImGuiMainMenu INSTANCE = new ImGuiMainMenu();

    private static final Identifier CAT = Identifier.of(Apex.MOD_ID, "icon.png");

    private Color firstColor = new Color(CommonUtil.getFirstClientColor());
    private Color secondColor = new Color(CommonUtil.getSecondClientColor());

    private final Map<String, AnimationUtil> buttonAnimations = new HashMap<>();

    public ImGuiMainMenu() {
        super(Text.empty());
    }

    @Override
    public void init() {
        super.init();
        firstColor = new Color(CommonUtil.getFirstClientColor());
        secondColor = new Color(CommonUtil.getSecondClientColor());
    }

    @Override
    public void renderScreen(ImGuiIO io) {
        var wrapper = new ImWrapper(ImGui.getForegroundDrawList());

        var size = io.getDisplaySize();

        float imguiMouseX = ImGui.getMousePosX();
        float imguiMouseY = ImGui.getMousePosY();

        var clr = ColorUtil.getTwoColorGradientMix(0, 0.3f, firstColor, secondColor);
        var clr2 = ColorUtil.getTwoColorGradientMix(1, 0.5f, secondColor, firstColor);
        var clr3 = ColorUtil.getTwoColorGradientMix(2, 0.25f, secondColor, firstColor);
        var clr4 = ColorUtil.getTwoColorGradientMix(3, 0.4f, firstColor, secondColor);
        wrapper.drawGradientRect(0, 0, size.x, size.y, clr.darker(), clr2, clr3.darker(), clr4);

        { // HEADER
            var text = Apex.getName().replace(" Beta", "");
            var headerSize = new ImVec2(350, 80);
            var x = size.x / 2 - headerSize.x / 2;
            var y = size.y / 2 - headerSize.y - 25f;

            wrapper.drawRoundedRect(x, y, headerSize.x, headerSize.y, 5, new Color(0, 0, 0, 150));
            //GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            //GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            wrapper.drawImageRounded(RenderUtil.getTextureId(CAT), x + 5, y - 3.5f, 80, 80, 0);
            wrapper.drawString("product-bold", (int) headerSize.y, text, x + 90, y - 5, Color.WHITE);
        }

        { // BUTTONS
            var offset = 10f;
            for (var buttonEntry : BUTTONS.entrySet()) {
                var buttonSize = new ImVec2(350, 30);
                var buttonPos = new ImVec2(size.x / 2 - buttonSize.x / 2, size.y / 2 - buttonSize.y + offset);

                var down = io.getMouseDown()[0];
                var over = isMouseOver(imguiMouseX, imguiMouseY, buttonPos, buttonSize);
                var color = over ? down ? Color.LIGHT_GRAY.darker() : Color.LIGHT_GRAY : Color.WHITE;

                var animation = buttonAnimations.computeIfAbsent(buttonEntry.getKey(), k -> new AnimationUtil(Easing.DECELERATE, 200));
                animation.run(over ? 10f : 0f);

                wrapper.drawRoundedRect(buttonPos.x, buttonPos.y, buttonSize.x, buttonSize.y, 5, new Color(0, 0, 0, 150));
                wrapper.drawString("product-regular", 20, buttonEntry.getKey(), buttonPos.x + 7 + animation.getValue(), buttonPos.y + 5, color);
                wrapper.drawString("icomoon", 20, buttonEntry.getValue(), buttonPos.x + buttonSize.x - 20 - 5, buttonPos.y + 5, color);

                offset += buttonSize.y + 5;
            }
        }

        { // FOOTER
            var offset = 10f + BUTTONS.size() * (30 + 5);

            var buttonSize = new ImVec2(350, 30);
            var buttonPos = new ImVec2(size.x / 2 - buttonSize.x / 2, size.y / 2 - buttonSize.y + offset);

            wrapper.drawRect(buttonPos.x, buttonPos.y, buttonSize.x, 2, new Color(255, 255, 255, 150));

            wrapper.drawRoundedRect(buttonPos.x, buttonPos.y + 5, buttonSize.x, buttonSize.y, 5, new Color(0, 0, 0, 150));
            wrapper.drawString("product-regular", 20, "You're running on build " + Apex.GITHUB_COMMIT, buttonPos.x + 7, buttonPos.y + 10, new Color(233, 233, 233));
            wrapper.drawString("icomoon", 20, Icons.COMMIT, buttonPos.x + buttonSize.x - 20 - 5, buttonPos.y + 10, new Color(233, 233, 233));
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        var io = ImGui.getIO();
        var size = io.getDisplaySize();

        float imguiMouseX = ImGui.getMousePosX();
        float imguiMouseY = ImGui.getMousePosY();

        { // BUTTONS
            var offset = 10f;
            for (var buttonEntry : BUTTONS.entrySet()) {
                var buttonSize = new ImVec2(350, 30);
                var buttonPos = new ImVec2(size.x / 2 - buttonSize.x / 2, size.y / 2 - buttonSize.y + offset);

                var over = isMouseOver(imguiMouseX, imguiMouseY, buttonPos, buttonSize);

                if (over && click.button() == 0) {
                    switch (buttonEntry.getKey()) {
                        case "Singleplayer" -> this.client.setScreen(new SelectWorldScreen(this));
                        case "Multiplayer" -> this.client.setScreen(new MultiplayerScreen(this));
                        case "Alt manager" -> this.client.setScreen(new AltScreen());
                        case "Settings" -> this.client.setScreen(new OptionsScreen(this, this.client.options));
                        case "Quit game" -> this.client.scheduleStop();
                    }
                }

                offset += buttonSize.y + 5;
            }
        }

        return super.mouseClicked(click, doubled);
    }

    private static final LinkedHashMap<String, String> BUTTONS = new LinkedHashMap<>();

    static {
        BUTTONS.put("Singleplayer", Icons.PLAYER);
        BUTTONS.put("Multiplayer", Icons.MULTIPLE_PEOPLE);
        BUTTONS.put("Alt manager", Icons.ACCOUNTS);
        BUTTONS.put("Settings", Icons.SETTINGS);
        BUTTONS.put("Quit game", Icons.LOGOUT);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
    }

    @Override
    public void blur() {
    }

    @Override
    protected void applyBlur(DrawContext context) {
    }

    private static boolean isMouseOver(float mouseX, float mouseY, ImVec2 pos, ImVec2 size) {
        return (pos.x <= mouseX && pos.x + size.x >= mouseX) && (pos.y <= mouseY && pos.y + size.y >= mouseY);
    }
}
