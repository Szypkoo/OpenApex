package lol.apex.util.player;

import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.util.CommonVars;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

@UtilityClass
public final class MoveUtil implements CommonVars {
    public static void setMotionY(double d) {
        mc.player.setVelocity(mc.player.getVelocity().x, d, mc.player.getVelocity().z);        
    }

    public static void stopMovementKeys(boolean sneak) {
        mc.options.forwardKey.setPressed(false);
        mc.options.backKey.setPressed(false);
        mc.options.leftKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
        mc.options.jumpKey.setPressed(false);
        mc.options.sprintKey.setPressed(false);

        if (sneak) {
            mc.options.sneakKey.setPressed(false);
        }
    }

    /// copied from {@link net.minecraft.entity.LivingEntity#jump()} (the sprint boost part)
    /// @param yaw yaw to use for the extra boost
    /// @param boost extra b/t (blocks per tick) to add, the sprint boost part uses 0.2
    public static void boost(float yaw, double boost) {
        float g = yaw * (float) (Math.PI / 180.0);
        mc.player.addVelocityInternal(new Vec3d(-MathHelper.sin(g) * boost, 0.0, MathHelper.cos(g) * boost));
    }

    /// copied from {@link net.minecraft.entity.LivingEntity#jump()} (the sprint boost part)
    /// @param boost extra b/t (blocks per tick) to add, the sprint boost part uses 0.2
    public static void boost(double boost) {
        boost(mc.player.getYaw(), boost);
    }

    public static void addMotion(double x, double y, double z) {
        mc.player.setVelocity(
                mc.player.getVelocity().x + x,
                mc.player.getVelocity().y + y,
                mc.player.getVelocity().z + z
        );
    }

    public static void addMotionY(double y) {
        mc.player.setVelocity(
                mc.player.getVelocity().x,
                mc.player.getVelocity().y + y,
                mc.player.getVelocity().z
        );
    }

    public static double direction() {
        if (mc.player == null) return 0.0;

        float yaw = mc.player.getYaw();
        float forward = movementForward();
        float strafe = movementSideways();

        if (forward < 0) yaw += 180F;

        float factor = 1F;
        if (forward < 0) factor = -0.5F;
        else if (forward > 0) factor = 0.5F;

        if (strafe > 0) yaw -= 90F * factor;
        if (strafe < 0) yaw += 90F * factor;

        return Math.toRadians(yaw);
    }

    public static void moveFlying(double increase) {
        if (!isMoving()) return;
        final double yaw = direction();
        setMotionX(mc.player.getVelocity().x + -MathHelper.sin((float) yaw) * increase);
        setMotionZ(mc.player.getVelocity().z + MathHelper.cos((float) yaw) * increase);
    }


    public static void setSpeedNoEvent(double speed) {
        double forward = movementForward();
        double strafe = movementSideways();
        float yaw = mc.player.getYaw();
        if (forward == 0 && strafe == 0) {
            mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
        } else {
            if (forward != 0) {
                if (strafe > 0) {
                    yaw += (float) (forward > 0 ? -45 : 45);
                } else if (strafe < 0) {
                    yaw += (float) (forward > 0 ? 45 : -45);
                }
                strafe = 0;
                if (forward > 0) {
                    forward = 1;
                } else if (forward < 0) {
                    forward = -1;
                }
            }
            double sin = MathHelper.sin((float) Math.toRadians(yaw + 90));
            double cos = MathHelper.cos((float) Math.toRadians(yaw + 90));
            mc.player.setVelocity(forward * speed * cos + strafe * speed * sin, mc.player.getVelocity().y, forward * speed * sin - strafe * speed * cos);
        }
    }

