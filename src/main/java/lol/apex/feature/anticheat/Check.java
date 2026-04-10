package lol.apex.feature.anticheat;

import lol.apex.Apex;
import lol.apex.event.client.ClientPostEvent;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.client.PreUpdateEvent;
import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.implementation.other.AntiCheatModule;
import lol.apex.util.game.ChatUtil;
import lol.apex.util.math.TimerUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;

@AllArgsConstructor
public class Check {
    @Getter
    private final PlayerEntity player;
    public String checkName;

    public void onSendPacket(PacketEvent.Send event) {

    }

    public void onReceivePacket(PacketEvent.Receive event) {

    }

    public void onTickPost(ClientPostEvent event) {

    }

    public void onTickPre(ClientTickEvent event) {

    }

    public void onPreUpdate(PreUpdateEvent event) {

    }

    private final TimerUtil delayTimer = new TimerUtil();

    public void flag(String message) {
        if(delayTimer.passed(500L, true)) {
            String formattedMessage =
                    "§c" + player.getGameProfile().name() +
                            "§f flagged: [" +
                            "§b" + checkName +
                            "§f] " +
                            "§7(" + message + ")";
            ChatUtil.sendACMessage(formattedMessage);
            delayTimer.reset(); // >:( I DONT CARE IF IT ALREADY RESETS ON PASS
        }
    }

    public boolean shouldCheckSelf() {
        return Apex.moduleManager.getByClass(AntiCheatModule.class).checkSelf.getValue();
    }
}
