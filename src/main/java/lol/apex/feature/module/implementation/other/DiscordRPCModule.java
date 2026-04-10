package lol.apex.feature.module.implementation.other;

import lol.apex.Apex;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import meteordevelopment.discordipc.DiscordIPC;
import meteordevelopment.discordipc.RichPresence;

import java.time.Instant;

@ModuleInfo(
        name = "DiscordRPC",
        description = "Shows Apex in your discord status.",
        category = Category.OTHER
)
public class DiscordRPCModule extends Module {

    // private BoolSetting states;
    private long clientIdentifier = 1479956590583877806L;

    private Thread rpcThread;

    @Override
    public void onEnable() {
        boolean started = DiscordIPC.start(clientIdentifier, () -> {
        });

        if (!started) {
        //    Apex.sendChatMessage("Failed to connect to Discord RPC. is discord open?");
            Apex.notificationRenderer.push("Discord RPC", "Failed to Connect to Discord.");

            onDisable();
            return;
        }

        rpcThread = new Thread(() -> {
            RichPresence presence = new RichPresence();
            presence.setLargeImage("b", Apex.WEBSITE);
            presence.setStart(Instant.now().getEpochSecond());

            String server = "Playing on " + getServerIp();
            String modules = getEnabledModules() + "/" + getTotalModules() + " modules enabled";


            while (enabled()) {

                presence.setDetails(server);
                presence.setState(modules);

                DiscordIPC.setActivity(presence);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Discord RPC Thread");
        rpcThread.start();
    }

    @Override
    public void onDisable() {
        DiscordIPC.stop();
    }

    private String getServerIp() {
        if (mc.isInSingleplayer()) {
            return "Singleplayer";
        } else if (mc.getCurrentServerEntry() != null) {
            return mc.getCurrentServerEntry().address;
        } else {
            return "Apex Client. Idling";
        }
    }

    private int getTotalModules() {
        return Math.toIntExact(Apex.moduleManager.stream().count());
    }

    private int getEnabledModules() {
        return (int) Apex.moduleManager
                .stream()
                .filter(Module::enabled)
                .count();
    }
}
 