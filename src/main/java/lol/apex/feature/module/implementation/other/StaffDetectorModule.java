package lol.apex.feature.module.implementation.other;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.Apex;
import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.util.CommonUtil;

import lol.apex.feature.module.base.Module;
import lombok.SneakyThrows;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import org.intellij.lang.annotations.Language;
import org.jspecify.annotations.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@ModuleInfo(
        name = "StaffDetector",
        description = "Automatically detects staff that are in your game or lobby.",
        category = Category.OTHER
)
public class StaffDetectorModule extends Module {
    public final BoolSetting kryptic_hub = new BoolSetting("Kryptic Auto Hub", true);
    public final BoolSetting warning = new BoolSetting("Warning Sound", true);
    private final List<String> staffs = new ArrayList<>();

    @Override
    public void onEnable() {
        super.onEnable();
        load();
    }

    @EventHook
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof PlayerListS2CPacket packet) {
            if(packet.getActions().contains(PlayerListS2CPacket.Action.ADD_PLAYER)) {
                for (PlayerListS2CPacket.Entry entry : packet.getPlayerAdditionEntries()) {
                    String name = entry.profile().name();

                    if(staffs.contains(name)) {
                        Apex.sendChatMessage("Staff Detector has detected " + name + " as staff!");

                        if(warning.getValue()) {
                            CommonUtil.warningSound();
                        }

                        if (kryptic_hub.getValue()) {
                            mc.player.networkHandler.sendChatMessage("/hub");
                            Apex.sendChatMessage("Automatically went to hub.");
                        }
                    }
                }
            }
        }
    }

    private static @Language("http-url-reference") @NonNull String coreGithubURL(@Language("http-url-reference") String fileName) {
        return "https://raw.githubusercontent.com/CCBlueX/LiquidCloud/refs/heads/main/LiquidBounce/staffs/" + fileName;
    }

    private String rootDomain(String of) {
        var domain = of.trim().toLowerCase();

        // Check if domain ends with dot, if so, remove it
        if (domain.endsWith(".")) {
            domain = domain.substring(0, -1);
        }

        final var parts = domain.split("\\.");
        if (parts.length <= 2) {
            // Already a root domain
            return domain;
        }

        return parts[(parts.length - 1) - 1] + "." + parts[parts.length - 1];
    }

    @SneakyThrows
    private static URL url(@Language("http-url-reference") String x) {
        return new URI(x).toURL();
    }

    private void load() {
        staffs.clear();
        final var curServer = mc.getCurrentServerEntry();
        if (curServer == null) return;
        final var addr = curServer.address;
        @Language("http-url-reference") final var addrNormalized = rootDomain(dropPort(addr));

        @Language("http-url-reference") final var urlString = coreGithubURL(addrNormalized);

        new Thread(() -> {
            try {
                URL url = url(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int status = connection.getResponseCode();
                if (status != 200) {
                    Apex.sendChatMessage("Failed to download staff list: HTTP " + status);
                    return;
                }

                try (final var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    staffs.addAll(reader.lines().toList());
                }
            } catch (IOException e) {
                Apex.sendChatMessage("Error downloading staff list: " + e.getMessage());
            }
        }).start();
    }

    private static @NonNull String dropPort(@NonNull String addr) {
        final var idx = addr.indexOf(':');
        if (idx == -1) return addr;
        return addr.substring(0, idx);
    }
}
