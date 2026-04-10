package lol.apex.mixin;

import lol.apex.Apex;
import lol.apex.event.packet.PacketEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;

@Mixin(ClientCommonNetworkHandler.class)
public class SendPacketMixin {
    @Final @Shadow
    protected ClientConnection connection; 

    @Unique
    public void sendPacketDirect(Packet<?> packet) {
        this.connection.send(packet);
    } 

    @Inject(at = @At("HEAD"), method = "sendPacket", cancellable = true) 
    private static void onSendPacket(Packet<?> packet, CallbackInfo info) {
        PacketEvent.Send event = new PacketEvent.Send(packet);
        Apex.eventBus.post(event);

        if(event.isCancelled()) info.cancel();
    } 
}
