package lol.apex.feature.module.implementation.player;

import lol.apex.Apex;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.TextInputSetting;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

@ModuleInfo(
        name = "AutoAuth",
        description = "Automatically authenticates you on servers with login plugins.",
        category = Category.PLAYER
)
public class AutoAuthModule extends Module {

    private static final String[] REGISTER_KEYWORDS = {
            "/register",    // English
            "/registrar",   // Spanish/Portuguese
            "/reg",         // Short form
            "/зарег",       // Russian (zareg)
            "/rejestracja", // Polish
            "/cadastrar",   // Portuguese
            "/kayit",       // Turkish
            "/enregistrer"  // French
    };

    private static final String[] LOGIN_KEYWORDS = {
            "/login",
            "/войти",
            "/ingresar",
            "/connexion"
    };

    private boolean registered, loggedIn;

    public final TextInputSetting passwordInput = new TextInputSetting("Password", "bigcoolpassword100", 100);

    private String password = passwordInput.getValue();

    @EventHook
    public void onPacket(PacketEvent.Receive event) {
        if (mc.player == null) return;

        if (!(event.getPacket() instanceof GameMessageS2CPacket packet)) return;
        String message = packet.content().getString().toLowerCase();

        if (!registered) {
            for (String keyword : REGISTER_KEYWORDS) {
                if (message.contains(keyword)) {
                    mc.player.networkHandler.sendChatCommand(
                            keyword.substring(1) + " " +
                                    password + " " +
                                    password
                    );
                    registered = true;
                    Apex.sendChatMessage("Successfully registered!");
                    toggle();
                    return;
                }
            }
        }

        for (String keyword : LOGIN_KEYWORDS) {
            if (message.contains(keyword)) {
                mc.player.networkHandler.sendChatCommand(
                        "login " + password
                );
                loggedIn = true;
                Apex.sendChatMessage("Successfully logged in!");
                return;
            }
        }
    }

    @Override
    public void onDisable() {
        registered = false;
        loggedIn = false;
    }
}

