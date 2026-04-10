package lol.apex.feature.anticheat;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.Apex;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.anticheat.checks.combat.AutoBlockA;
import lol.apex.feature.anticheat.checks.combat.AutoClickerA;
import lol.apex.feature.anticheat.checks.combat.AutoSoupA;
import lol.apex.feature.anticheat.checks.combat.FastBowA;
import lol.apex.feature.anticheat.checks.movement.NoSlowA;
import lol.apex.feature.anticheat.checks.rotation.InvalidPitchCheck;
import lol.apex.feature.module.implementation.other.AntiCheatModule;
import lol.apex.util.CommonVars;
import net.minecraft.client.network.AbstractClientPlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class AntiCheatManager implements CommonVars {
    private final Map<AbstractClientPlayerEntity, List<Check>> playerChecks =
            new HashMap<>();

    private void initializeChecks(AbstractClientPlayerEntity player) {
        if(!playerChecks.containsKey(player)) {
            List<Check> checks = new ArrayList<>();

            // Combat
            checks.add(new AutoClickerA(player));
            checks.add(new AutoBlockA(player));
            checks.add(new AutoSoupA(player));
            checks.add(new FastBowA(player));

            // rotation
            checks.add(new InvalidPitchCheck(player));

            // movement
            checks.add(new NoSlowA(player));

            playerChecks.put(player, checks);
        }
    }

    @EventHook
    public void onTick(ClientTickEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        List<AbstractClientPlayerEntity> players = getPlayers();
        for (AbstractClientPlayerEntity player : players) {
            List<Check> checks = playerChecks.get(player);
            if (checks != null) {
                for (Check check : checks) {
                    check.onTickPre(event);
                }
            }
        }
    }

    @EventHook
    public void onPacket(PacketEvent.Send event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if(!isAntiCheatEnabled()) {
            return;
        }

        List<AbstractClientPlayerEntity> players = getPlayers();
        for (AbstractClientPlayerEntity player : players) {
            List<Check> checks = playerChecks.get(player);
            if (checks != null) {
                for (Check check : checks) {
                    check.onSendPacket(event);
                }
            }
        }
    }

    @EventHook
    public void onPacket(PacketEvent.Receive event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if(!isAntiCheatEnabled()) {
            return;
        }

        List<AbstractClientPlayerEntity> players = getPlayers();
        for (AbstractClientPlayerEntity player : players) {
            List<Check> checks = playerChecks.get(player);
            if (checks != null) {
                for (Check check : checks) {
                    check.onReceivePacket(event);
                }
            }
        }
    }

    public List<AbstractClientPlayerEntity> getPlayers() {
        List<AbstractClientPlayerEntity> players = new CopyOnWriteArrayList<>();
        List<AbstractClientPlayerEntity> worldPlayers = new ArrayList<>(mc.world.getPlayers());

        for (AbstractClientPlayerEntity abstractClientPlayerEntity : worldPlayers) {
            if (abstractClientPlayerEntity == null) {
                continue;
            }

            if(!isAntiCheatEnabled()) {
                continue;
            }

            players.add(abstractClientPlayerEntity);
            initializeChecks(abstractClientPlayerEntity);
        }
        return players;
    }

    public boolean isAntiCheatEnabled() {
        return Apex.moduleManager.getByClass(AntiCheatModule.class).enabled();
    }
}
