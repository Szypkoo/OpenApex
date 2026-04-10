package lol.apex.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.ClientConnection;

@Mixin(ClientCommonNetworkHandler.class)
public interface SendPacketMixinAccessor {
    @Accessor 
    ClientConnection getConnection(); 
}
