package lol.apex.manager.implementation;

import dev.toru.clients.eventBus.EventHook;
import dev.toru.clients.eventBus.EventPriority;
import lol.apex.Apex;
import lol.apex.event.packet.PacketEvent;
import lol.apex.event.packet.PacketQueueEvent;
import lol.apex.util.CommonVars;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import lol.apex.util.game.PacketUtil;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.*;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

public final class BlinkManager implements CommonVars {
    public record QueuedPacket<T extends PacketListener>(
            @NonNull Packet<T> packet,
            long time
    ) {
        @Contract("_ -> new")
        public static <T extends PacketListener> @NonNull QueuedPacket<T> of(@NonNull Packet<T> packet) {
            return new QueuedPacket<>(packet, System.currentTimeMillis());
        }
    }

    public enum Action {
        /**
         * bypass the queue entirely and just send it directly to the server.
         */
        PASS,
        /**
         * send all held packets and this one.
         **/
        FLUSH,
        /**
         * start queueing packets.
         **/
        QUEUE
    }
    /** restricted version of {@link Action}. **/
    private enum BaselineAction {
        /** @see Action#PASS **/
        PASS,
        /** @see Action#FLUSH **/
        FLUSH,
        /** send the event **/
        GO
    }

    private final List<QueuedPacket<?>> c2sQueue = new ArrayList<>();
    private final List<QueuedPacket<?>> s2cQueue = new ArrayList<>();

    public long lagTimeC2S() {
        if (c2sQueue.isEmpty()) return 0L;
        final var q = c2sQueue.getFirst();
        return System.currentTimeMillis() - q.time();
    }

    @SuppressWarnings("unused")
    public long lagTimeS2C() {
        if (s2cQueue.isEmpty()) return 0L;
        final var q = s2cQueue.getFirst();
        return System.currentTimeMillis() - q.time();
    }

    private static @NonNull BaselineAction baselineAction(Packet<?> packet) {
        return switch (packet) {
            case HandshakeC2SPacket ignored -> BaselineAction.PASS;
            case QueryPingC2SPacket ignored -> BaselineAction.PASS;
            case QueryRequestC2SPacket ignored -> BaselineAction.PASS;
            case ChatMessageC2SPacket ignored -> BaselineAction.PASS;
            case CommandExecutionC2SPacket ignored -> BaselineAction.PASS;
            case GameMessageS2CPacket ignored -> BaselineAction.PASS;

            case PlayerPositionLookS2CPacket ignored -> BaselineAction.FLUSH;
            case DisconnectS2CPacket ignored -> BaselineAction.FLUSH;
            case PlaySoundS2CPacket ignored -> BaselineAction.PASS;
            case StopSoundS2CPacket ignored -> BaselineAction.PASS;
            case HealthUpdateS2CPacket p -> p.getHealth() <= 0 ? BaselineAction.FLUSH : BaselineAction.PASS;
            default -> BaselineAction.GO;
        };
    }

    /** @return cancel the event? **/
    private <T extends PacketListener> boolean handlePacket(
            QueuedPacket<T> queuedPacket,
            Function<QueuedPacket<T>, PacketQueueEvent> getEvent,
            Runnable flush,
            Consumer<QueuedPacket<T>> add
    ) {
        final var e = getEvent.apply(queuedPacket);
        Apex.eventBus.post(e);

        switch (e.action) {
            // don't do anything, just pass it on.
            case PASS -> {}
            // flush ts
            case FLUSH -> flush.run();
            // queue it
            case QUEUE -> {
                add.accept(queuedPacket);
                return true;
            }
        }
        return false;
    }

    private <T extends PacketListener> boolean handlePacketSend(QueuedPacket<T> packet) {
        return handlePacket(packet, PacketQueueEvent.Send::new, this::flushC2S, c2sQueue::add);
    }
    private <T extends PacketListener> boolean handlePacketReceive(QueuedPacket<T> packet) {
        return handlePacket(packet, PacketQueueEvent.Receive::new, this::flushS2C, s2cQueue::add);
    }

    @SuppressWarnings("unused")
    @EventHook(priority = EventPriority.LOWEST)
    private void onPacketSend(PacketEvent.Send event) {
        final var packet = event.getPacket();
        switch (baselineAction(packet)) {
            case PASS -> {}
            case FLUSH -> flushC2S();
            case GO -> event.setCancelled(handlePacketSend(QueuedPacket.of(packet)));
        }
    }

    @SuppressWarnings("unused")
    @EventHook(priority = EventPriority.LOWEST)
    private void onPacketReceive(PacketEvent.Receive event) {
        final var packet = event.getPacket();
        switch (baselineAction(packet)) {
            case PASS -> {}
            case FLUSH -> flushS2C();
            case GO -> event.setCancelled(handlePacketReceive(QueuedPacket.of(packet)));
        }
    }


    public void flushC2S() {
        synchronized (c2sQueue) {
            for (QueuedPacket<?> q : c2sQueue) {
                PacketUtil.sendSilentPacket(q.packet);
            }
            c2sQueue.clear();
        }
    }

    public void flushS2C() {
        if (mc.getNetworkHandler() == null) return;

        synchronized (s2cQueue) {
            for (QueuedPacket<?> q : s2cQueue) {
                mc.execute(() -> PacketUtil.handlePacketSilently(q.packet));
            }
            s2cQueue.clear();
        }
    }
}