package lol.apex.feature.command.implementation;

import lol.apex.Apex;
import lol.apex.feature.command.base.Command;
import lol.apex.feature.command.base.CommandInfo;
import lol.apex.feature.file.impl.ModulesFile;
import lol.apex.util.game.ChatUtil;

import java.io.File;
import java.util.Arrays;

@CommandInfo(
        name = "config",
        description = "Helps manage your configurations.",
        alias = "c"
)
public class ConfigCommand extends Command {
    @Override
    public void execute(String[] args) {
        if(args.length == 0) {
            ChatUtil.sendCommandErrorMessage("Usage: .config save/load/list/delete");
            return;
        } 

        String sub = args[0].toLowerCase(); 

        switch(sub) {
            case "save": {
                if(args.length < 2) {
                    ChatUtil.sendCommandErrorMessage("Usage: .config save <config name>");
                    return;
                } 

                String name = args[1];
                new ModulesFile(name).saveToFile();
                Apex.sendChatMessage("Successfully saved config " + name + "!");
                break;
            } 

            case "load": {
                if(args.length < 2) {
                    ChatUtil.sendCommandErrorMessage("Usage: .config load <config name>");
                    return;
                }                  

                String name = args[1]; 
                if(doesFileExist(name)) {
                    new ModulesFile(name).loadFromFile();
                    Apex.sendChatMessage("Successfully loaded config " + name + "!");
                } else {
                    ChatUtil.sendCommandErrorMessage("The config profile " + name + " does not exist.");
                }
                break;
            } 

            case "list": {
                listConfigs();
                break;
            } 

            case "delete": {
                if (args.length < 2) {
                    ChatUtil.sendCommandErrorMessage("Usage: .config delete <config name>");
                    return;
                }                      

                String name = args[1]; 
                if (deleteConfig(name)) {
                    Apex.sendChatMessage("Successfully deleted config profile " + name +".");
                } else {
                    ChatUtil.sendCommandErrorMessage("The config " + name + " does not exist.");
                }
                break;
            } 

            default:
                ChatUtil.sendCommandErrorMessage("Usage: .config delete <config name>");
        }
    } 

    private void listConfigs() {
        File dir = ModulesFile.BASE_DIR.resolve("configs/").toFile();

        File[] files = dir.listFiles((d, n) -> n.endsWith(".json")); 
        if(files == null || files.length == 0) {
            ChatUtil.sendCommandErrorMessage("No configs found. ):");
            return;
        }

        Apex.sendChatMessage("Configs:");
        Arrays.stream(files)
        .forEach(f -> Apex.sendChatMessage(f.getName().replace(".json", "")));
    }

    private boolean deleteConfig(String name) {
        File dir = ModulesFile.BASE_DIR.resolve("configs/").toFile();
        File file = new File(dir, name + ".json"); 
        return file.exists() && file.delete();
    } 

    private boolean doesFileExist(String name) {
        File dir = ModulesFile.BASE_DIR.resolve("configs/").toFile();
        File file = new File(dir, name + ".json");
        return file.exists();
    } 
}
