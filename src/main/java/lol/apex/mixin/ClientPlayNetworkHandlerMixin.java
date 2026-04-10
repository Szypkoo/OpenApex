package lol.apex.mixin;


import lol.apex.Apex;
import lol.apex.event.packet.SendMessageEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler {
    @Shadow
    public abstract void sendChatMessage(String ctx);

    @Unique
    private boolean ignoreChatMessage;

    protected ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState state) {
        super(client, connection, state);
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo info) {
        if (ignoreChatMessage) return;
        boolean handled = Apex.commandManager.handleChatMsg(message);

        if (handled) {
            info.cancel();
            return;
        }

        SendMessageEvent event = new SendMessageEvent(message);
        Apex.eventBus.post(event);

        if (event.isCancelled()) {
            info.cancel();
            return;
        }

        if (!event.message.equals(message)) {
            ignoreChatMessage = true;
            sendChatMessage(event.message);
            ignoreChatMessage = false;
            info.cancel();
        }
    }
}