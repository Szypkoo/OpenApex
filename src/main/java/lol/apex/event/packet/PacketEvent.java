package lol.apex.event.packet;

import dev.toru.clients.eventBus.Event;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.packet.Packet;

public class PacketEvent {
    @Getter
    @Setter
    public static class Send extends Event {
        private Packet<?> packet;

        public Send(Packet<?> packet) {
            this.packet = packet;
        }
    }

    @Getter
    @Setter
    public static class Receive extends Event {
        private Packet<?> packet;

        public Receive(Packet<?> packet) {
            this.packet = packet;
        }

    }
}