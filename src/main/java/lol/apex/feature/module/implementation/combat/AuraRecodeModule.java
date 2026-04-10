package lol.apex.feature.module.implementation.combat;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.Apex;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.event.player.PlayerRotationEvent;
import lol.apex.event.player.WorldChangeEvent;
import lol.apex.event.render.RenderTickEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.implementation.movement.MovementCorrectionModule;
import lol.apex.feature.module.implementation.other.AntiBotModule;
import lol.apex.feature.module.implementation.player.ChestAuraModule;
import lol.apex.feature.module.implementation.player.ScaffoldModule;
import lol.apex.feature.module.implementation.visual.TargetHUDModule;
import lol.apex.feature.module.setting.implementation.*;
import lol.apex.util.animation.ItemAnimationUtil;
import lol.apex.util.math.TimerUtil;
import lol.apex.util.player.MoveUtil;
import lol.apex.util.rotation.AlgebraUtil;
import lol.apex.util.rotation.MathUtil;
import lol.apex.util.rotation.RotationUtil;
import lol.apex.util.skidded.ClickUtil;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// This is a recoded version of Aura

@ModuleInfo(
        name = "Aura",
        description = "Automatically attacks people around you.",
        category = Category.COMBAT
)
public class AuraRecodeModule extends Module {
    public final SliderSetting range = new SliderSetting("Range", 3.0f, 0f, 6f, 0.05f);
    public final EnumSetting<RotationMode> rotationMode = new EnumSetting<RotationMode>("Rotation Mode", RotationMode.REGULAR);
    public final EnumSetting<AutoBlockMode> autoBlockMode = new EnumSetting<>("Auto Block", AutoBlockMode.FAKE);
    public final EnumSetting<HitboxMode> hitboxMode = new EnumSetting<HitboxMode>("Hitbox Mode", HitboxMode.LATEST);

    public final BoolSetting ignoreFriends = new BoolSetting("Ignore Friends", true);

    public final BoolSetting jitter = new BoolSetting("Jitter", true).hide(()-> !(rotationMode.getValue() == RotationMode.REGULAR));
    public final SliderSetting yawJitter = new SliderSetting("Yaw Jitter", 0.3f, 0f, 2f, 0.01f).hide(()-> !jitter.getValue());
    public final SliderSetting pitchJitter = new SliderSetting("Pitch Jitter", 0.15f, 0f, 2f, 0.01f).hide(()-> !jitter.getValue());
    public final BoolSetting autoDisable = new BoolSetting("Auto Disable", true);
    private final SliderSetting min = new SliderSetting("Min CPS", 15, 0, 20, 1);
    private final SliderSetting max = new SliderSetting("Max CPS", 19, 0, 20, 1);

    private final SliderSetting rotationSpeed = new SliderSetting("Rotation Speed", 180f, 10f, 360f, 1f);

    public final BoolSetting attackPlayers = new BoolSetting("Players", true);

    public LivingEntity target;

    private double currentCPS;
    private final TimerUtil attackTimer = new TimerUtil();

    @Override
    public void onEnable() {

        currentCPS = MathUtil.nextDouble(min.getValue(), max.getValue());

        ItemAnimationUtil.setBlocking(false);

        super.onEnable();
    }

    @Override
    public void onDisable() {

        ItemAnimationUtil.setBlocking(false);

        super.onDisable();
    }

    @RequiredArgsConstructor
    public enum RotationMode {
        REGULAR("Regular"),
        POLAR("Polar"),
        NONE("None"),
        SNAP("Snap");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @RequiredArgsConstructor
    public enum HitboxMode {
        LATEST("Latest"),
        LEGACY("Legacy");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @RequiredArgsConstructor
    public enum AutoBlockMode {

