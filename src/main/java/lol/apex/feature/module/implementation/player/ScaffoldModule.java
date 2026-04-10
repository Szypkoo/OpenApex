package lol.apex.feature.module.implementation.player;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.Apex;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.player.PlayerRotationEvent;
import lol.apex.event.player.WorldChangeEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.implementation.player.scaffold.rotation.HypixelRotations;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.entity.simulation.SimulatedPlayer;
import lol.apex.util.player.MoveUtil;
import lol.apex.util.player.PlayerUtil;
import lol.apex.util.world.ScaffoldUtil;
import lol.apex.util.rotation.BlockRotationUtil;
import lol.apex.util.rotation.Rotation;
import lol.apex.util.rotation.RotationUtil;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jspecify.annotations.Nullable;

@ModuleInfo(
        name = "Scaffold",
        description = "Automatically bridges for you.",
        category = Category.PLAYER
)
public final class ScaffoldModule extends Module {
    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.NORMAL);
    public final EnumSetting<RotateMode> rotateMode = new EnumSetting<>("Rotate Mode", RotateMode.NONE);
    public final SliderSetting rotationSpeed = new SliderSetting("Rotation Speed", 360f, 0f, 360f, 0.5f);
    public final BoolSetting keepY = new BoolSetting("Keep Y", true);
    public final BoolSetting avoidUnderplace = new BoolSetting("Avoid Underplace", true);
    public final BoolSetting autoDisable = new BoolSetting("Auto Disable", true);
    public final BoolSetting autoJump = new BoolSetting("Auto Jump", false);

    private int airTicks = 0;

    @RequiredArgsConstructor
    public enum Mode {
        NORMAL("Normal"),
        TELLY("Telly");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @RequiredArgsConstructor
    public enum RotateMode {
        NONE("None"),
        BASIC("Basic"),
        HYPIXEL("Hypixel"),
        GODBRIDGE("Godbridge"),
        POLAR("Polar");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    private int oldSlot = -1; // -1 = no old slot
    private int startY;
    private boolean placed = false;
    private boolean jumped = false;
    @SuppressWarnings("unused")
    private boolean isKeepyActive = false;
    private @Nullable Rotation planRots;

    private BlockHitResult plan;

    private int findBlockInHotbar() {
        if (mc.player == null) throw new IllegalStateException("mc.player == null when calling findBlockSlot");
        // I wish I could just like Slots.Hotbar.forEach { i -> i.stack.item is ItemBlock } or something but cat
        for (int i = 0; i < 9; i++) {
            final var stack = mc.player.getInventory().getMainStacks().get(i);
            if (stack.getItem() instanceof BlockItem) {
                mc.player.getInventory().setSelectedSlot(i);
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onEnable() {
        oldSlot = mc.player.getInventory().getSelectedSlot();
        startY = mc.player.getBlockY();
    }

    @Override
    public void onDisable() {
        if (mc.player != null && oldSlot != -1) {
            mc.player.getInventory().setSelectedSlot(oldSlot);
            oldSlot = -1;
        }
        airTicks = 0;
    }

    private HitResult nigger(Rotation rot, float tickProgress, double maxDistance) {
        final var plr = mc.player;
        Vec3d vec3d = plr.getCameraPosVec(tickProgress);
        final var rotVec = plr.getRotationVector(rot.pitch(), rot.yaw());
        Vec3d vec3d3 = vec3d.add(rotVec.x * maxDistance, rotVec.y * maxDistance, rotVec.z * maxDistance);
        return plr.getEntityWorld().raycast(new RaycastContext(
                vec3d, vec3d3,
                RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE,
                plr
        ));
    }

    @SuppressWarnings("unused")
    @EventHook
    private void onTick(ClientTickEvent ignored) {
        if (mc.player == null) return;
        if (mc.currentScreen != null) return;

        if (mode.getValue() == Mode.TELLY && placed && (mc.player.isOnGround() || airTicks >= 7)) {
            plan = null;
        }

        if (autoJump.getValue() && mc.player.isOnGround() && !mc.player.isJumping()) {
            PlayerUtil.jump();
        }

        final var player = mc.player;

        if (mode.getValue() == Mode.TELLY) {
            if (player.isOnGround()) {
                airTicks = 0;
                if (mc.player.isSprinting() && !mc.player.isJumping()) {
                    PlayerUtil.jump();
                    jumped = true;
                }
            } else {
                airTicks++;
            }
        }

        if (!(player.getInventory().getSelectedStack().getItem() instanceof BlockItem)) {
            int slot = findBlockInHotbar();
            if (slot != -1) {
                player.getInventory().setSelectedSlot(slot);
            } else {
                plan = null;
                return;
            }
        }

        final var targetX = player.getBlockX();
        int targetY = keepY.getValue() ? startY : player.getBlockY();
        final var targetZ = player.getBlockZ();

        if (keepY.getValue() && mc.player.isJumping() && !mc.options.pickItemKey.isPressed()) {
            isKeepyActive = true;
            targetY = player.getBlockY();
            startY = targetY;
        } else {
            isKeepyActive = false;
        }

        BlockPos belowTarget = new BlockPos(targetX, targetY - 1, targetZ);

        ScaffoldUtil.PosFace place =
                !ScaffoldUtil.isValidBlockPosition(belowTarget)
                        ? ScaffoldUtil.findPlaceableNeighbor(belowTarget, false)
                        : null;

        if (avoidUnderplace.getValue()) {
            BlockPos playerPos = player.getBlockPos();
            if (place != null && place.bp().getY() > mc.player.getBlockY()) {
                place = null;
                plan = null;
            }
        }

        if (place != null) {
            plan = new BlockHitResult(
                    ScaffoldUtil.getRandomizedHitVec(place.bp(), place.dir()),
                    place.dir(),
                    place.bp(),
                    false
            );
            planRots = BlockRotationUtil.getRotationTowardsBlock(plan);
            placed = false;
        }

        if (plan == null) return;

        if (mc.world.getBlockState(plan.getBlockPos()).hasBlockEntity()) {
            plan = null;
            return;
        }

        if (rotateMode.getValue() == RotateMode.NONE) {
            mc.interactionManager.interactBlock(player, Hand.MAIN_HAND, plan);
            mc.player.swingHand(Hand.MAIN_HAND);
            placed = true;
            jumped = false;
        }
        else if (mc.crosshairTarget instanceof BlockHitResult b && b.getBlockPos().equals(plan.getBlockPos())) {
            if (mc.world.getBlockState(b.getBlockPos()).hasBlockEntity()) {
                plan = null;
                return;
            }

            if (rotateMode.getValue() == RotateMode.GODBRIDGE) {
                mc.doItemUse();
                final var world = mc.world;
                final var blockPos = mc.player.getBlockPos().down();
                final var plr = SimulatedPlayer.fromPlayer(mc.player);
                plr.tick(false, mc.player.input.getMovementInput().y, mc.player.input.getMovementInput().x);
                if (!plr.onGround && mc.player.isOnGround()) {
                    mc.player.jump();
                }
            }

            final var r = planRots == null ? new Rotation(mc.player.getYaw(), mc.player.getPitch()) : planRots;
            final var cast = nigger(r, 1f, mc.player.getBlockInteractionRange());
            if (!(cast instanceof BlockHitResult c)) {
                return;
            }
            mc.interactionManager.interactBlock(player, Hand.MAIN_HAND, c);
            mc.player.swingHand(Hand.MAIN_HAND);
            placed = true;
            jumped = false;
        }
    }

    private boolean needsRotation() {
        return mc.player != null && (!jumped || !mc.player.isOnGround()) && airTicks >= 3;
    }

    @SuppressWarnings("unused")
    @EventHook
    private void onRotation(PlayerRotationEvent e) {
        if (plan == null || mc.player == null) return;

        if (rotateMode.getValue() == RotateMode.NONE) return;
        if (mode.getValue() == Mode.TELLY && !needsRotation()) return;

        switch (rotateMode.getValue()) {
            case BASIC -> {
                final var target = BlockRotationUtil.getRotationTowardsBlock(plan);
                if (target == null) {
                    plan = null;
                    return;
                }

                float maxSpeed = rotationSpeed.getValue();
                float smoothedYaw = RotationUtil.smoothRot(mc.player.getYaw(), target.yaw(), maxSpeed);
                float smoothedPitch = RotationUtil.smoothRot(mc.player.getPitch(), target.pitch(), maxSpeed);

                e.yaw(smoothedYaw);
                e.pitch(smoothedPitch);

                if (Math.random() > 0.99) {
                    smoothedYaw += (Math.random() - 0.5) * 2;
                    smoothedPitch += (Math.random() - 0.5) * 2;
                }
            }

            case HYPIXEL -> {
                final var target = BlockRotationUtil.getRotationTowardsBlock(plan);
                if (target == null) {
                    plan = null;
                    return;
                }

                if (rotateMode.getValue() == RotateMode.HYPIXEL) {
                    e.set(HypixelRotations.smooth(target));
                    return;
                }
                final var speed = rotationSpeed.getValue();

                final var smoothedYaw = RotationUtil.smoothRot(mc.player.getYaw(), target.yaw(), speed);
                final var smoothedPitch = target.pitch();

                e.yaw(smoothedYaw);
                e.pitch(smoothedPitch);
            }
            case GODBRIDGE -> {
                final var speed = rotationSpeed.getValue();

                final var smoothedYaw = RotationUtil.smoothRot(mc.player.getYaw(), getGodBridgeYaw(), speed);
                final var smoothedPitch = RotationUtil.smoothRot(mc.player.getPitch(), 75.7f, speed);

                e.yaw(smoothedYaw);
                e.pitch(smoothedPitch);
            }
            case POLAR -> {
                final var target = BlockRotationUtil.getRotationTowardsBlock(plan);
                if (target == null) {
                    plan = null;
                    return;
                }

                float maxSpeed = rotationSpeed.getValue();
                final var smoothedYaw = target.yaw();
                final var smoothedPitch = target.pitch();
                final var calcRots = new Rotation(smoothedYaw, smoothedPitch);
                var rots = planRots == null ? planRots = calcRots : planRots;
                e.set(planRots);
            }
        }
    }

    private float getGodBridgeYaw() {
        if (!MoveUtil.isMoving()) {
            return 45f;
        }
        float direction = RotationUtil.getMovementDirectionYaw() + 180f;
        final var movingYaw = Math.round(direction / 45) * 45;

        final var isMovingStraight = movingYaw % 90 == 0f;
        return movingYaw + -45f;
    }

    @EventHook
    public void onWorldChange(WorldChangeEvent event) {
        if (autoDisable.getValue()) {
        //    Apex.sendChatMessage("Scaffold has been disabled due to world change.");
            Apex.notificationRenderer.push("Scaffold", "Disabled on world change.");

            toggle();
        }
    }

    @Override
    public String getSuffix() {
        return rotateMode.getValue().toString();
    }
}
