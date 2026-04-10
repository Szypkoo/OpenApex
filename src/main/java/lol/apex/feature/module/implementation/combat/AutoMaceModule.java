package lol.apex.feature.module.implementation.combat;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.player.PlayerRotationEvent;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.math.TimerUtil;
import lol.apex.util.player.PlayerUtil;
import lol.apex.util.rotation.MathUtil;
import lol.apex.util.rotation.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;

import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

import dev.toru.clients.eventBus.EventHook;

/*
    credit goes to tato and mostly his ai
*/

import lol.apex.feature.module.base.*;

@ModuleInfo( 
    name = "AutoMace",
    description = "Automatically uses the mace in pvp.",
    category = Category.COMBAT
)
public class AutoMaceModule extends Module {

    public final SliderSetting minFallDistance = new SliderSetting("Min Fall Distance", 3.0f, 1.0f, 10.0f, 0.1f);
    public final SliderSetting attackDelay = new SliderSetting("Attack Delay", 100f, 0f, 500f, 10f);
    public final SliderSetting targetRange = new SliderSetting("Target Range", 6.0f, 1.0f, 10.0f, 0.1f);
    public final SliderSetting densityThreshold = new SliderSetting("Density Threshold", 7.0f, 1.0f, 20.0f, 0.5f);

    public final BoolSetting targetPlayers = new BoolSetting("Target Players", true);
    public final BoolSetting targetMobs = new BoolSetting("Target Mobs", false);
    public final BoolSetting silentSwap = new BoolSetting("Silent Swap", true);
    public final BoolSetting doubleTap = new BoolSetting("Double Tap", false);
    public final SliderSetting rotationSpeed = new SliderSetting("Rotation Speed", 180f, 10f, 360f, 1f);

    public Entity currentTarget;

    private final TimerUtil attackTimer = new TimerUtil();
    private int savedSlot = -1;
    private double fallStartY = -1;
    private boolean isFalling = false;

    // DTAP State machine: 0 = Idle, 1 = Breach Hit, 2 = Swapped, 3 = Density Hit
    private int doubleTapStage = 0;

    @EventHook
    public void onTick(ClientTickEvent event) {
        if (mc.player == null || mc.world == null) return;

        updateFall();
        attack();
    }

    private void updateFall() {
        boolean onGround = mc.player.isOnGround();
        boolean falling = mc.player.getVelocity().y < -0.1;
        boolean wearingElytra = mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA;
        boolean gliding = !onGround && wearingElytra;
        double currentY = mc.player.getY();

        if (onGround) {
            if (isFalling) {
                resetFall();
            }
            if (savedSlot != -1) {
                switchToSlot(savedSlot);
                savedSlot = -1;
            }
            return;
        }

        if (!isFalling) {
            isFalling = true;
            fallStartY = currentY;
            doubleTapStage = 0;
        } else if ((falling || gliding) && fallStartY != -1 && currentY > fallStartY) {
            fallStartY = currentY;
        }
    }

    private void attack() {
        Entity target = findTarget();
        if (target == null) {
            currentTarget = null;
            return;
        }

        currentTarget = target;

        boolean wearingElytra = mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA;
        boolean gliding = !mc.player.isOnGround() && wearingElytra;

        boolean canAttack = isFalling && (mc.player.getVelocity().y < -0.1 || (doubleTap.getValue() && (gliding || wearingElytra)));

        if (!canAttack)
            return;

        double fallDist = fallStartY == -1 ? 0 : Math.max(0, fallStartY - mc.player.getY());

        if (!doubleTap.getValue() && fallDist < minFallDistance.getValue())
            return;

        if (doubleTap.getValue()) {
            handleDoubleTap(target);
            return;
        }

        handleMaceAttack(target);
    }


    private Entity findTarget() {
        List<Entity> targets = StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(this::isValidTarget)
                .filter(e -> mc.player.distanceTo(e) <= targetRange.getValue())
                .sorted(Comparator.comparingDouble(e -> mc.player.distanceTo(e)))
                .toList();
        
        return targets.isEmpty() ? null : targets.get(0);
    }

    private void handleDoubleTap(Entity target) {
        switch (doubleTapStage) {
            case 0:
                int breachSlot = findBreachMaceSlot();
                if (breachSlot != -1) {
                    attackEntity(target, breachSlot);
                    doubleTapStage = 1;
                }
                break;
            case 1:
                if (performSwapElytraToChestplate()) {
                    doubleTapStage = 2;
                } else {
                    doubleTapStage = 2;
                }
                break;
            case 2:
                int densitySlot = findDensityMaceSlot();
                if (densitySlot != -1) {
                    attackEntity(target, densitySlot);
                    doubleTapStage = 3;
                }
                break;
            case 3:
                attemptRewear();
                doubleTapStage = 0;
                break;
        }
    }