    public static void setMotionZ(double d) {
        mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().x, d);        

    }

    public static void setMotionX(double d) {
        mc.player.setVelocity(d, mc.player.getVelocity().y, mc.player.getVelocity().z);

    }

    public static boolean isMoving() {
        return !mc.player.input.getMovementInput().equals(Vec2f.ZERO);
    } 

    public static void modifySpeed(PlayerMoveEvent event, double d) {
        double d2 = movementForward();
        double d3 = movementSideways();
        float f = mc.player.getYaw(); 
        if(d2 == 0.0 && d3 == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else {
            if(d2 != 0.0) {
                if(d3 > 0.0) {
                    f += (float)(d2 > 0.0 ? -45 : 45);
                } else if (d3 < 0.0) {
                    f += (float)(d2 > 0.0 ? 45 : -45);
                } 

                d3 = 0.0; 
                if(d2 > 0.0) {
                    d2 = 1.0;
                } else if (d2 < 0.0) {
                    d2 = -1.0;
                }
            }
            double sin = Math.sin(Math.toRadians(f + 90.0f)); 
            double cos = Math.cos(Math.toRadians(f + 90.0f)); 

            event.setX(d2 * d * cos + d3 * d * sin);
            event.setZ(d2 * d * sin - d3 * d * cos);
        }
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = mc.player.getAbilities().getWalkSpeed() * 2.873;

        if (mc.player.hasStatusEffect(StatusEffects.SLOWNESS)) {
            StatusEffectInstance slowness =
            mc.player.getStatusEffect(StatusEffects.SLOWNESS);
            baseSpeed /= 1.0 + 0.2 * (slowness.getAmplifier() + 1);
        }

        if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            StatusEffectInstance speed =
            mc.player.getStatusEffect(StatusEffects.SPEED);
            baseSpeed *= 1.0 + 0.2 * (speed.getAmplifier() + 1);
        }

        return baseSpeed;
    }

    public static void stop() {
        mc.player.setVelocity(0, 0, 0);        

    }

    public static float movementSideways() {
        return mc.player.input.getMovementInput().x;
    }

    public static float movementForward() {
        return mc.player.input.getMovementInput().y;
    }
 
    public static void silentMoveFix(PlayerMoveEvent event, float serverYaw) {
        float yaw = serverYaw;

        float forward = (float) movementForward();
        float strafe  = (float) movementSideways();

        if (forward == 0.0F && strafe == 0.0F) {
            event.setX(0.0);
            event.setZ(0.0);
            return;
        }

        int dif = (int) ((MathHelper.wrapDegrees(
                mc.player.getYaw() - serverYaw - 23.5F - 135.0F
        ) + 180.0F) / 45.0F);

        float calcForward = 0.0F;
        float calcStrafe = 0.0F;

        switch (dif) {
            case 0 -> {
                calcForward = forward;
                calcStrafe = strafe;
            }
            case 1 -> {
                calcForward += forward;
                calcStrafe -= forward;
                calcForward += strafe;
                calcStrafe += strafe;
            }
            case 2 -> {
                calcForward = strafe;
                calcStrafe = -forward;
            }
            case 3 -> {
                calcForward -= forward;
                calcStrafe -= forward;
                calcForward += strafe;
                calcStrafe -= strafe;
            }
            case 4 -> {
                calcForward = -forward;
                calcStrafe = -strafe;
            }
            case 5 -> {
                calcForward -= forward;
                calcStrafe += forward;
                calcForward -= strafe;
                calcStrafe -= strafe;
            }
            case 6 -> {
                calcForward = -strafe;
                calcStrafe = forward;
            }
            case 7 -> {
                calcForward += forward;
                calcStrafe += forward;
                calcForward -= strafe;
                calcStrafe += strafe;
            }
        }

        if (calcForward > 1.0F ||
            (calcForward < 0.9F && calcForward > 0.3F) ||
            calcForward < -1.0F ||
            (calcForward > -0.9F && calcForward < -0.3F)) {
            calcForward *= 0.5F;
        }

        if (calcStrafe > 1.0F ||
            (calcStrafe < 0.9F && calcStrafe > 0.3F) ||
            calcStrafe < -1.0F ||
            (calcStrafe > -0.9F && calcStrafe < -0.3F)) {
            calcStrafe *= 0.5F;
        }

        float speed = (float) getBaseMoveSpeed();

        float d = calcStrafe * calcStrafe + calcForward * calcForward;
        if (d >= 1.0E-4F) {
            d = MathHelper.sqrt(d);
            if (d < 1.0F) d = 1.0F;

            d = speed / d;
            calcStrafe *= d;
            calcForward *= d;

            float yawRad = (float) Math.toRadians(yaw);
            float sin = MathHelper.sin(yawRad);
            float cos = MathHelper.cos(yawRad);

            double motionX = calcStrafe * cos - calcForward * sin;
            double motionZ = calcForward * cos + calcStrafe * sin;

            event.setX(motionX);
            event.setZ(motionZ);
        }
    }

    public static double getSpeed() {
        return Math.sqrt(mc.player.getVelocity().x * mc.player.getVelocity().x + 
                        mc.player.getVelocity().z * mc.player.getVelocity().z);
    }

    public static void useDiagonalSpeed(PlayerMoveEvent event) {
        boolean isDiagonal = movementForward() != 0 && movementSideways() != 0;
        if (isDiagonal) {
            double currentSpeed = getSpeed();
            modifySpeed(event, currentSpeed * 0.98);
        }
    }

    public static void partialStrafe(PlayerMoveEvent event, double percent) {
        double currentSpeed = getSpeed();
        double targetSpeed = currentSpeed * percent;
        modifySpeed(event, targetSpeed);
    }

    public static double getMotionY() {
        return mc.player.getVelocity().y;
    }

    public static double[] forward(final double d) {
        float f = movementForward();
        float f2 = movementSideways();
        float f3 = mc.player.getYaw();
        if (f != 0.0f) {
            if (f2 > 0.0f) {
                f3 += ((f > 0.0f) ? -45 : 45);
            } else if (f2 < 0.0f) {
                f3 += ((f > 0.0f) ? 45 : -45);
            }
            f2 = 0.0f;
            if (f > 0.0f) {
                f = 1.0f;
            } else if (f < 0.0f) {
                f = -1.0f;
            }
        }
        final double d2 = Math.sin(Math.toRadians(f3 + 90.0f));
        final double d3 = Math.cos(Math.toRadians(f3 + 90.0f));
        final double d4 = f * d * d3 + f2 * d * d2;
        final double d5 = f * d * d2 - f2 * d * d3;
        return new double[]{d4, d5};
    }

    public static double getMotionX() {
        return mc.player.getVelocity().x;
    }

    public static double getMotionZ() {
        return mc.player.getVelocity().z;
    } 

    public static boolean isMovingForwards() {
        return mc.player != null
                && mc.player.input != null
                && movementForward() > 0
                && movementSideways() == 0;
    } 

    public static boolean isMovingForward() {
        return movementForward() > 0;
    } 

    public static double getVerusLimit(boolean dif) {

        if (mc.player == null) return 0.0;
        if (dif && mc.player.fallDistance > 0.2f) {
            return getBaseMoveSpeed();
        }

        if (mc.player.fallDistance < 0.2f) {

            if (mc.player.isSprinting()) {

                if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
                    StatusEffectInstance effect =
                            mc.player.getStatusEffect(StatusEffects.SPEED);

                    if (effect != null) {
                        int amplifier = effect.getAmplifier();

                        if (mc.player.isOnGround()) {
                            return amplifier == 1 ? 0.7f : 0.62f;
                        } else {
                            return amplifier == 1 ? 0.81f : 0.62f;
                        }
                    }
                }

                return mc.player.isOnGround() ? 0.54f : 0.46f;
            }

            return getBaseMoveSpeed() * 1.02f;
        }

        return 0.0;
    }

    public static double getAllowedHorizontalDistance() {
        double speed = getBaseMoveSpeed();

        if (mc.player.isSprinting()) {
            speed *= 1.3;
        }

        if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            StatusEffectInstance effect = mc.player.getStatusEffect(StatusEffects.SPEED);
            speed *= 1.0 + 0.2 * (effect.getAmplifier() + 1);
        }

        if (mc.player.hasStatusEffect(StatusEffects.SLOWNESS)) {
            StatusEffectInstance effect = mc.player.getStatusEffect(StatusEffects.SLOWNESS);
            speed /= 1.0 + 0.2 * (effect.getAmplifier() + 1);
        }

        return speed;
    }
}
