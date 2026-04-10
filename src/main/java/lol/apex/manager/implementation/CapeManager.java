package lol.apex.manager.implementation;

import lol.apex.Apex;
import lol.apex.feature.module.implementation.visual.CapesModule;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

import static lol.apex.util.CommonVars.mc;

// next code is in PlayerListEntryMixin

public class CapeManager {
    private static final Map<String, String> capeMapping = new HashMap<>();
    private static Identifier customCape;

    public static void initialize() {

        // I added this for when we add social aspects to the client
        // like we can show a cape on there name,
        // with there client name or something
        addCape(mc.player.getName().toString(), "apex");
    }

    public static void addCape(String playerName, String id) {
        capeMapping.put(playerName.toLowerCase(), id);
    }

    public static Identifier getCapeForUser(String playerName) {
        if (Apex.moduleManager.getByClass(CapesModule.class) != null
                && CapesModule.capeId != null
                && playerName.equals(mc.getSession().getUsername())) {

            customCape = Identifier.of(
                    Apex.MOD_ID,
                    "textures/capes/" + CapesModule.capeId + ".png"
            );
            return customCape;
        }

        String cape = capeMapping.get(playerName.toLowerCase());
        if (cape != null) {
            customCape = Identifier.of(
                    Apex.MOD_ID,
                    "textures/capes/" + cape + ".png"
            );
            return customCape;
        }

        return null;
    }

}
