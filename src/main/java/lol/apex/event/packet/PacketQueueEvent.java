package lol.apex.event.packet;

import lol.apex.manager.implementation.BlinkManager;
import net.minecraft.network.packet.Packet;

public abstract sealed class PacketQueueEvent {
    protected final BlinkManager.QueuedPacket<?> queued;

    public PacketQueueEvent(BlinkManager.QueuedPacket<?> queued) {
        this.queued = queued;
    }

    public BlinkManager.Action action = BlinkManager.Action.FLUSH;
    public Packet<?> packet() {
        return this.queued.packet();
    }

    public static final class Send extends PacketQueueEvent {
        public Send(BlinkManager.QueuedPacket<?> queued) { super(queued); }
    }

    public static final class Receive extends PacketQueueEvent {
        public Receive(BlinkManager.QueuedPacket<?> queued) { super(queued); }
    }
}