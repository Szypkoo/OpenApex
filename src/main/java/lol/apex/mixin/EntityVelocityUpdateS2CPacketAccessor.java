package lol.apex.mixin;

import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

@Mixin(EntityVelocityUpdateS2CPacket.class)
public interface EntityVelocityUpdateS2CPacketAccessor {

    @Accessor("velocity")
    void setVelocity(Vec3d velocity);

}