        NONE("None"),
        FAKE("Fake");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    private List<LivingEntity> getTargets() {
        if (mc.player == null || mc.world == null) {
            return new ArrayList<>();
        }

        double theRange = range.getValue();

        Box searchBox = mc.player.getBoundingBox().expand(theRange, theRange, theRange);
        List<LivingEntity> nearby = mc.world.getEntitiesByClass(LivingEntity.class, searchBox, entity -> true);

        return nearby.stream()
                .filter(this::isValidTarget)
                .filter(entity -> {
                    Box targetBox = hitboxFix(entity);

                    double distanceSq = mc.player.squaredDistanceTo(
                            AlgebraUtil.nearest(targetBox, mc.player.getEyePos())
                    );

                    boolean canSee = mc.player.canSee(entity);

                    if (canSee) {
                        return distanceSq <= (range.getValue() * range.getValue());
                    }

                    return false;
                })
                .sorted(Comparator.comparingDouble(entity -> {
                    Box targetBox = hitboxFix(entity);
                    return mc.player.squaredDistanceTo(AlgebraUtil.nearest(targetBox, mc.player.getEyePos()));
                }))
                .collect(Collectors.toList());
    }

    private Box hitboxFix(Entity entity) {
        Box box = entity.getBoundingBox();
        if(!entity.isAlive()) return null;

        switch (hitboxMode.getValue()) {
            case LEGACY -> {
                double shrinkX = 0.1;
                double shrinkY = 0.05;
                double shrinkZ = 0.1;
                box = new Box(box.minX + shrinkX, box.minY, box.minZ + shrinkZ, box.maxX - shrinkX, box.minY + 1.8, box.maxZ - shrinkZ);
            }
            case LATEST -> {
                box = entity.getBoundingBox();
            }
        }
        return box;
    }

    private boolean isStillValidTarget(LivingEntity entity) {
        if (entity == null) return false;
        if (!entity.isAlive()) return false;

        if (!isValidTarget(entity)) return false;

        double maxRange = range.getValue() + 0.3;
        double rangeSq = maxRange * maxRange;
        return mc.player.squaredDistanceTo(entity) <= rangeSq;
    }

    private boolean isValidTarget(Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;

        if (entity == mc.player) return false;
        if (living.isDead()) return false;

        if (entity instanceof ArmorStandEntity) return false;

        if (ignoreFriends.getValue() && living instanceof PlayerEntity player &&
                Apex.friendManager.isFriend(player)) {
            return false;
        }

        ChestAuraModule chestAura = Apex.moduleManager.getByClass(ChestAuraModule.class);
        if (chestAura != null && chestAura.enabled() && chestAura.targetChest != null) {
            return false;
        }

        ScaffoldModule scaffold = Apex.moduleManager.getByClass(ScaffoldModule.class);
        if (scaffold != null && scaffold.enabled()) {
            return false;
        }

        if (living instanceof PlayerEntity player) {
            AntiBotModule antiBot = Apex.moduleManager.getByClass(AntiBotModule.class);

            if (antiBot != null && antiBot.enabled() && antiBot.isBot(player)) {
                return false;
            }

            if (!attackPlayers.getValue()) return false;

            if (isShieldBlocking(player)) return false;
        }

        return (living instanceof Angerable ang && isAngryAt(ang, mc.player)) || living instanceof HostileEntity || living instanceof PlayerEntity;
    }

    private boolean isShieldBlocking(PlayerEntity player) {
        if (player == null) return false;

        return player.isBlocking() &&
                player.getActiveItem().isOf(Items.SHIELD);
    }

    private boolean isAngryAt(Angerable ang, LivingEntity e) {
        return ang.canTarget(e) && ang.hasAngerTime();
    }

    @EventHook
    public void onRenderTick(RenderTickEvent event) {
        if (mc.player == null || mc.world == null)
            return;

        boolean shouldClick = false;

        if (target != null && canSwing(target)) {

            boolean cooldownReady = mc.player.getAttackCooldownProgress(0.0f) >= 0.95f;
            boolean cpsReady = attackTimer.passed((long) (1000.0 / currentCPS), false);

            if (cooldownReady && cpsReady) {
                shouldClick = true;

                currentCPS = MathUtil.nextDouble(min.getValue(), max.getValue());
                attackTimer.reset();
            }
        }

        if (shouldClick) {
            attack(target);
        } else {
            ClickUtil.action(ClickUtil.Button.LEFT, false);

            if (autoBlockMode.getValue() == AutoBlockMode.FAKE && target == null) {
                ItemAnimationUtil.setBlocking(false);
            }
        }
    }

