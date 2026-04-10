package lol.apex.mixin;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@Mixin(PlayerMoveC2SPacket.class)
public interface PlayerMoveC2SPacketAccessor {
    @Accessor("x")
    double getX();

    @Accessor("y")
    double getY();

    @Accessor("z")
    double getZ();

    @Accessor("yaw")
    float getYaw();

    @Accessor("pitch")
    float getPitch();

    @Mutable
    @Accessor("x")
    void setX(double newX);

    @Mutable
    @Accessor("y")
    void setY(double newY);

    @Mutable
    @Accessor("z")
    void setZ(double newZ);

    @Mutable
    @Accessor("yaw")
    void setYaw(float newYaw);

    @Mutable
    @Accessor("pitch")
    void setPitch(float newPitch);

    @Mutable
    @Accessor("onGround")
    void setOnGround(boolean onGround);

    @Accessor("onGround")
    boolean getOnGround();

}