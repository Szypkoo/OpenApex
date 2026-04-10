package lol.apex.manager.implementation;

import lol.apex.Apex;
import lol.apex.feature.command.base.Command;
import lol.apex.feature.command.implementation.*;
import lol.apex.manager.MapManager;

import java.util.Arrays;
import java.util.function.Supplier;

public final class CommandManager extends MapManager<String, Command> {

    private static final String prefix = ".";

    public CommandManager() {
        this.addCommands(
                HelpCommand::new,
                BindCommand::new,
                ConfigCommand::new,
                ModuleCommand::new,
                FriendCommand::new,
                ToggleCommand::new
           //     TestCommand::new
        );
    }

    @SafeVarargs
    public final void addCommands(Supplier<Command>... commands) {
        for(Supplier<Command> command : commands) {
            this.addCommand(command.get());
        }
    }

    public void addCommand(Command command) {
        this.put(command.getName().toLowerCase(), command);
    }

    public boolean processCommand(String input) {
        if(input == null || input.isBlank()) return false;
        if(!input.startsWith(prefix)) return false;

        input = input.substring(prefix.length()).trim();
        String[] parts = input.split("\\s+");
        String commandName = parts[0];
        Command command = null;

        for(Command cmd : this) {
            if(cmd.matches(commandName)) {
                command = cmd;
                break;
            }
        }

        if(command == null) {
            Apex.sendChatMessage("Unknown Command " + commandName + " use .help for a list of commands.");
            return true;
        }

        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        command.execute(args);
        return true;
    }

    public boolean handleChatMsg(String message) {
        return message.startsWith(prefix) && processCommand(message);
    }
}
