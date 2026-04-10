package lol.apex.feature.command.implementation;

import lol.apex.Apex;
import lol.apex.feature.command.base.Command;
import lol.apex.feature.command.base.CommandInfo;

@CommandInfo(
        name = "help",
        description = "HELP MEEEE!!!!!!.",
        alias = "h"
)
public class HelpCommand extends Command {

    @Override
    public void execute(String[] args) {
        Apex.sendChatMessage("Commands: ");
        for (Command command : Apex.commandManager) {
            Apex.sendChatMessage(
                    "§7" + command.getName() + " §8- §7" + command.getDescription() + " §8[§7" + command.getAlias() + "§8]"
            );
        }
    }
}
