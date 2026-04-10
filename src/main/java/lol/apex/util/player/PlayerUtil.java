package lol.apex.util.player;

import lol.apex.util.CommonVars;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;

@UtilityClass
public class PlayerUtil implements CommonVars {

    private static double theY;
    private static boolean fallDistance;

    public static boolean fallDistance(double blocks) {
        if (mc.player.getVelocity().y < 0) {
            if (!fallDistance) {
                theY = mc.player.getY();
                fallDistance = true;
            }

            if (theY - mc.player.getY() >= blocks) {
                theY = mc.player.getY();
                return true;
            }
        } else {
            fallDistance = false;
        }

        return false;
    }

    public static Vec3d getClosestPoint(Entity target) {
        Box hb = target.getBoundingBox();
        Vec3d eyePos = mc.player.getEyePos();

        double cx = MathHelper.clamp(eyePos.x, hb.minX, hb.maxX);
        double cy = MathHelper.clamp(eyePos.y, hb.minY, hb.maxY);
        double cz = MathHelper.clamp(eyePos.z, hb.minZ, hb.maxZ);

        return new Vec3d(cx, cy, cz);
    }

    public static double getBiblicallyAccurateDistanceToEntity(Entity target) {
        return mc.player.getEyePos().distanceTo(getClosestPoint(target));
    }

    public static void swingMainHand() {
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    public static boolean isInWeb(PlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        Block block = mc.world != null ? mc.world.getBlockState(pos).getBlock() : null;
        return block == Blocks.COBWEB;
    }

    @SuppressWarnings("deprecation")
    public static boolean isOverLiquid() {
        BlockPos pos = mc.player.getBlockPos().down();
        return mc.world.getBlockState(pos).isLiquid();
    }

    public static void attack(Entity target) {
        mc.interactionManager.attackEntity(mc.player, target);
    }

    public static void attackBlock(BlockPos targetPos, Direction direction) {
        mc.interactionManager.attackBlock(targetPos, direction);
    }

    public static double getPlayerBPS() {
        if (mc.player == null) return 0.0;

        double deltaX = mc.player.getX() - mc.player.lastX;
        double deltaZ = mc.player.getZ() - mc.player.lastZ;

        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ); // blocks per tick

        return distance * 20;
    }

    public static double getArmorProtection(ItemStack stack) {
        double protection = 0;
        final AttributeModifiersComponent attributeModifiersComponent = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
        for (AttributeModifiersComponent.Entry entry : attributeModifiersComponent.modifiers()) {
            if (entry.attribute() != EntityAttributes.ARMOR) {
                continue;
            }
            EntityAttributeModifier modifier = entry.modifier();
            protection += modifier.value();
        }
        return protection;
    }

    public static double getStackAttackDamage(ItemStack stack) {
        double attackDamage = 0;
        final AttributeModifiersComponent attributeModifiersComponent = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
        for (AttributeModifiersComponent.Entry entry : attributeModifiersComponent.modifiers()) {
            if (entry.attribute() != EntityAttributes.ATTACK_DAMAGE || entry.slot() != AttributeModifierSlot.MAINHAND) {
                continue;
            }
            EntityAttributeModifier modifier = entry.modifier();
            attackDamage += modifier.value();
        }
        return attackDamage;
    }

    public static void jump() {
        KeyBinding.setKeyPressed(mc.options.jumpKey.getDefaultKey(), true);

        new Thread(() -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}

            KeyBinding.setKeyPressed(mc.options.jumpKey.getDefaultKey(), false);
        }).start();
    } // THIS BYPASSES GRIM DONT TOUCH IT

    public static void selfDamage(double blocks) {
        var player = mc.player;
        if (player == null) return;

        BlockPos pos = player.getBlockPos();

        // Move up
        mc.player.networkHandler.sendPacket(
                new PlayerMoveC2SPacket.PositionAndOnGround(
                        player.getX(),
                        player.getY() + blocks,
                        player.getZ(),
                        false, mc.player.horizontalCollision
                )
        );

        mc.player.networkHandler.sendPacket(
                new PlayerMoveC2SPacket.PositionAndOnGround(
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        false, mc.player.horizontalCollision
                )
        );
    }
}