    private void handleMaceAttack(Entity target) {
        double fallDist = fallStartY == -1 ? 0 : Math.max(0, fallStartY - mc.player.getY());

        if (attackTimer.passed(attackDelay.getValue().longValue(), false)) {
            int targetSlot = getAppropriateMaceSlot(fallDist);
            
            if (targetSlot != -1) {
                attackEntity(target, targetSlot);
                attackTimer.reset();
            }
        }
    }

    private void attackEntity(Entity target, int slot) {
        int prev = mc.player.getInventory().getSelectedSlot();
        boolean silent = silentSwap.getValue();
        
        if (slot != prev) {
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
            if (!silent) {
                mc.player.getInventory().setSelectedSlot(slot);
            }
        }

        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);

        if (silent && slot != prev) {
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(prev));
        }
    }

    private boolean isValidTarget(Entity entity) {
        if (entity == null || entity == mc.player || entity == mc.getCameraEntity())
            return false;
        if (!(entity instanceof LivingEntity))
            return false;
        if (!((LivingEntity) entity).isAlive())
            return false;

        if (entity instanceof PlayerEntity) {
            return targetPlayers.getValue();
        } else {
            return targetMobs.getValue();
        }
    }

    private int getAppropriateMaceSlot(double fallDistance) {
        boolean useDensity = fallDistance >= densityThreshold.getValue();
        int targetSlot = useDensity ? findDensityMaceSlot() : findBreachMaceSlot();

        if (targetSlot == -1) {
            targetSlot = findAnyMaceSlot();
        }

        return targetSlot;
    }

    private int findDensityMaceSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.MACE && hasEnchantment(stack, "density")) {
                return i;
            }
        }
        return -1;
    }

    private int findBreachMaceSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.MACE && hasEnchantment(stack, "breach")) {
                return i;
            }
        }
        return -1;
    }

    private int findAnyMaceSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.MACE) {
                return i;
            }
        }
        return -1;
    }

    private boolean hasEnchantment(ItemStack stack, String name) {
        return stack.getEnchantments().getEnchantments().stream()
                .anyMatch(enchantment -> enchantment.getIdAsString().contains(name));
    }

    private void switchToSlot(int slot) {
        if (slot >= 0 && slot < 9) {
            mc.player.getInventory().setSelectedSlot(slot);
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        }
    }

    private boolean performSwapElytraToChestplate() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            String name = stack.getItem().toString();
            if (name.contains("chestplate")) {
                int prev = mc.player.getInventory().getSelectedSlot();
                
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(i));
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                
                if (silentSwap.getValue()) {
                    mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(prev));
                } else {
                    mc.player.getInventory().setSelectedSlot(i);
                }
                return true;
            }
        }
        return false;
    }

    private void attemptRewear() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.ELYTRA) {
                int prev = mc.player.getInventory().getSelectedSlot();
                
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(i));
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                
                if (silentSwap.getValue()) {
                    mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(prev));
                } else {
                    mc.player.getInventory().setSelectedSlot(i);
                }
                break;
            }
        }
    }

    private void resetFall() {
        isFalling = false;
        fallStartY = -1;
        doubleTapStage = 0;
    }

    @Override
    public void onEnable() {
        resetFall();
        savedSlot = -1;
        super.onEnable();

    }

    @Override
    public void onDisable() {
        if (savedSlot != -1) {
            switchToSlot(savedSlot);
        }
        resetFall();
        super.onDisable();
    }

    @EventHook
    public void onRotation(PlayerRotationEvent event) {
        if (mc.player == null || currentTarget == null) return;

        if (!isFalling && !doubleTap.getValue()) return;

        var rot = RotationUtil.rotation(
                PlayerUtil.getClosestPoint(currentTarget),
                mc.player.getEyePos()
        );

        float yaw = RotationUtil.smoothRot(
                mc.player.getYaw(),
                rot.yaw(),
                rotationSpeed.getValue().floatValue()
        );

        float pitch = RotationUtil.smoothRot(
                mc.player.getPitch(),
                rot.pitch(),
                rotationSpeed.getValue().floatValue()
        );

        yaw += MathUtil.nextFloat(-0.3f, 0.3f);
        pitch += MathUtil.nextFloat(-0.15f, 0.15f);

        event.yaw(yaw);
        event.pitch(pitch);
    }
}