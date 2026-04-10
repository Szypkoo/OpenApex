package lol.apex.feature.command.implementation;

import lol.apex.Apex;
import lol.apex.feature.command.base.Command;
import lol.apex.feature.command.base.CommandInfo;
import lol.apex.feature.module.base.Module;
import lol.apex.util.game.ChatUtil;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

@CommandInfo(
        name = "bind",
        description = "Binds a module to a key.",
        alias = "b"
)
public class BindCommand extends Command {
    @Override
    public void execute(String[] args) {
        if(args.length == 0) {
            ChatUtil.sendCommandErrorMessage("Usage: bind <module> <key> / bind remove <module> / bind list / bind purge");
            return;
        }

        switch(args[0].toLowerCase()) {
            case "remove" -> {
                if(args.length < 2) {
                    ChatUtil.sendCommandErrorMessage("Usage: .bind remove <module>");
                    return;
                }

                Optional<Module> module = Apex.moduleManager.getModuleByName(args[1]);

                module.ifPresentOrElse(m -> {
                    m.setKey(-1);
                    ChatUtil.sendChatMessage("Removed " + m.getName() + " bind");

                }, () -> ChatUtil.sendCommandErrorMessage("Module not found."));
            }
            case "list" -> {
                Apex.sendChatMessage("§7Binds:");

                for (Module module : Apex.moduleManager) {

                    int key = module.getKey();

                    if (key == -1) continue;

                    String keyName = getKeyName(key);

                    if (keyName.equals("UNKNOWN")) continue; // <-- filter bad keys

                    Apex.sendChatMessage(
                            "§f" + module.getName() + " §8-> §7" + keyName
                    );
                }
            }

            case "purge" -> {
                for (Module module : Apex.moduleManager) {
                    module.setKey(-1);
                }
                Apex.sendChatMessage("Cleared all binds");
            }

            default -> {
                if (args.length < 2) {
                    ChatUtil.sendCommandErrorMessage("Usage: bind <module> <key>");
                    return;
                }

                String keyStr = args[args.length - 1];

                StringBuilder moduleNameBuilder = new StringBuilder();
                for (int i = 0; i < args.length - 1; i++) {
                    moduleNameBuilder.append(args[i]).append(" ");
                }

                String moduleName = moduleNameBuilder.toString().trim();
                Optional<Module> module = Apex.moduleManager.getModuleByName(moduleName);

                module.ifPresentOrElse(m -> {
                    int key = getKeyFromStr(keyStr);

                    if (key == -1) {
                        ChatUtil.sendCommandErrorMessage("Invalid key!");
                        return;
                    }

                    m.setKey(key);

                    Apex.sendChatMessage(
                            "§7Bound §f" + m.getName() + " §7to §f" + keyStr.toUpperCase()
                    );

                }, () -> ChatUtil.sendCommandErrorMessage("Module not found."));
            }
        }
    }

    private String getKeyName(int key) {
        String name = GLFW.glfwGetKeyName(key, 0);

        if (name != null) {
            return name.toUpperCase();
        }

        // special keys
        return switch (key) {
            case GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT -> "SHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL -> "CTRL";
            case GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT -> "ALT";
            case GLFW.GLFW_KEY_SPACE -> "SPACE";
            case GLFW.GLFW_KEY_ENTER -> "ENTER";
            case GLFW.GLFW_KEY_TAB -> "TAB";
            case GLFW.GLFW_KEY_BACKSPACE -> "BACKSPACE";
            case GLFW.GLFW_KEY_ESCAPE -> "ESC";
            default -> "UNKNOWN";
        };
    }

    private int getKeyFromStr(String key) {
        try {
            return GLFW.class.getField("GLFW_KEY_" + key.toUpperCase()).getInt(null);
        }catch (Exception e) {
            return -1;
        }
    }
}
