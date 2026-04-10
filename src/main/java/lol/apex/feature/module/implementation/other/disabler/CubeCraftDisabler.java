package lol.apex.feature.module.implementation.other.disabler;

import lol.apex.Apex;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.combat.AuraRecodeModule;
import lol.apex.feature.module.implementation.movement.SpeedModule;
import lol.apex.feature.module.implementation.other.DisablerModule;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.manager.implementation.BlinkManager;
import lol.apex.util.game.PacketUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.LinkedHashSet;

public class CubeCraftDisabler extends SubModule {

    public CubeCraftDisabler(DisablerModule parent) {
        super("CubeCraft", "Boolean array backend anticheat disabler.", "Specific");
        addSettings(groundSpoof, autoResync);
    }

    public static BoolSetting groundSpoof = new BoolSetting("(CC) GroundSpoof", true);
    public static BoolSetting autoResync = new BoolSetting("(CC) AutoResync", false);

    private static final LinkedHashSet<BlinkManager.QueuedPacket<?>> packetQueue = new LinkedHashSet<>();

    private static long lastSwingTime;
    private static boolean swingPendingConfirm;
    private static boolean hasMissed;
    private static boolean didReleased;
    private static LivingEntity targetWhenSwang;

    public static void onSend(PacketEvent.Send event) {

        Packet<?> packet = event.getPacket();

        if (autoResync.getValue()) {

            if (packet instanceof HandSwingC2SPacket) {

                LivingEntity target = Apex.moduleManager.getByClass(AuraRecodeModule.class).target;

                if (target != null) {

                    lastSwingTime = System.currentTimeMillis();
                    swingPendingConfirm = true;
                    hasMissed = false;
                    didReleased = false;

                    targetWhenSwang = target;

                    Apex.sendChatMessage("§eSwung at: " + target.getName().getString());
                }
            }
        }

        if (groundSpoof.getValue()) {

            if (packet instanceof PlayerMoveC2SPacket move) {

                double y = move.getY(0);
                double spoof = y - (y % 0.015625);

                move.y = spoof;
            }
        }
    }

    public static void onReceive(PacketEvent.Receive event) {

        Packet<?> packet = event.getPacket();

        if (autoResync.getValue()) {

            if (packet instanceof PlaySoundS2CPacket soundPacket) {

                SoundEvent soundEvent = soundPacket.getSound().value();
                Identifier sound = Registries.SOUND_EVENT.getId(soundEvent);

                if (sound != null && sound.getPath().contains("hurt")
                        && swingPendingConfirm
                        && targetWhenSwang != null) {

                    long time = System.currentTimeMillis() - lastSwingTime;

                    double dx = targetWhenSwang.getX() - soundPacket.getX();
                    double dy = targetWhenSwang.getY() - soundPacket.getY();
                    double dz = targetWhenSwang.getZ() - soundPacket.getZ();

                    double dist = dx * dx + dy * dy + dz * dz;

                    if (time < 150 && dist < 16) {

                        Apex.sendChatMessage("§aAttack confirmed on " + targetWhenSwang.getName().getString());

                        swingPendingConfirm = false;
                        hasMissed = false;
                        didReleased = false;

                        targetWhenSwang = null;
                    }
                }
            }

            if (swingPendingConfirm && !hasMissed
                    && System.currentTimeMillis() - lastSwingTime > 150) {

                hasMissed = true;
                swingPendingConfirm = false;

                if (!didReleased && targetWhenSwang != null) {

                    Apex.sendChatMessage("§cSwing missed on " + targetWhenSwang.getName().getString());

                    didReleased = true;

                    releasePackets(true);
                }

                targetWhenSwang = null;
            }
        }

        if (packet instanceof PlayerPositionLookS2CPacket) {

            Apex.sendChatMessage("§cFlagged! Disabling speed.");

            Apex.moduleManager.getByClass(SpeedModule.class).toggle();
        }

        if (packet instanceof CommonPingS2CPacket) {

            event.setCancelled(true);

            packetQueue.add(BlinkManager.QueuedPacket.of(packet));
        }
    }


    public static void tick(DisablerModule parent, ClientTickEvent event) {

        releasePackets(false);
    }


    private static void releasePackets(boolean all) {
        packetQueue.removeIf(data -> {
            if (all || data.time() <= System.currentTimeMillis() - 10000L) {
                if (mc.getNetworkHandler() != null) {
                    PacketUtil.handlePacketSilently(data.packet());
                }

                return true;
            }

            return false;
        });
    }
}