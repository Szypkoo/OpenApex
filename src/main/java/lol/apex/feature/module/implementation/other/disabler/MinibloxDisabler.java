package lol.apex.feature.module.implementation.other.disabler;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.SubModule;

public class MinibloxDisabler extends SubModule {
    public MinibloxDisabler() {
        super("Miniblox");
    }

    public static void onTick(ClientTickEvent event) {
    //    PacketUtil.sendPacket(new PlayerInputC2SPacket(mc.player.input.playerInput));

    }
}
