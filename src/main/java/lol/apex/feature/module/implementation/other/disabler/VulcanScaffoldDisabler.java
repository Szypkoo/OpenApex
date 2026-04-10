package lol.apex.feature.module.implementation.other.disabler;

import lol.apex.Apex;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.player.ScaffoldModule;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;

public class VulcanScaffoldDisabler extends SubModule {

    private static boolean state = false;

    public VulcanScaffoldDisabler() {
        super("VulcanScaffold");
    }

    public static void onTick(ClientTickEvent event) {
        var scaffold = Apex.moduleManager.getByClass(ScaffoldModule.class);
        if (!scaffold.enabled()) return;
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        state = !state;

        mc.player.setSneaking(state);
        // idk how to do it with packet

    }
}