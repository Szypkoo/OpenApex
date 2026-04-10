package lol.apex.feature.command.implementation;

import lol.apex.Apex;
import lol.apex.feature.command.base.Command;
import lol.apex.feature.command.base.CommandInfo;
import lol.apex.util.game.ChatUtil;

import java.util.Optional;
import lol.apex.feature.module.base.Module;

@CommandInfo(
        name = "toggle",
        description = "Allows you to quickly toggle certain modules",
        alias = "t"
)
public class ToggleCommand extends Command {

    @Override
    public void execute(String[] args) {
        if(args.length < 1) {
            ChatUtil.sendCommandErrorMessage("Usage: .toggle <module name>");
            return;
        }

        String moduleName = args[0];
        Optional<Module> module = Apex.moduleManager.getModuleByName(moduleName);

        Module targetModule = module.get();

        targetModule.toggle();

     //   String status = targetModule.isEnabled() ? "enabled" : "disabled";
     //   Apex.sendChatMessage("§a" + targetModule.getName() + " has been §7" + status);
    }
}
