package lol.apex.feature.emulator.vulcan;

import dev.toru.clients.eventBus.EventHook;
import dev.toru.clients.eventBus.EventPriority;
import lol.apex.event.packet.PacketEvent;
import lol.apex.util.CommonVars;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.PlayerInput;
import org.jspecify.annotations.Nullable;

/**
 * Provides data to {@link CheckEmulator the check emulator} for it to use.
 */
public final class DataListener implements CommonVars {
    public static boolean boat = false;
    private static int vehicleEnterTick = -1;

    public static int vehicleTicks = -1;
    public static int vehicleAirTicks = -1;
    /**
     * Null if accessed after exiting boat, or when no move packet has been sent after entering.
     */
    public static @Nullable VehicleMoveC2SPacket lastMove;

    // LOWEST means we won't get packets canceled by BlinkManager for queueing.
    @EventHook(priority = EventPriority.LOW)
    private void onPacket(PacketEvent.Send e) {
        if (mc.player == null || mc.world == null) return;
        switch (e.getPacket()) {
            case PlayerInteractEntityC2SPacket p -> {
                if (p.type instanceof PlayerInteractEntityC2SPacket.InteractHandler
                        && mc.world.getEntityById(p.entityId) instanceof VehicleEntity) {
                    boat = true;
                    vehicleEnterTick = mc.player.age;
                    lastMove = null;
                }
            }
            case PlayerInputC2SPacket(PlayerInput input) -> {
                if (boat && input.sneak()) {
                    boat = false;
                    vehicleEnterTick = -1;
                    lastMove = null;
                }
            }
            case VehicleMoveC2SPacket pkt -> {
                vehicleTicks = mc.player.age - vehicleEnterTick;
                lastMove = pkt;
                final var vehicle = mc.player.getVehicle();
                if (vehicle != null) {
                    vehicleAirTicks = vehicle.isOnGround() ? 0 : vehicleAirTicks++;
                }
            }
            default -> {}
        }
    }
}
