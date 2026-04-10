package lol.apex.feature.module.implementation.player;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import dev.toru.clients.eventBus.EventHook;
import lol.apex.Apex;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.player.PlayerRotationEvent;
import lol.apex.event.player.WorldChangeEvent;
import lol.apex.event.render.RenderWorldEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.implementation.combat.AuraRecodeModule;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.render.RenderUtil;
import lol.apex.util.rotation.BlockRotationUtil;
import lol.apex.util.rotation.RotationUtil;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

import java.util.HashSet;
import java.util.Set;

import static net.minecraft.client.gl.RenderPipelines.POSITION_COLOR_SNIPPET;
import static net.minecraft.client.gl.RenderPipelines.RENDERTYPE_LINES_SNIPPET;

@ModuleInfo(
        name = "ChestAura",
        description = "Automatically opens chests around you.",
        category = Category.PLAYER
)
public class ChestAuraModule extends Module {
    public final SliderSetting range = new SliderSetting("Range", 3.0f, 0f, 6f, 0.05f);
    public final BoolSetting rotate = new BoolSetting("Rotate", true);
    public final SliderSetting rotationSpeed = new SliderSetting("Rotation Speed", 180f, 10f, 360f, 1f);
    public final BoolSetting visuals = new BoolSetting("Visuals", true);
    public final SliderSetting visualRange = new SliderSetting("Visual Range", 6.0f, 0f, 12f, 0.05f).hide(() -> !visuals.getValue());
    public final EnumSetting<RenderMode> renderMode = new EnumSetting<>("Mode", RenderMode.OUTLINE).hide(() -> !visuals.getValue());
    public final SliderSetting alphaValue = new SliderSetting("Fill Alpha", 150, 0, 255, 1).hide(() -> !visuals.getValue() || !renderMode.getValue().equals(RenderMode.FILLED));
    public final SliderSetting outlineWidth = new SliderSetting("Outline Width", 1.5f, 1.0f, 5.0f, 0.1f).hide(() -> !visuals.getValue() || !renderMode.getValue().equals(RenderMode.OUTLINE));
    public final BoolSetting pauseScaffold = new BoolSetting("Pause Scaffold", true);
    public final BoolSetting waitForAura = new BoolSetting("Wait For Aura", true);
    public final Set<BlockPos> openedChests = new HashSet<>();
    public BlockPos targetChest = null;

    @RequiredArgsConstructor
    public enum RenderMode {
        OUTLINE("Outline"),
        FILLED("Filled");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

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

    private boolean isRiding() {
        return mc.player != null && mc.player.hasVehicle();
    }

    private boolean shouldWaitForAura() {
        var aura = Apex.moduleManager.getByClass(AuraRecodeModule.class);
        return waitForAura.getValue() && aura != null && aura.target != null;
    }

    private boolean isScaffolding() {
        return pauseScaffold.getValue() &&
                Apex.moduleManager.getByClass(ScaffoldModule.class) != null &&
                Apex.moduleManager.getByClass(ScaffoldModule.class).enabled();
    }

    @EventHook
    public void onTick(ClientTickEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if (Apex.moduleManager.getByClass(AuraRecodeModule.class).target != null) {
            targetChest = null;
            return;
        }

        if (isScaffolding()) {
            targetChest = null;
            return;
        }

        if (shouldWaitForAura()) {
            targetChest = null;
            return;
        }

        if (isRiding()) {
            targetChest = null;
            return;
        }

        BlockPos playerPos = mc.player.getBlockPos();
        int rangeint = range.getValue().intValue();

        targetChest = findChests(playerPos, rangeint);

        if (!isValidTarget(targetChest)) {
            targetChest = null;
            return;
        }

        if (!rotate.getValue()) {
            openChest(targetChest);
        }
    }

    @EventHook
    public void onRotation(PlayerRotationEvent event) {
        if (!isValidTarget(targetChest)) {
            targetChest = null;
            return;
        }

        if (shouldWaitForAura()) {
            targetChest = null;
            return;
        }

        if (isScaffolding()) {
            targetChest = null;
            return;
        }

        if (Apex.moduleManager.getByClass(AuraRecodeModule.class).target != null) {
            targetChest = null;
            return;
        }

        if (isRiding()) {
            targetChest = null;
            return;
        }

        if (openedChests.contains(targetChest)) {
            targetChest = null;
            return;
        }

        Vec3d hitPos = new Vec3d(
                targetChest.getX() + 0.5,
                targetChest.getY() + 0.5,
                targetChest.getZ() + 0.5
        );

        BlockHitResult hitResult = new BlockHitResult(hitPos, Direction.UP, targetChest, false);

        var targetRot = BlockRotationUtil.getRotationTowardsBlock(hitResult);

        if (targetRot != null) {
            float speed = rotationSpeed.getValue();

            float newYaw = RotationUtil.smoothRot(event.yaw(), targetRot.yaw(), speed);
            float newPitch = RotationUtil.smoothRot(event.pitch(), targetRot.pitch(), speed);

            event.yaw(newYaw);
            event.pitch(newPitch);

            if (Math.abs(newYaw - targetRot.yaw()) < 5 &&
                    Math.abs(newPitch - targetRot.pitch()) < 5) {
                openChest(targetChest);
            }
        }
    }

