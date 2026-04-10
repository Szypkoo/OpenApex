package lol.apex.util.entity.simulation;

import lol.apex.util.annotation.AIPasted;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.List;

@AIPasted("old one is too inaccurate so yea")
public class SimulatedPlayer {

    private final LivingEntity player;
    private final World world;

    public Vec3d pos;
    public Vec3d velocity;
    public Box box;

    public float yaw, pitch;
    public boolean onGround;
    public boolean sprinting;

    private int jumpCooldown;
    public double fallDistance = 0.;

    public SimulatedPlayer(LivingEntity player) {
        this.player = player;
        this.world = player.getEntityWorld();

        this.pos = player.getEntityPos();
        this.velocity = player.getVelocity();
        this.box = player.getBoundingBox();

        this.yaw = player.getYaw();
        this.pitch = player.getPitch();
        this.onGround = player.isOnGround();
        this.sprinting = player.isSprinting();
    }

    public static SimulatedPlayer fromPlayer(LivingEntity player) {
        return new SimulatedPlayer(player);
    }

    public void tick(boolean jump, float forward, float sideways) {

        // --- INPUT VECTOR (vanilla) ---
        Vec3d input = new Vec3d(sideways, 0, forward);
        if (input.lengthSquared() > 1.0) input = input.normalize();

        float speed = getMovementSpeed();

        // rotate input by yaw
        Vec3d movement = Entity.movementInputToVelocity(input, speed, yaw);

        // --- APPLY MOVEMENT (vanilla acceleration) ---
        velocity = velocity.add(movement);

        // --- JUMP ---
        if (jump && onGround && jumpCooldown == 0) {
            velocity = new Vec3d(velocity.x, getJumpVelocity(), velocity.z);

            if (sprinting) {
                float rad = (float)Math.toRadians(yaw);
                velocity = velocity.add(
                        -Math.sin(rad) * 0.2,
                        0,
                        Math.cos(rad) * 0.2
                );
            }

            jumpCooldown = 10;
        }

        if (jumpCooldown > 0) jumpCooldown--;

        // --- GRAVITY ---
        if (!hasNoGravity()) {
            velocity = velocity.add(0, -0.08, 0);
        }
//        if (isTouchingWater()) {
//            fallDistance = 0;
//        }
        if (player.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
            fallDistance = 0;
        }
        // --- DRAG ---
        velocity = new Vec3d(
                velocity.x * 0.91,
                velocity.y * 0.98,
                velocity.z * 0.91
        );

        // --- FRICTION (block-based) ---
        float slipperiness = getBlockSlipperiness();
        if (onGround) {
            float f = slipperiness * 0.91F;
            velocity = new Vec3d(
                    velocity.x * f,
                    velocity.y,
                    velocity.z * f
            );
        }

        // --- MOVE WITH COLLISION ---
        move(velocity);
    }
    private void move(Vec3d movement) {

        List<VoxelShape> entityCollisions = world.getEntityCollisions(player, box.stretch(movement));

        Vec3d adjusted = Entity.adjustMovementForCollisions(
                player,
                movement,
                box,
                world,
                entityCollisions
        );

        pos = pos.add(adjusted);
        box = box.offset(adjusted);

        boolean collidedX = movement.x != adjusted.x;
        boolean collidedY = movement.y != adjusted.y;
        boolean collidedZ = movement.z != adjusted.z;

        boolean wasOnGround = onGround;
        onGround = collidedY && movement.y < 0;

        // --- FALL DISTANCE LOGIC (vanilla-like) ---
        if (onGround) {
            if (!wasOnGround) {
                // just landed
                fallDistance = 0;
            }
        } else if (adjusted.y < 0) {
            // falling
            fallDistance -= adjusted.y;
        }

        // collision velocity cancel
        if (collidedX) velocity = new Vec3d(0, velocity.y, velocity.z);
        if (collidedZ) velocity = new Vec3d(velocity.x, velocity.y, 0);
        if (onGround) velocity = new Vec3d(velocity.x, 0, velocity.z);
    }

    private float getMovementSpeed() {
        double base = player.getAttributeValue(EntityAttributes.MOVEMENT_SPEED);

        if (sprinting) base *= 1.3;

        if (player.hasStatusEffect(StatusEffects.SPEED)) {
            int amp = player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
            base *= 1.0 + 0.2 * (amp + 1);
        }

        if (player.hasStatusEffect(StatusEffects.SLOWNESS)) {
            int amp = player.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier();
            base *= 1.0 - 0.15 * (amp + 1);
        }

        return (float) base;
    }

    private float getJumpVelocity() {
        float base = 0.42F;

        if (player.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            int amp = player.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier();
            base += 0.1F * (amp + 1);
        }

        return base;
    }

    private float getBlockSlipperiness() {
        BlockPos below = BlockPos.ofFloored(
                pos.x,
                box.minY - 0.5000001,
                pos.z
        );

        return world.getBlockState(below).getBlock().getSlipperiness();
    }

    private boolean hasNoGravity() {
        return player.hasNoGravity();
    }

    public SimulatedPlayer copy() {
        SimulatedPlayer s = new SimulatedPlayer(player);
        s.pos = this.pos;
        s.velocity = this.velocity;
        s.box = this.box;
        s.yaw = this.yaw;
        s.pitch = this.pitch;
        s.onGround = this.onGround;
        s.sprinting = this.sprinting;
        s.jumpCooldown = this.jumpCooldown;
        return s;
    }
}