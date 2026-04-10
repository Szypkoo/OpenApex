package lol.apex.feature.module.implementation.movement.speed;

import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;
import lol.apex.util.player.PlayerUtil;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;

public class FakepixelSpeed extends SubModule {
    public FakepixelSpeed() {
        super("Fakepixel", "Old Vulcan but some checks are nuked", "Old Vulcan");
    }
    private static int airTicks = 0;
    private static int lastFunnyTick = -1;
    private static final double SPEED = 0.31;
    private static double curSpeed = SPEED;
    private static final double MAX_FRICTION_DIFF = 1.0E-5;
    private static boolean isStrafeExempt() {
        return airTicks <= 3;
    }
    public static void onMove(PlayerMoveEvent e) {
        airTicks = mc.player.isOnGround() ? 0 : airTicks + 1;
        // BLOCK_BREAK(data -> data.getActionProcessor().getSinceBlockBreakTicks() < 25),
        // strafe exempts if: ExemptType.TELEPORT, ExemptType.PARTIALLY_STUCK, ExemptType.BUKKIT_VELOCITY,
        // ExemptType.DEATH, ExemptType.JOINED, ExemptType.WORLD_CHANGE, ExemptType.FLIGHT, ExemptType.FULLY_STUCK,
        // ExemptType.GLIDING, ExemptType.ELYTRA, ExemptType.CREATIVE, ExemptType.SPECTATOR, ExemptType.SOUL_SAND,
        // ExemptType.VEHICLE, ExemptType.ENTITY_COLLISION, ExemptType.DEPTH_STRIDER, ExemptType.FROZEN,
        // ExemptType.LIQUID, ExemptType.DOLPHINS_GRACE, ExemptType.RIPTIDE, ExemptType.SWIMMING,
        // ExemptType.CLIMBABLE, ExemptType.ENDER_PEARL, ExemptType.FIREBALL, ExemptType.CHORUS_FRUIT,
        // ExemptType.BLOCK_BREAK (!!), ExemptType.CANCELLED_MOVE
        if (!isStrafeExempt() && mc.player.age - lastFunnyTick < 7) {
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                    mc.player.getBlockPos(),
                    mc.player.getMovementDirection().getOpposite()
            ));
            lastFunnyTick = mc.player.age;
        }
        if (airTicks > 2 && curSpeed > 0.25) {
            final var friction = curSpeed * 0.9100000262260437 + 0.026;
            curSpeed = friction + MAX_FRICTION_DIFF;
        }
        if (mc.player.isOnGround()) {
            PlayerUtil.jump();
            e.setY(mc.player.getVelocity().y);
            curSpeed = SPEED;
        }
        if (curSpeed <= 0.25) {
            curSpeed = SPEED;
        }
        MoveUtil.modifySpeed(e, MoveUtil.getSpeed());
    }
}
