package lol.apex.mixin;

import io.netty.channel.ChannelPipeline;
import lol.apex.Apex;
import lol.apex.event.packet.PacketEvent;
import lol.apex.event.packet.PipelineEvent;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.handler.PacketSizeLogger;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static void onReceivePacket(Packet<?> packet, PacketListener listener, CallbackInfo info) {
        PacketEvent.Receive event = new PacketEvent.Receive(packet);
        Apex.eventBus.post(event);

        if(event.isCancelled()) info.cancel();
    }

    /**
     * Hook proxy
     */
    @Inject(method = "addHandlers", at = @At("HEAD"))
    private static void hookProxy(ChannelPipeline pipeline, NetworkSide side, boolean local, PacketSizeLogger packetSizeLogger, CallbackInfo ci) {
        if (side == NetworkSide.CLIENTBOUND) {
            final var event = new PipelineEvent(pipeline, local);
            Apex.eventBus.post(event);
        }
    }
}