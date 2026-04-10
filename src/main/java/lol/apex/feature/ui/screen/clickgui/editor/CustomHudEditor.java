package lol.apex.feature.ui.screen.clickgui.editor;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImDrawFlags;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import lol.apex.Apex;
import lol.apex.feature.ui.Icons;
import lol.apex.feature.ui.hud.HudRenderer;
import lol.apex.feature.ui.hud.impl.custom.CustomHudComponent;
import lol.apex.feature.ui.imgui.ImGuiFonts;
import lol.apex.feature.ui.imgui.ImWrapper;
import lol.apex.feature.ui.screen.clickgui.mod.ClickGuiSettingsRenderer;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public final class CustomHudEditor {
    private static final float WINDOW_WIDTH = 720.0f;
    private static final float WINDOW_HEIGHT = 460.0f;

    private final ClickGuiSettingsRenderer settingsRenderer = new ClickGuiSettingsRenderer();
    private boolean open;
    private int selectedIndex = -1;
    private CustomHudComponent draggingComponent;
    private CustomHudComponent reorderDraggingComponent;
    private CustomHudComponent reorderLastTargetComponent;
    private boolean reorderLastInsertBefore;
    private float dragOffsetX;
    private float dragOffsetY;
    private float editorWindowX;
    private float editorWindowY;
    private float editorWindowWidth = WINDOW_WIDTH;
    private float editorWindowHeight = WINDOW_HEIGHT;
    private List<HudRenderer.SnapGuide> activeSnapGuides = List.of();

    public void open() {
        open = true;
        if (selectedIndex < 0 && !Apex.hudRenderer.getCustomComponents().isEmpty()) {
            selectedIndex = 0;
        }
    }

    public void toggle() {
        open = !open;
        if (open && selectedIndex < 0 && !Apex.hudRenderer.getCustomComponents().isEmpty()) {
            selectedIndex = 0;
        }
    }

    public boolean render(ImGuiIO io) {
        if (!open) {
            draggingComponent = null;
            clearReorderState();
            activeSnapGuides = List.of();
            return false;
        }

        renderDragOverlay(io);
        renderEditorWindow(io);
        return open;
    }

    private void renderEditorWindow(ImGuiIO io) {
        ImGui.setNextWindowSize(WINDOW_WIDTH, WINDOW_HEIGHT, ImGuiCond.Appearing);
        ImGui.setNextWindowPos(
                (io.getDisplaySizeX() - WINDOW_WIDTH) * 0.5f,
                (io.getDisplaySizeY() - WINDOW_HEIGHT) * 0.5f,
                ImGuiCond.Appearing
        );

        ImBoolean windowOpen = new ImBoolean(open);
        int flags = ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize;
        if (ImGui.begin("Custom HUD Editor", windowOpen, flags)) {
            editorWindowX = ImGui.getWindowPosX();
            editorWindowY = ImGui.getWindowPosY();
            editorWindowWidth = ImGui.getWindowSizeX();
            editorWindowHeight = ImGui.getWindowSizeY();

            float actionButtonSize = ImGui.getFrameHeight() + 6.0f;
            if (renderActionButton(Icons.TEXTLABEL, actionButtonSize, "Add text element")) {
                float x = 5.0f;
                float y = 5.0f;
                Apex.hudRenderer.createCustomTextComponent(x, y);
                selectedIndex = Apex.hudRenderer.getCustomComponents().size() - 1;
            }

            ImGui.sameLine();

            if (renderActionButton(Icons.RECTANGLE, actionButtonSize, "Add rectangle element")) {
                float x = 5.0f;
                float y = 5.0f;
                Apex.hudRenderer.createRectangleComponent(x, y);
                selectedIndex = Apex.hudRenderer.getCustomComponents().size() - 1;
            }

            ImGui.sameLine();

            if (renderActionButton(Icons.MODULELIST, actionButtonSize, "Add module list element")) {
                float x = 5.0f;
                float y = 5.0f;
                Apex.hudRenderer.createModuleListComponent(x, y);
                selectedIndex = Apex.hudRenderer.getCustomComponents().size() - 1;
            }

            ImGui.separator();

            ImGui.beginChild("CustomHudElements", 220, 0, true);
            renderElementList();
            ImGui.endChild();

            ImGui.sameLine();

            ImGui.beginChild("CustomHudSettings", 0, 0, true);
            renderSelectedElementSettings();
            ImGui.endChild();
        }
        ImGui.end();
        open = windowOpen.get() && open;
    }

    private void renderElementList() {
        List<CustomHudComponent> elements = Apex.hudRenderer.getCustomComponents();
        if (elements.isEmpty()) {
            ImGui.text("No custom elements yet.");
            wrappedDescription("Press one of the add buttons to create an element.");
            return;
        }

        float rowInset = 4.0f;
        float buttonHeight = ImGui.getFrameHeight() + 6.0f;
        boolean reorderedThisFrame = false;

        for (int i = 0; i < elements.size(); i++) {
            CustomHudComponent component = elements.get(i);
            String label = component.name + "##" + i;
            ImGui.pushID(i);
            float buttonSize = buttonHeight;
            float spacing = ImGui.getStyle().getItemSpacingX();
            float startX = ImGui.getCursorPosX() + rowInset;
            ImGui.setCursorPosX(startX);
            float rowWidth = Math.max(1.0f, ImGui.getContentRegionAvailX() - rowInset);
            float dragHandleWidth = buttonSize;
            float deleteButtonWidth = buttonSize + 2.0f;
            float textButtonWidth = Math.max(1.0f, rowWidth - dragHandleWidth - deleteButtonWidth - spacing * 2.0f);

            if (renderDragHandleButton(buttonSize, "Drag to reorder")) {
                selectedIndex = i;
            }
            if (ImGui.isItemActivated()) {
                reorderDraggingComponent = component;
                reorderLastTargetComponent = null;
                selectedIndex = i;
            }

            ImGui.sameLine();
            if (renderElementButton(label, selectedIndex == i, textButtonWidth, buttonHeight)) {
                selectedIndex = i;
            }

            if (!reorderedThisFrame) {
                reorderedThisFrame = handleElementReorder(component);
                if (reorderedThisFrame) {
                    ImGui.popID();
                    break;
                }
            }

            ImGui.sameLine();
            if (renderActionButton(Icons.TRASH, buttonSize, "Delete element")) {
                Apex.hudRenderer.remove(component);
                if (selectedIndex == i) {
                    selectedIndex = -1;
                } else if (selectedIndex > i) {
                    selectedIndex--;
                }
                ImGui.popID();
                break;
            }
            ImGui.popID();
        }
    }

    private void renderSelectedElementSettings() {
        List<CustomHudComponent> elements = Apex.hudRenderer.getCustomComponents();
        if (selectedIndex < 0 || selectedIndex >= elements.size()) {
            ImGui.text("Select an element to edit it.");
            wrappedDescription("You can also drag selected elements directly on the screen.");
            return;
        }

        CustomHudComponent selected = elements.get(selectedIndex);
        ImGui.text(selected.name);
        wrappedDescription("Drag the highlighted box on the screen to move it.");
        ImGui.separator();
        settingsRenderer.render(selected.getSettings());
    }

    private void renderDragOverlay(ImGuiIO io) {
        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        ImGui.setNextWindowSize(io.getDisplaySizeX(), io.getDisplaySizeY(), ImGuiCond.Always);

        int flags = ImGuiWindowFlags.NoDecoration
                | ImGuiWindowFlags.NoBackground
                | ImGuiWindowFlags.NoSavedSettings
                | ImGuiWindowFlags.NoMove
                | ImGuiWindowFlags.NoScrollbar
                | ImGuiWindowFlags.NoScrollWithMouse
                | ImGuiWindowFlags.NoInputs;

        if (ImGui.begin("##custom-hud-overlay", flags)) {
            ImDrawList drawList = ImGui.getWindowDrawList();
            ImWrapper wrapper = new ImWrapper(drawList);
            List<CustomHudComponent> elements = Apex.hudRenderer.getCustomComponents();
            float mouseX = ImGui.getMousePosX();
            float mouseY = ImGui.getMousePosY();
            boolean mouseOverEditor = isInEditorWindow(mouseX, mouseY);
            renderBoundsGuides(drawList, Apex.hudRenderer.getScreenBoundsGuides(), 0x22FFFFFF, 1.0f);

            for (int i = 0; i < elements.size(); i++) {
                CustomHudComponent component = elements.get(i);
                Apex.hudRenderer.resolveComponentLayout(component);
                component.renderToWrapper(wrapper);
                float width = Math.max(component.size.x, 40.0f);
                float height = Math.max(component.size.y, 18.0f);
                float minX = component.position.x - 4;
                float minY = component.position.y - 4;
                float maxX = component.position.x + width + 4;
                float maxY = component.position.y + height + 4;
                boolean hovered = !mouseOverEditor && mouseX >= minX && mouseX <= maxX && mouseY >= minY && mouseY <= maxY;
                boolean selected = selectedIndex == i;

                if (hovered && ImGui.isMouseClicked(0)) {
                    selectedIndex = i;
                    draggingComponent = component;
                    dragOffsetX = mouseX - component.position.x;
                    dragOffsetY = mouseY - component.position.y;
                }

                if (draggingComponent == component) {
                    if (ImGui.isMouseDown(0)) {
                        if (io.getKeyShift()) {
                            component.setPosition(mouseX - dragOffsetX, mouseY - dragOffsetY);
                            activeSnapGuides = List.of();
                        } else {
                            HudRenderer.SnapResult snapResult = Apex.hudRenderer.snapComponent(
                                    component,
                                    mouseX - dragOffsetX,
                                    mouseY - dragOffsetY,
                                    width,
                                    height
                            );
                            component.setPosition(snapResult.x(), snapResult.y());
                            activeSnapGuides = snapResult.guides();
                            if (snapResult.horizontalAnchor() != null || snapResult.verticalAnchor() != null) {
                                component.setAnchors(
                                        snapResult.horizontalAnchor() != null ? snapResult.horizontalAnchor() : component.horizontalAnchor,
                                        snapResult.verticalAnchor() != null ? snapResult.verticalAnchor() : component.verticalAnchor
                                );
                            }
                        }
                    } else {
                        MinecraftClient client = MinecraftClient.getInstance();
                        component.chooseClosestAnchorAndUpdateOffsets(
                                client.getWindow().getFramebufferWidth(),
                                client.getWindow().getFramebufferHeight()
                        );
                        draggingComponent = null;
                        activeSnapGuides = List.of();
                    }
                }

                int borderColor = selected ? 0xFF4DA6FF : (hovered ? 0xCCFFFFFF : 0x66FFFFFF);
                drawList.addRect(
                        component.position.x - 2,
                        component.position.y - 2,
                        component.position.x + width + 2,
                        component.position.y + height + 2,
                        borderColor,
                        0.0f,
                        ImDrawFlags.None,
                        1.0f
                );
            }

            renderBoundsGuides(drawList, activeSnapGuides, 0xFF4DA6FF, 1.5f);
        }
        ImGui.end();
    }

    private void renderBoundsGuides(ImDrawList drawList, List<HudRenderer.SnapGuide> guides, int color, float thickness) {
        for (HudRenderer.SnapGuide guide : guides) {
            drawList.addLine(guide.startX(), guide.startY(), guide.endX(), guide.endY(), color, thickness);
        }
    }

    private void wrappedDescription(String text) {
        ImGui.pushFont(ImGuiFonts.getFont("product-regular", 18));
        ImGui.textWrapped(text);
        ImGui.popFont();
    }

    private boolean renderActionButton(String icon, float buttonSize, String tooltip) {
        boolean pressed;
        ImGui.pushFont(ImGuiFonts.getFont("icomoon", 18));
        ImGui.pushStyleVar(imgui.flag.ImGuiStyleVar.ButtonTextAlign, 0.5f, 0.5f);
        pressed = ImGui.button(icon, buttonSize + 2.0f, buttonSize);
        ImGui.popStyleVar();
        ImGui.popFont();

        if (ImGui.isItemHovered()) {
            ImGui.setTooltip(tooltip);
        }

        return pressed;
    }

    private boolean renderDragHandleButton(float buttonSize, String tooltip) {
        boolean pressed;
        ImGui.pushFont(ImGuiFonts.getFont("icomoon", 18));
        ImGui.pushStyleVar(imgui.flag.ImGuiStyleVar.ButtonTextAlign, 0.5f, 0.5f);
        pressed = ImGui.button(Icons.DRAG, buttonSize, buttonSize);
        ImGui.popStyleVar();
        ImGui.popFont();

        if (ImGui.isItemHovered()) {
            ImGui.setTooltip(tooltip);
        }

        return pressed;
    }

    /**
     * @see <a href="https://github.com/ocornut/imgui/issues/1931">Source</a>
     */
    private boolean handleElementReorder(CustomHudComponent component) {
        if (reorderDraggingComponent == null) {
            return false;
        }

        if (!ImGui.isMouseDown(0)) {
            clearReorderState();
            return false;
        }

        if (!ImGui.isMouseDragging(0) || component == reorderDraggingComponent) {
            return false;
        }

        float mouseY = ImGui.getMousePosY();
        float itemMinY = ImGui.getItemRectMinY();
        float itemMaxY = ImGui.getItemRectMaxY();
        if (mouseY < itemMinY || mouseY > itemMaxY) {
            return false;
        }

        boolean moveBefore = mouseY < (itemMinY + itemMaxY) * 0.5f;
        if (component == reorderLastTargetComponent && moveBefore == reorderLastInsertBefore) {
            return false;
        }

        List<CustomHudComponent> elements = Apex.hudRenderer.getCustomComponents();
        int fromIndex = elements.indexOf(reorderDraggingComponent);
        int hoveredIndex = elements.indexOf(component);
        if (fromIndex < 0 || hoveredIndex < 0) {
            return false;
        }

        int targetIndex;
        if (moveBefore) {
            targetIndex = fromIndex < hoveredIndex ? hoveredIndex - 1 : hoveredIndex;
        } else {
            targetIndex = fromIndex < hoveredIndex ? hoveredIndex : hoveredIndex + 1;
        }

        targetIndex = Math.clamp(targetIndex, 0, elements.size() - 1);

        boolean moved = Apex.hudRenderer.moveCustomComponent(fromIndex, targetIndex);
        if (!moved) {
            return false;
        }

        reorderLastTargetComponent = component;
        reorderLastInsertBefore = moveBefore;
        List<CustomHudComponent> updatedElements = Apex.hudRenderer.getCustomComponents();
        selectedIndex = updatedElements.indexOf(reorderDraggingComponent);
        return true;
    }

    private void clearReorderState() {
        reorderDraggingComponent = null;
        reorderLastTargetComponent = null;
        reorderLastInsertBefore = false;
    }

    private boolean renderElementButton(String label, boolean selected, float width, float height) {
        if (selected) {
            ImGui.pushStyleColor(imgui.flag.ImGuiCol.Button, ImGui.getStyleColorVec4(imgui.flag.ImGuiCol.Button).plus(0.15f, 0.15f, 0.15f, 0.0f));
            ImGui.pushStyleColor(imgui.flag.ImGuiCol.ButtonHovered, ImGui.getStyleColorVec4(imgui.flag.ImGuiCol.ButtonHovered).plus(0.15f, 0.15f, 0.15f, 0.0f));
            ImGui.pushStyleColor(imgui.flag.ImGuiCol.ButtonActive, ImGui.getStyleColorVec4(imgui.flag.ImGuiCol.ButtonActive).plus(0.15f, 0.15f, 0.15f, 0.0f));
        }

        ImGui.pushStyleVar(imgui.flag.ImGuiStyleVar.ButtonTextAlign, 0.06f, 0.5f);
        boolean pressed = ImGui.button(label, width, height);
        ImGui.popStyleVar();

        if (selected) {
            ImGui.popStyleColor(3);
        }

        return pressed;
    }

    private boolean isInEditorWindow(float mouseX, float mouseY) {
        return mouseX >= editorWindowX
                && mouseX <= editorWindowX + editorWindowWidth
                && mouseY >= editorWindowY
                && mouseY <= editorWindowY + editorWindowHeight;
    }
}
