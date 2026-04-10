package lol.apex.feature.module.implementation.visual;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.render.RenderWorldEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.ColorSetting;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.entity.simulation.SimulatedPlayer;
import lol.apex.util.render.RenderUtil;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

import java.awt.*;

import static net.minecraft.client.gl.RenderPipelines.POSITION_COLOR_SNIPPET;
import static net.minecraft.client.gl.RenderPipelines.RENDERTYPE_LINES_SNIPPET;

@ModuleInfo(
        name = "SimulatedPlayerTest",
        description = "this niggerlink simulation",
        category = Category.VISUAL
)
public class SimulatedPlayerTestModule extends Module {
    private static final RenderPipeline DEBUG_FILLED_BOX =
            RenderPipelines.register(
                    RenderPipeline.builder(POSITION_COLOR_SNIPPET)
                            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                            .withDepthWrite(false)
                            .withLocation("pipeline/debug_filled_box")
                            .build()
            );

    private static final RenderLayer FILLED =
            RenderLayer.of(
                    "filled_box",
                    RenderSetup.builder(DEBUG_FILLED_BOX)
                            .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                            .outputTarget(OutputTarget.MAIN_TARGET)
                            .translucent()
                            .build()
            );

    private static final RenderPipeline LINES_PIPELINE =
            RenderPipelines.register(
                    RenderPipeline.builder(RENDERTYPE_LINES_SNIPPET)
                            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                            .withDepthWrite(false)
                            .withLocation("pipeline/lines_no_depth")
                            .build()
            );

    private static final RenderLayer LINES =
            RenderLayer.of(
                    "lines",
                    RenderSetup.builder(LINES_PIPELINE)
                            .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                            .outputTarget(OutputTarget.MAIN_TARGET)
                            .build()
            );

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.OUTLINE);
    private final ColorSetting color = new ColorSetting("Color", new Color(150, 0, 255));
    private final SliderSetting outlineWidth = new SliderSetting("Outline width", 1.5f, 1.0f, 5.0f, 0.1f).hide(() -> !mode.getValue().equals(Mode.OUTLINE));
    private final SliderSetting alphaValue = new SliderSetting("Box opacity", 150, 0, 255, 1).hide(() -> !mode.getValue().equals(Mode.FILLED));
    private final SliderSetting ticks = new SliderSetting("Ticks", 20, 1, 20, 1);
    private SimulatedPlayer currentPlayer;

    private SimulatedPlayer simulate() {
        final var simulatedPlayer = SimulatedPlayer.fromPlayer(mc.player);
        for (var i = 0; i < ticks.getValue(); i++) {
            simulatedPlayer.tick(mc.player.isJumping() || mc.options.jumpKey.isPressed(), mc.player.input.getMovementInput().y, mc.player.input.getMovementInput().x);
        }
        currentPlayer = simulatedPlayer;
        return simulatedPlayer;
    }

    @EventHook
    public void onTick(ClientTickEvent e) {
        final var a = simulate();
        mc.player.setPosition(a.pos);
        mc.player.setVelocity(mc.player.age % 2 == 0 ? Vec3d.ZERO : a.velocity);
    }

    @EventHook
    public void onRender(RenderWorldEvent event) {
        if (mc.player == null) return;
        if (currentPlayer == null) return;

        var provider = mc.worldRenderer.bufferBuilders.getEntityVertexConsumers();
        var outlinedConsumer = provider.getBuffer(LINES);
        var filledConsumer = provider.getBuffer(FILLED);
        var camera = event.renderState.cameraRenderState.pos;

        var stack = new MatrixStack();

        var pos = currentPlayer.pos;
        var bb = mc.player.getDimensions(mc.player.getPose())
                .getBoxAt(pos)
                .offset(-camera.x, -camera.y, -camera.z);

        switch (mode.getValue()) {
            case FILLED -> {
                int alpha = Math.round(alphaValue.getValue());
                int fillColor = (alpha << 24) | (color.getValue().getRGB() & 0x00FFFFFF);
                RenderUtil.drawSolidBox(stack, filledConsumer, bb, fillColor);
            }

            case OUTLINE -> VertexRendering.drawOutline(
                    stack,
                    outlinedConsumer,
                    VoxelShapes.cuboid(bb),
                    0,
                    0,
                    0,
                    color.getValue().getRGB(),
                    outlineWidth.getValue()
            );
        }
    }

    @RequiredArgsConstructor
    private enum Mode {
        FILLED("Filled"),
        OUTLINE("Outline");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }
}