    public boolean canSwing(Entity entity) {
        if (entity == null || entity == mc.player || !entity.isAlive())
            return false;

        if (!mc.player.canSee(entity)) return false;

        double distance = mc.player.getEyePos().distanceTo(
                AlgebraUtil.nearest(entity.getBoundingBox(), mc.player.getEyePos())
        );

        return distance <= range.getValue();
    }

    private void attack(Entity target) {
        if (mc.currentScreen != null) return;

        if (rotationMode.getValue() == RotationMode.NONE) {
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
        } else {
            ClickUtil.action(ClickUtil.Button.LEFT, true);
        }

        if (autoBlockMode.getValue() == AutoBlockMode.FAKE && target != null) {
            ItemAnimationUtil.setBlocking(true);
        }

        currentCPS = MathUtil.nextDouble(min.getValue(), max.getValue());
    }

    @EventHook
    public void onPlayerMove(PlayerMoveEvent event) {
        MovementCorrectionModule moveCorrection = Apex.moduleManager.getByClass(MovementCorrectionModule.class);

        if (target != null && moveCorrection.mode.getValue() == MovementCorrectionModule.Mode.SILENT) {
            MoveUtil.silentMoveFix(event, RotationUtil.prevyaw);
        }
    }


    @EventHook
    public void onRotation(PlayerRotationEvent event) {
        if (this.target == null) return;

        switch (rotationMode.getValue()) {
            case POLAR -> {
                Box targetBox = hitboxFix(target);
                final var z = RotationUtil.rotation(
                        AlgebraUtil.nearest(targetBox, mc.player.getEyePos()),
                        mc.player.getEyePos()
                );

                var newYaw = z.yaw();
                var newPitch = z.pitch();

                newYaw = RotationUtil.smoothRot(
                        mc.player.getYaw(), newYaw,
                        MathUtil.nextFloat(0.0f, 5.0f) * 6.0f
                );

                newPitch = RotationUtil.smoothRot(
                        mc.player.getPitch(),
                        (float) ((double) newPitch +
                                10.0 * Math.sin(Math.toRadians(mc.player.getYaw() - newYaw))),
                        MathUtil.nextFloat(0.0f, 5.0f) * 6.0f
                );

                newYaw += MathUtil.nextFloat(-1.0f, 1.0f) * 1.2f;
                newPitch += MathUtil.nextFloat(-0.2f, 0.2f);

                event.angles(newYaw, newPitch);
            }
            case REGULAR -> {
                final var target = RotationUtil.rotation(
                        AlgebraUtil.nearest(Objects.requireNonNull(hitboxFix(this.target)), mc.player.getEyePos()),
                        mc.player.getEyePos()
                );

                float speed = rotationSpeed.getValue();

                float yawJit = 0f;
                float pitchJit = 0f;

                if (jitter.getValue()) {
                    yawJit = MathUtil.nextFloat(-yawJitter.getValue(), yawJitter.getValue());
                    pitchJit = MathUtil.nextFloat(-pitchJitter.getValue(), pitchJitter.getValue());
                }

                float smoothedYaw = RotationUtil.smoothRot(
                        mc.player.getYaw(),
                        target.yaw() + yawJit,
                        speed
                );

                float smoothedPitch = RotationUtil.smoothRot(
                        mc.player.getPitch(),
                        target.pitch() + pitchJit,
                        speed
                );

                event.yaw(smoothedYaw);
                event.pitch(smoothedPitch);
            }

            case SNAP -> {
                event.set(RotationUtil.rotation(
                        AlgebraUtil.nearest(hitboxFix(target), mc.player.getEyePos()),
                        mc.player.getEyePos()
                ));
            }
        }
    }

    @EventHook
    public void onTick(ClientTickEvent event) {
        if (mc.player == null || mc.world == null) {
            target = null;
            return;
        }

        if (isStillValidTarget(target)) {
            return;
        }

        List<LivingEntity> targets = getTargets();
        target = targets.isEmpty() ? null : targets.getFirst();
    }


    @EventHook
    public void onWorldChange(WorldChangeEvent event) {
        if (autoDisable.getValue()) {
            Apex.notificationRenderer.push("Aura", "Disabled on world change.");
            toggle();
        }
    }
}
