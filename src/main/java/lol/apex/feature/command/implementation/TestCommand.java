package lol.apex.feature.command.implementation;

import lol.apex.feature.command.base.Command;
import lol.apex.feature.command.base.CommandInfo;

@CommandInfo(
        name = "test",
        description = "For developers only.",
        alias = "testdev"
)
public class TestCommand extends Command {
    @Override
    public void execute(String[] args) {

    }
}
