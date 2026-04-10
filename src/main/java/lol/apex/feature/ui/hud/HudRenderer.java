package lol.apex.feature.ui.hud;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.Apex;
import lol.apex.event.render.Render2DEvent;
import lol.apex.feature.file.impl.HudFile;
import lol.apex.feature.ui.hud.impl.custom.CustomHudComponent;
import lol.apex.feature.ui.hud.impl.custom.CustomModuleListHudComponent;
import lol.apex.feature.ui.hud.impl.custom.CustomRectangleHudComponent;
import lol.apex.feature.ui.hud.impl.custom.CustomTextHudComponent;
import lol.apex.util.CommonVars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HudRenderer implements CommonVars {
    public static final float SNAP_THRESHOLD = 6.0f;
    private final List<HudComponent> components = new ArrayList<>();
    private int customTextCounter = 1;
    private int customRectangleCounter = 1;
    private int customModuleListCounter = 1;

    public record SnapGuide(float startX, float startY, float endX, float endY) {
    }

    public record SnapResult(float x, float y, HudComponent.HorizontalAnchor horizontalAnchor, HudComponent.VerticalAnchor verticalAnchor, List<SnapGuide> guides) {
    }

    public HudRenderer() {
        createDefaultElements();
    }

    public void initialize() {
        HudFile.DEFAULT.loadFromFile();
    }

    public void register(HudComponent comp) {
        components.add(comp);
    }

    public void setEnabled(Class<? extends HudComponent> componentType, boolean enabled) {
        components.stream()
                .filter(componentType::isInstance)
                .forEach(component -> component.enabled = enabled);
    }

    public CustomTextHudComponent createCustomTextComponent(float x, float y) {
        String name = nextCustomTextName();
        CustomTextHudComponent component = new CustomTextHudComponent(name, name, x, y);
        register(component);
        return component;
    }

    public CustomRectangleHudComponent createRectangleComponent(float x, float y) {
        String name = nextCustomRectangleName();
        CustomRectangleHudComponent component = new CustomRectangleHudComponent(name, x, y);
        register(component);
        return component;
    }

    public CustomModuleListHudComponent createModuleListComponent(float x, float y) {
        String name = nextCustomModuleListName();
        CustomModuleListHudComponent component = new CustomModuleListHudComponent(name, x, y);
        register(component);
        return component;
    }

    public void clear() {
        components.clear();
    }

    public List<HudComponent> getComponents() {
        return Collections.unmodifiableList(components);
    }

    private void createDefaultElements() {
        CustomTextHudComponent watermark = new CustomTextHudComponent("Watermark", Apex.getName() + " Client", 6.0f, 6.0f);
        watermark.setBackgroundEnabled(false);
        watermark.setFont("product-bold");
        watermark.setFontSize(40.0f);
        register(watermark);
    }

    public void remove(HudComponent component) {
        components.remove(component);
    }

    public SnapResult snapComponent(HudComponent movingComponent, float desiredX, float desiredY, float width, float height) {
        float snappedX = desiredX;
        float snappedY = desiredY;
        float bestXDistance = SNAP_THRESHOLD + 1.0f;
        float bestYDistance = SNAP_THRESHOLD + 1.0f;
        HudComponent.HorizontalAnchor snappedHorizontalAnchor = null;
        HudComponent.VerticalAnchor snappedVerticalAnchor = null;
        List<SnapGuide> guides = new ArrayList<>();
        int screenWidth = mc.getWindow().getFramebufferWidth();
        int screenHeight = mc.getWindow().getFramebufferHeight();

        for (HudComponent.HorizontalAnchor anchor : HudComponent.HorizontalAnchor.values()) {
            float anchorX = getAnchorX(anchor, screenWidth, width);
            float distance = Math.abs(desiredX - anchorX);
            if (distance <= SNAP_THRESHOLD && distance < bestXDistance) {
                bestXDistance = distance;
                snappedX = anchorX;
                snappedHorizontalAnchor = anchor;
            }
        }

        for (HudComponent.VerticalAnchor anchor : HudComponent.VerticalAnchor.values()) {
            float anchorY = getAnchorY(anchor, screenHeight, height);
            float distance = Math.abs(desiredY - anchorY);
            if (distance <= SNAP_THRESHOLD && distance < bestYDistance) {
                bestYDistance = distance;
                snappedY = anchorY;
                snappedVerticalAnchor = anchor;
            }
        }

        float[] movingXPoints = {desiredX, desiredX + width * 0.5f, desiredX + width};
        float[] movingYPoints = {desiredY, desiredY + height * 0.5f, desiredY + height};

        for (HudComponent component : components) {
            if (component == movingComponent || !component.enabled) {
                continue;
            }

            float otherWidth = Math.max(component.size.x, 0.0f);
            float otherHeight = Math.max(component.size.y, 0.0f);
            float[] otherXPoints = {component.position.x, component.position.x + otherWidth * 0.5f, component.position.x + otherWidth};
            float[] otherYPoints = {component.position.y, component.position.y + otherHeight * 0.5f, component.position.y + otherHeight};

            for (int movingIndex = 0; movingIndex < movingXPoints.length; movingIndex++) {
                for (float otherX : otherXPoints) {
                    float distance = Math.abs(movingXPoints[movingIndex] - otherX);
                    if (distance <= SNAP_THRESHOLD && distance < bestXDistance) {
                        bestXDistance = distance;
                        snappedX = desiredX + (otherX - movingXPoints[movingIndex]);
                        snappedHorizontalAnchor = null;
                    }
                }
            }

            for (int movingIndex = 0; movingIndex < movingYPoints.length; movingIndex++) {
                for (float otherY : otherYPoints) {
                    float distance = Math.abs(movingYPoints[movingIndex] - otherY);
                    if (distance <= SNAP_THRESHOLD && distance < bestYDistance) {
                        bestYDistance = distance;
                        snappedY = desiredY + (otherY - movingYPoints[movingIndex]);
                        snappedVerticalAnchor = null;
                    }
                }
            }
        }

        snappedX = clamp(snappedX, 0.0f, Math.max(0.0f, screenWidth - width));
        snappedY = clamp(snappedY, 0.0f, Math.max(0.0f, screenHeight - height));

        if (bestXDistance <= SNAP_THRESHOLD) {
            float snapCenterX = snappedX + width * 0.5f;
            guides.add(new SnapGuide(snapCenterX, 0.0f, snapCenterX, screenHeight));
        }

        if (bestYDistance <= SNAP_THRESHOLD) {
            float snapCenterY = snappedY + height * 0.5f;
            guides.add(new SnapGuide(0.0f, snapCenterY, screenWidth, snapCenterY));
        }

        return new SnapResult(snappedX, snappedY, snappedHorizontalAnchor, snappedVerticalAnchor, guides);
    }

    public void resolveComponentLayout(HudComponent component) {
        component.resolvePositionForCurrentScreen();
    }

    public List<SnapGuide> getScreenBoundsGuides() {
        int screenWidth = mc.getWindow().getFramebufferWidth();
        int screenHeight = mc.getWindow().getFramebufferHeight();
        List<SnapGuide> guides = new ArrayList<>();
        guides.add(new SnapGuide(screenWidth / 3.0f, 0.0f, screenWidth / 3.0f, screenHeight));
        guides.add(new SnapGuide(screenWidth * (2.0f / 3.0f), 0.0f, screenWidth * (2.0f / 3.0f), screenHeight));
        guides.add(new SnapGuide(0.0f, screenHeight / 3.0f, screenWidth, screenHeight / 3.0f));
        guides.add(new SnapGuide(0.0f, screenHeight * (2.0f / 3.0f), screenWidth, screenHeight * (2.0f / 3.0f)));
        guides.add(new SnapGuide(screenWidth * 0.5f, 0.0f, screenWidth * 0.5f, screenHeight));
        guides.add(new SnapGuide(0.0f, screenHeight * 0.5f, screenWidth, screenHeight * 0.5f));
        return guides;
    }

    private float getAnchorX(HudComponent.HorizontalAnchor anchor, int screenWidth, float width) {
        return switch (anchor) {
            case LEFT -> 0.0f;
            case CENTER -> (screenWidth - width) * 0.5f;
            case RIGHT -> screenWidth - width;
        };
    }

    private float getAnchorY(HudComponent.VerticalAnchor anchor, int screenHeight, float height) {
        return switch (anchor) {
            case TOP -> 0.0f;
            case MIDDLE -> (screenHeight - height) * 0.5f;
            case BOTTOM -> screenHeight - height;
        };
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public List<CustomHudComponent> getCustomComponents() {
        return components.stream()
                .filter(CustomHudComponent.class::isInstance)
                .map(CustomHudComponent.class::cast)
                .toList();
    }

    public boolean moveCustomComponent(int fromIndex, int toIndex) {
        List<CustomHudComponent> customComponents = getCustomComponents();
        if (fromIndex < 0 || fromIndex >= customComponents.size() || toIndex < 0 || toIndex >= customComponents.size() || fromIndex == toIndex) {
            return false;
        }

        CustomHudComponent movedComponent = customComponents.get(fromIndex);
        int movedIndex = components.indexOf(movedComponent);
        if (movedIndex < 0) {
            return false;
        }

        components.remove(movedIndex);
        List<CustomHudComponent> reorderedCustomComponents = getCustomComponents();
        if (toIndex > reorderedCustomComponents.size()) {
            return false;
        }

        if (toIndex == reorderedCustomComponents.size()) {
            components.add(movedComponent);
            return true;
        }

        CustomHudComponent targetComponent = reorderedCustomComponents.get(toIndex);
        int targetIndex = components.indexOf(targetComponent);
        if (targetIndex < 0) {
            return false;
        }

        components.add(targetIndex, movedComponent);
        return true;
    }

    private String nextCustomTextName() {
        String name;

        do {
            name = "Custom Text " + customTextCounter++;
        } while (hasComponentNamed(name));

        return name;
    }

    private String nextCustomRectangleName() {
        String name;

        do {
            name = "Rectangle " + customRectangleCounter++;
        } while (hasComponentNamed(name));

        return name;
    }

    private String nextCustomModuleListName() {
        String name;

        do {
            name = "Module List " + customModuleListCounter++;
        } while (hasComponentNamed(name));

        return name;
    }

    private boolean hasComponentNamed(String name) {
        for (HudComponent component : components) {
            if (component.name.equals(name)) {
                return true;
            }
        }

        return false;
    }

    @EventHook
    public void onRender2D(Render2DEvent event) {
        components.stream()
                .filter(component -> component.enabled)
                .forEach(component -> {
                    resolveComponentLayout(component);
                    component.render(event);
                    component.updateAnchorOffsetsFromPosition();
                });
    }
}
