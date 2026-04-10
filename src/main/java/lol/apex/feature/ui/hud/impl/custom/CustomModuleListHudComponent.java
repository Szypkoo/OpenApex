package lol.apex.feature.ui.hud.impl.custom;

import imgui.ImFont;
import lol.apex.Apex;
import lol.apex.event.render.Render2DEvent;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.setting.base.BaseSetting;
import lol.apex.feature.module.setting.implementation.*;
import lol.apex.feature.ui.imgui.IImWrapper;
import lol.apex.feature.ui.imgui.ImGuiFonts;
import lol.apex.feature.ui.imgui.ImGuiImpl;
import lol.apex.util.math.ColorUtil;
import lombok.RequiredArgsConstructor;
import org.joml.Vector4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CustomModuleListHudComponent extends CustomHudComponent {
    private static final String[] FONTS = {
            "google24-bold",
            "google24-light",
            "google24-medium",
            "google24-regular",
            "google24-semibold",
            "product-bold",
            "product-regular",
            "arimo-bold",
            "arimo-medium",
            "arimo-regular",
            "arimo-semibold",
            "ibm-bold",
            "ibm-medium",
            "ibm-regular",
            "ibm-semibold"
    };

    private static final float PADDING = 4.0f;

    private final BoolSetting suffixes;
    private final TextInputSetting suffixDivider;
    private final StringSetting suffixEncapsulation;
    private final BoolSetting textShadow;
    private final BoolSetting background;
    private final SliderSetting cornerRadius;
    private final BoolSetting blur;
    private final StringSetting font;
    private final SliderSetting fontSize;
    private final EnumSetting<ColorMode> colorMode;
    private final BoolSetting animatedGradient;
    private final EnumSetting<SortMode> sortMode;
    private final EnumSetting<ModuleNaming> moduleNaming;
    private final BoolSetting spacedOut;
    private final SliderSetting widthPadding;
    private final SliderSetting heightPadding;
    private final SliderSetting moduleSpacing;
    private final StringSetting suffixFont;
    private final SliderSetting suffixFontSize;
    private final SliderSetting gradientSpread;
    private final ColorSetting primaryColor;
    private final ColorSetting secondaryColor;
    private final ColorSetting suffixColor;
    private final ColorSetting backgroundColor;
    private final List<BaseSetting<?>> settings;

    public CustomModuleListHudComponent(String name, float x, float y) {
        super(name);
        this.position.x = x;
        this.position.y = y;

        this.suffixes = new BoolSetting("Suffixes", true);
        this.suffixDivider = new TextInputSetting("Suffix Divider", " - ", 8).hide(() -> !suffixes.getValue());
        this.suffixEncapsulation = new StringSetting("Suffix Encapsulation", "None", "[]", "()", "{}", "<>", "||").hide(() -> !suffixes.getValue());
        this.textShadow = new BoolSetting("Text Shadow", true);
        this.background = new BoolSetting("Background", true);
        this.cornerRadius = new SliderSetting("Corner Radius", 0, 0, 10, 1);
        this.blur = new BoolSetting("Blur", false).hide(() -> !background.getValue());
        this.font = new StringSetting("Font", FONTS);
        this.fontSize = new SliderSetting("Font Size", 24, 10, 72, 1);
        this.colorMode = new EnumSetting<>("Color Mode", ColorMode.VERTICAL);
        this.sortMode = new EnumSetting<>("Sort Mode", SortMode.LENGTH);
        this.moduleNaming = new EnumSetting<>("Module Naming", ModuleNaming.DEFAULT);
        this.widthPadding = new SliderSetting("Width Padding", 6.0F, 0.0F, 24.0F, 1.0F);
        this.spacedOut = new BoolSetting("Spaced Out", true);
        this.heightPadding = new SliderSetting("Height Padding", 3.0F, 0.0F, 24.0F, 1.0F);
        this.moduleSpacing = new SliderSetting("Module Spacing", 2.0F, 0.0F, 24.0F, 1.0F);
        this.suffixFont = new StringSetting("Suffix Font", FONTS).hide(() -> !suffixes.getValue());
        this.suffixFontSize = new SliderSetting("Suffix Font Size", 24, 10, 72, 1).hide(() -> !suffixes.getValue());
        this.animatedGradient = new BoolSetting("Animated Gradient", false).hide(() -> !colorMode.is(ColorMode.HORIZONTAL));
        this.gradientSpread = new SliderSetting("Gradient Spread", 0.3f, 0.0f, 3.0f, 0.1f).hide(() -> colorMode.getValue() == ColorMode.SOLID);
        this.primaryColor = new ColorSetting("Primary Color", new Color(255, 255, 255));
        this.secondaryColor = new ColorSetting("Secondary Color", new Color(120, 180, 255)).hide(() -> colorMode.getValue() == ColorMode.SOLID);
        this.suffixColor = new ColorSetting("Suffix Color", new Color(200, 200, 200)).hide(() -> !suffixes.getValue());
        this.backgroundColor = new ColorSetting("Background Color", new Color(0, 0, 0, 120)).hide(() -> !background.getValue());
        this.settings = List.of(
                suffixes,
                suffixDivider,
                suffixEncapsulation,
                textShadow,
                background,
                cornerRadius,
                blur,
                font,
                fontSize,
                colorMode,
                animatedGradient,
                sortMode,
                moduleNaming,
                spacedOut,
                widthPadding,
                heightPadding,
                moduleSpacing,
                suffixFont,
                suffixFontSize,
                gradientSpread,
                primaryColor,
                secondaryColor,
                suffixColor,
                backgroundColor
        );
    }

    @Override
    public String getComponentType() {
        return "custom_module_list";
    }

    @Override
    public List<BaseSetting<?>> getSettings() {
        return settings;
    }

    @Override
    public void renderToWrapper(IImWrapper wrapper) {
        String drawFont = font.getValue();
        int drawFontSize = fontSize.getValue().intValue();
        ImFont imFont = ImGuiFonts.getFont(drawFont, drawFontSize);
        int suffixFontSizeValue = suffixFontSize.getValue().intValue();
        ImFont suffixImFont = ImGuiFonts.getFont(suffixFont.getValue(), suffixFontSizeValue);
        List<ModuleEntry> modules = collectEntries(imFont, drawFontSize, suffixImFont, suffixFontSizeValue, false);

        if (modules.isEmpty()) {
            size.x = 0.0f;
            size.y = 0.0f;
            resolvePositionForCurrentScreen();
            return;
        }

        float widthPad = widthPadding.getValue();
        float heightPad = heightPadding.getValue();
        float spacing = moduleSpacing.getValue();
        float maxWidth = 0.0f;
        float totalHeight = 0.0f;

        for (ModuleEntry entry : modules) {
            float rectWidth = entry.totalWidth + widthPad * 2.0f;
            float rectHeight = entry.textHeight + heightPad * 2.0f;
            maxWidth = Math.max(maxWidth, rectWidth);
            totalHeight += rectHeight;
        }

        size.x = maxWidth + PADDING * 2.0f;
        size.y = totalHeight + Math.max(0, modules.size() - 1) * spacing + PADDING * 2.0f;
        resolvePositionForCurrentScreen();

        sortEntries(modules);

        boolean alignRight = isRightSide();

        float cursorY = position.y + PADDING;
        int index = 0;

        for (ModuleEntry entry : modules) {
            float rectWidth = entry.totalWidth + widthPad * 2.0f;
            float rectHeight = entry.textHeight + heightPad * 2.0f;
            float rectX = alignRight
                    ? position.x + size.x - PADDING - rectWidth
                    : position.x + PADDING;
            float rectY = cursorY;

            if (background.getValue()) {
                var rad = cornerRadius.getValue();
                Vector4f r = calculateRoundingRadius(index, modules, rad);
                r = mapCorners(r, alignRight);

                boolean roundUL = r.x > 0f;
                boolean roundUR = r.y > 0f;
                boolean roundBR = r.z > 0f;
                boolean roundBL = r.w > 0f;

                if (blur.getValue())
                    wrapper.drawSelectiveBlur(rectX, rectY, rectWidth, rectHeight, rad, roundUL, roundUR, roundBR, roundBL);

                if (rad > 0) {
                    wrapper.drawSelectiveRect(rectX, rectY, rectWidth, rectHeight, rad, roundUL, roundUR, roundBR, roundBL, backgroundColor.getValue());
                } else {
                    wrapper.drawRect(rectX, rectY, rectWidth, rectHeight, backgroundColor.getValue());
                }
            }

            float textX = rectX + widthPad;
            float textY = rectY + heightPad;
            Color lineColor = getLineColor(index);
            drawEntry(wrapper, font.getValue(), drawFontSize, suffixFont.getValue(), suffixFontSizeValue, entry, textX, textY, lineColor, index);

            cursorY += rectHeight + spacing;
            index++;
        }
    }

    @Override
    public void render(Render2DEvent event) {
        if (mc.options.hudHidden) {
            return;
        }

        ImGuiImpl.render(this::renderToWrapper);
    }

    private List<ModuleEntry> collectEntries(ImFont baseFont, int baseSize, ImFont suffixFont, int suffixSize, boolean sort) {
        List<ModuleEntry> entries = new ArrayList<>();

        for (Module module : Apex.moduleManager) {
            if (!module.enabled()) {
                continue;
            }

            String baseName = applyModuleNaming(module.getName());
            String suffixText = getFormattedSuffix(module);
            entries.add(new ModuleEntry(baseFont, baseSize, suffixFont, suffixSize, baseName, suffixText, suffixDivider.getValue()));
        }

        if (entries.isEmpty()) {
            return entries;
        }

        if (sort) {
            sortEntries(entries);
        }

        return entries;
    }

    private void sortEntries(List<ModuleEntry> entries) {
        Comparator<ModuleEntry> comparator = switch (sortMode.getValue()) {
            case ALPHABETICAL -> Comparator.comparing(entry -> entry.baseText.toLowerCase());
            case DEFAULT -> Comparator.comparingDouble(entry -> entry.baseWidth);
            case LENGTH -> Comparator.comparingDouble(entry -> entry.totalWidth);
        };

        if (sortMode.getValue() != SortMode.ALPHABETICAL) {
            comparator = comparator.reversed();
        }

        entries.sort(comparator);
        if (isBottomSide()) {
            Collections.reverse(entries);
        }
    }

    private void drawEntry(IImWrapper wrapper, String baseFont, int baseSize, String suffixFont, int suffixSize, ModuleEntry entry, float x, float y, Color lineColor, int index) {
        switch (colorMode.getValue()) {
            case SOLID, VERTICAL ->
                    wrapper.drawString(baseFont, baseSize, entry.baseText, x, y, lineColor, textShadow.getValue());
            case HORIZONTAL -> {
                //meh I dunno if this will look like the watermark gradient
                if (animatedGradient.getValue()) {
                    wrapper.drawAnimatedStringGradient(baseFont, baseSize, entry.baseText, x, y, primaryColor.getValue(), secondaryColor.getValue(), gradientSpread.getValue(), index, textShadow.getValue());
                } else {
                    wrapper.drawStringGradient(baseFont, baseSize, entry.baseText, x, y, primaryColor.getValue(), secondaryColor.getValue());
                }
            }
        }

        if (entry.suffixText == null) {
            return;
        }

        float dividerX = x + entry.baseWidth;
        Color dividerColor = suffixColor.getValue();

        wrapper.drawString(suffixFont, suffixSize, entry.dividerText, dividerX, y, dividerColor, textShadow.getValue());
        wrapper.drawString(suffixFont, suffixSize, entry.suffixText, dividerX + entry.dividerWidth, y, dividerColor, textShadow.getValue());
    }

    private Color getLineColor(int index) {
        return switch (colorMode.getValue()) {
            case SOLID, HORIZONTAL -> primaryColor.getValue();
            case VERTICAL ->
                    ColorUtil.getTwoColorGradient(index, gradientSpread.getValue(), primaryColor.getValue(), secondaryColor.getValue());
        };
    }

    private String applyModuleNaming(String text) {
        if (text == null) {
            return null;
        }

        String spaced = spacedOut.getValue() ? addSpacing(text) : text;

        return switch (moduleNaming.getValue()) {
            case LOWERCASE -> spaced.toLowerCase();
            case UPPERCASE -> spaced.toUpperCase();
            default -> spaced;
        };
    }

    private String addSpacing(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        return text.replaceAll("(?<=[a-z])(?=[A-Z])", " ");
    }

    private String getFormattedSuffix(Module module) {
        if (!suffixes.getValue()) {
            return null;
        }

        String rawSuffix = module.getSuffix();
        if (rawSuffix == null || rawSuffix.isBlank()) {
            return null;
        }

        String wrapper = suffixEncapsulation.getValue();
        String formatted = switch (wrapper) {
            case "[]" -> "[" + rawSuffix + "]";
            case "()" -> "(" + rawSuffix + ")";
            case "{}" -> "{" + rawSuffix + "}";
            case "<>" -> "<" + rawSuffix + ">";
            case "||" -> "|" + rawSuffix + "|";
            default -> rawSuffix;
        };
        return applyModuleNaming(formatted);
    }

    private boolean isRightSide() {
        return (position.x + size.x * 0.5f) >= mc.getWindow().getFramebufferWidth() * 0.5f;
    }

    private boolean isBottomSide() {
        return (position.y + size.y * 0.5f) >= mc.getWindow().getFramebufferHeight() * 0.5f;
    }

    private Vector4f mapCorners(Vector4f r, boolean right) {
        if (!right) {
            return r;
        }

        return new Vector4f(
                r.y,
                r.x,
                r.w,
                r.z
        );
    }

    private Vector4f calculateRoundingRadius(
            int index,
            List<ModuleEntry> modules,
            float radius
    ) {
        int size = modules.size();

        if (size == 0) {
            return new Vector4f(0f, 0f, 0f, 0f);
        }

        boolean first = index == 0;
        boolean last = index == size - 1;

        float tl = 0f;
        float tr = 0f;
        float br = 0f;
        float bl = 0f;

        ModuleEntry current = modules.get(index);

        ModuleEntry next = (index < size - 1) ? modules.get(index + 1) : null;

        boolean shouldMergeBottom =
                next != null &&
                        Math.abs(current.totalWidth - next.totalWidth) < 2.4f;

        if (first) {
            tl = radius;
            tr = radius;
        }

        if (last || !shouldMergeBottom) {
            bl = last ? radius : 0.0f;
            br = radius;
        }

        return new Vector4f(tl, tr, br, bl);
    }

    @RequiredArgsConstructor
    private enum ColorMode {
        SOLID("Solid"),
        HORIZONTAL("Horizontal"),
        VERTICAL("Vertical");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @RequiredArgsConstructor
    private enum SortMode {
        LENGTH("Length"),
        DEFAULT("Default"),
        ALPHABETICAL("Alphabetical");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @RequiredArgsConstructor
    private enum ModuleNaming {
        DEFAULT("Default"),
        LOWERCASE("Lowercase"),
        UPPERCASE("Uppercase");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    private static final class ModuleEntry {
        private final String baseText;
        private final String dividerText;
        private final String suffixText;
        private final float baseWidth;
        private final float dividerWidth;
        private final float suffixWidth;
        private final float totalWidth;
        private final float textHeight;

        private ModuleEntry(ImFont baseFont, int baseSize, ImFont suffixFont, int suffixSize, String baseText, String suffixText, String dividerText) {
            this.baseText = baseText;
            this.dividerText = suffixText == null ? "" : dividerText;
            this.suffixText = suffixText;
            this.baseWidth = baseFont.calcTextSizeAX(baseSize, Float.MAX_VALUE, 0.0f, baseText);
            this.dividerWidth = this.dividerText.isEmpty() ? 0.0f : suffixFont.calcTextSizeAX(suffixSize, Float.MAX_VALUE, 0.0f, this.dividerText);
            this.suffixWidth = suffixText == null ? 0.0f : suffixFont.calcTextSizeAX(suffixSize, Float.MAX_VALUE, 0.0f, suffixText);
            this.totalWidth = baseWidth + dividerWidth + suffixWidth;
            float baseHeight = baseFont.calcTextSizeA(baseSize, Float.MAX_VALUE, 0.0f, baseText).y;
            float suffixHeight = suffixText == null ? 0.0f : suffixFont.calcTextSizeA(suffixSize, Float.MAX_VALUE, 0.0f, suffixText).y;
            this.textHeight = Math.max(baseHeight, suffixHeight);
        }
    }
}
