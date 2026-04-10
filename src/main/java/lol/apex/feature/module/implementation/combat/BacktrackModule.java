package lol.apex.feature.module.implementation.combat;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.packet.PacketQueueEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.manager.implementation.BlinkManager;
import lol.apex.util.player.PlayerUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.*;

@ModuleInfo(
        name = "Backtrack",
        description = "Allows you to hit players from their previous position.",
        category = Category.COMBAT
)
public final class BacktrackModule extends Module {

    @EventHook
    public void onPacket(PacketQueueEvent.Receive e) {
        if (mc.player == null) return;
        final var player = mc.player;
        e.action = switch (e.packet()) {
            case EntityPositionS2CPacket p -> {
                if (p.entityId() == player.getId()) yield BlinkManager.Action.PASS;
                final var target = mc.world.getEntityById(p.entityId());
                if (!(target instanceof LivingEntity)) yield BlinkManager.Action.PASS;
                final var pos = mc.player.getEyePos();
                final var serverDist = p.change().position().distanceTo(pos);
                final var clientDist = PlayerUtil.getBiblicallyAccurateDistanceToEntity(target);
                if (serverDist <= clientDist) {
                    yield BlinkManager.Action.PASS;
                }
                yield serverDist >= 6 ? BlinkManager.Action.PASS : BlinkManager.Action.QUEUE;
            }
            case PlayerPositionLookS2CPacket ignored -> BlinkManager.Action.FLUSH;
            case PlayerSpawnPositionS2CPacket ignored -> BlinkManager.Action.QUEUE;
            case PlayerRotationS2CPacket ignored -> BlinkManager.Action.FLUSH;
            case EntityPositionSyncS2CPacket p -> {
                if (p.id() == player.getId()) yield BlinkManager.Action.PASS;
                final var target = mc.world.getEntityById(p.id());
                if (!(target instanceof LivingEntity)) yield BlinkManager.Action.PASS;
                final var serverDist = p.values().position().distanceTo(mc.player.getEyePos());
                final var clientDist = PlayerUtil.getBiblicallyAccurateDistanceToEntity(target);
                if (serverDist <= clientDist) {
                    yield BlinkManager.Action.PASS;
                }
                yield serverDist >= 6 ? BlinkManager.Action.PASS : BlinkManager.Action.QUEUE;
            }
            case EntityTrackerUpdateS2CPacket p -> {
                if (p.id() == player.getId()) yield BlinkManager.Action.PASS;
                final var target = mc.world.getEntityById(p.id());
                if (!(target instanceof LivingEntity)) yield BlinkManager.Action.PASS;
                yield BlinkManager.Action.PASS;
            }
            case EntitySetHeadYawS2CPacket p -> BlinkManager.Action.PASS;
            case EntityS2CPacket p -> {
                final var target = p.getEntity(mc.world);
                if (target == player || !(target instanceof LivingEntity)) yield BlinkManager.Action.PASS;
                yield p.isPositionChanged() ? BlinkManager.Action.QUEUE : BlinkManager.Action.PASS;
            }
            case HealthUpdateS2CPacket p -> p.getHealth() <= 0 ? BlinkManager.Action.FLUSH : BlinkManager.Action.PASS;
//            case EntityVelocityUpdateS2CPacket p -> BlinkManager.Action.QUEUE;
//            case ChatMessageS2CPacket ignored -> e.action = BlinkManager.Action.PASS;
//            case PlaySoundS2CPacket ignored -> e.action = BlinkManager.Action.PASS;
//            case StopSoundS2CPacket ignored -> e.action = BlinkManager.Action.PASS;
            default -> BlinkManager.Action.PASS;
        };
    }
}
