package lol.apex.feature.module.implementation.other;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.packet.PacketEvent;
import lol.apex.event.packet.SendMessageEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.implementation.other.disabler.*;
import lol.apex.feature.module.setting.implementation.BoolSetting;

@ModuleInfo(
        name = "Disabler",
        description = "Disables certain anti-cheat checks by using exploits.",
        category = Category.OTHER
)
public class DisablerModule extends Module {
    public final BoolSetting vulcanFastBow = new BoolSetting("Vulcan (FastBow)", false);
    public final BoolSetting vulcanScaffold = new BoolSetting("Vulcan (Scaffold)", false);

    public final BoolSetting cubecraft = new BoolSetting("CubeCraft", false);
    public final BoolSetting chatFilter = new BoolSetting("Chat Filter", false);
    public final BoolSetting abilities = new BoolSetting("Abilities", false);
    public final BoolSetting miniblox = new BoolSetting("Miniblox", false);
    public final BoolSetting sprint = new BoolSetting("Sprint", false);

    @EventHook
    public void onSendMessage(SendMessageEvent e) {
        if (chatFilter.getValue()) {
            ChatFilterDisabler.onSendMessage(e);
        }
    }

    @EventHook
    public void onTick(ClientTickEvent event) {
        if (vulcanFastBow.getValue()) {
            VulcanFastBowDisabler.onTick(this, event);
        }

        if (vulcanScaffold.getValue()) {
            VulcanScaffoldDisabler.onTick(event);
        }

        if (cubecraft.getValue()) {
            CubeCraftDisabler.tick(this, event);
        }

        if (miniblox.getValue()) {
            MinibloxDisabler.onTick(event);
        }

    }

    @EventHook
    public void onPacketSend(PacketEvent.Send event) {
        if (cubecraft.getValue()) {
            CubeCraftDisabler.onSend(event);
        }

        if (abilities.getValue()) {
            AbilitiesDisabler.onPacketSend(event);
        }

        if (sprint.getValue()) {
            SprintDisabler.onPacket(event);
        }

    }

    @EventHook
    public void onPacketReceive(PacketEvent.Receive event) {

        if (cubecraft.getValue()) {
            CubeCraftDisabler.onReceive(event);
        }
    }
}