    private BlockPos findChests(BlockPos center, int radius) {
        for (BlockPos pos : BlockPos.iterate(
                center.add(-radius, -radius, -radius),
                center.add(radius, radius, radius))) {

            if (!openedChests.contains(pos) &&
                    mc.world.getBlockState(pos).getBlock() instanceof ChestBlock) {
                return pos;
            }
        }
        return null;
    }

    private void openChest(BlockPos chestPos) {
        if (isScaffolding() || isRiding() || !isValidTarget(chestPos)) return;

        Vec3d hitPos = new Vec3d(
                chestPos.getX() + 0.5,
                chestPos.getY() + 0.5,
                chestPos.getZ() + 0.5
        );

        BlockHitResult hitResult = new BlockHitResult(hitPos, Direction.UP, chestPos, false);

        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);

        openedChests.add(chestPos);
        targetChest = null;
    }

    @Override
    public void onDisable() {
        openedChests.clear();
        targetChest = null;
    }

    @EventHook
    public void onWorldChange(WorldChangeEvent event) {
        openedChests.clear();
        targetChest = null;
    }

    @EventHook
    public void onRender(RenderWorldEvent event) {
        if (mc.player == null || mc.world == null || !visuals.getValue()) {
            return;
        }

        BlockPos playerPos = mc.player.getBlockPos();
        int rangeint = visualRange.getValue().intValue();

        var provider = mc.worldRenderer.bufferBuilders.getEntityVertexConsumers();
        var outlinedConsumer = provider.getBuffer(LINES);
        var filledConsumer = provider.getBuffer(FILLED);
        var camera = event.renderState.cameraRenderState.pos;
        var stack = new MatrixStack();

        Set<BlockPos> renderedChests = new HashSet<>();

        for (BlockPos pos : BlockPos.iterate(
                playerPos.add(-rangeint, -rangeint, -rangeint),
                playerPos.add(rangeint, rangeint, rangeint))) {

            if (renderedChests.contains(pos)) {
                continue;
            }

            if (mc.world.getBlockState(pos).getBlock() instanceof ChestBlock) {
                BlockState blockState = mc.world.getBlockState(pos);
                ChestType chestType = blockState.get(ChestBlock.CHEST_TYPE);

                if (chestType != ChestType.SINGLE) {
                    Direction chestFacing = blockState.get(ChestBlock.FACING);
                    Direction checkDir = chestType == ChestType.LEFT ?
                            chestFacing.rotateYClockwise() : chestFacing.rotateYCounterclockwise();

                    BlockPos secondChestPos = pos.offset(checkDir);

                    if (mc.world.getBlockState(secondChestPos).getBlock() instanceof ChestBlock && !renderedChests.contains(secondChestPos)) {
                        // Merge both chest positions into one box
                        Box box1 = new Box(pos).offset(-camera.x, -camera.y, -camera.z);
                        Box box2 = new Box(secondChestPos).offset(-camera.x, -camera.y, -camera.z);
                        Box mergedBox = box1.union(box2);

                        int color = openedChests.contains(pos) && openedChests.contains(secondChestPos) ? 0xFF00FF00 : 0xFFFF0000;

                        switch (renderMode.getValue()) {
                            case FILLED -> {
                                int alpha = Math.round(alphaValue.getValue());
                                int fillColor = (alpha << 24) | (color & 0x00FFFFFF);
                                RenderUtil.drawSolidBox(stack, filledConsumer, mergedBox, fillColor);
                            }
                            case OUTLINE -> VertexRendering.drawOutline(
                                    stack,
                                    outlinedConsumer,
                                    VoxelShapes.cuboid(mergedBox),
                                    0, 0, 0,
                                    color,
                                    outlineWidth.getValue()
                            );
                        }

                        renderedChests.add(pos);
                        renderedChests.add(secondChestPos);
                    }
                } else {
                    Box box = new Box(pos).offset(-camera.x, -camera.y, -camera.z);
                    int color = openedChests.contains(pos) ? 0xFF00FF00 : 0xFFFF0000;

                    switch (renderMode.getValue()) {
                        case FILLED -> {
                            int alpha = Math.round(alphaValue.getValue());
                            int fillColor = (alpha << 24) | (color & 0x00FFFFFF);
                            RenderUtil.drawSolidBox(stack, filledConsumer, box, fillColor);
                        }
                        case OUTLINE -> VertexRendering.drawOutline(
                                stack,
                                outlinedConsumer,
                                VoxelShapes.cuboid(box),
                                0, 0, 0,
                                color,
                                outlineWidth.getValue()
                        );
                    }

                    renderedChests.add(pos);
                }
            }
        }
    }

    private boolean isValidTarget(BlockPos pos) {
        return pos != null && mc.world != null && !openedChests.contains(pos) &&
                mc.world.getBlockState(pos).getBlock() instanceof ChestBlock;
    }
}
