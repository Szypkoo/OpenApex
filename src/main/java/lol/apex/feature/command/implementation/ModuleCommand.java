package lol.apex.feature.command.implementation;


import lol.apex.Apex;
import lol.apex.feature.command.base.Command;
import lol.apex.feature.command.base.CommandInfo;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.setting.base.BaseSetting;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.game.ChatUtil;

import java.util.Optional;

@CommandInfo(
        name = "module",
        description = "Changes settings of certain modules from commands!.",
        alias = "m"
)
public class ModuleCommand extends Command {

    @SuppressWarnings("unchecked")
    @Override
    public void execute(String[] args) {
        if (args.length < 3) {
            ChatUtil.sendCommandErrorMessage("Usage: .<module> <setting> <value>");
            return;
        }

        String moduleName = args[0];
        String settingName = args[1];
        String value = args[2];

        Optional<Module> module = Apex.moduleManager.getModuleByName(moduleName);

        if (module.isEmpty()) {
            ChatUtil.sendCommandErrorMessage("Module not found: " + moduleName);
            return;
        }

        BaseSetting<?> targetBaseSetting = null;

        String normalizedInput = normalize(settingName);

        for (BaseSetting<?> baseSetting : module.get().getBaseSettings()) {
            if (normalize(baseSetting.getName()).equals(normalizedInput)) {
                targetBaseSetting = baseSetting;
                break;
            }
        }

        if (targetBaseSetting == null) {
            ChatUtil.sendCommandErrorMessage("Setting not found: " + settingName);
            return;
        }

        try {

            switch (targetBaseSetting) {
                case BoolSetting booleanSetting -> booleanSetting.setValue(Boolean.parseBoolean(value));
                case SliderSetting numberSetting -> {

                    float parsedValue = Float.parseFloat(value);

                    numberSetting.setValue(parsedValue);
                }
                case @SuppressWarnings("all")EnumSetting enumSetting -> {
                    @SuppressWarnings("all")
                    final var current = (Enum) enumSetting.getValue();
                    final var enumClass = current.getDeclaringClass();

                    Enum<?> newValue = null;

                    for (Enum<?> constant : (Enum<?>[]) enumClass.getEnumConstants()) {
                        if (constant.name().equalsIgnoreCase(value)) {
                            newValue = constant;
                            break;
                        }
                    }

                    if (newValue == null) {
                        ChatUtil.sendCommandErrorMessage("Invalid mode");
                        for (Enum<?> constant : (Enum<?>[]) enumClass.getEnumConstants()) {
                            Apex.sendChatMessage("§7- " + constant.name());
                        }
                        return;
                    }

                    enumSetting.setValue(newValue);
                }
                default -> {
                    ChatUtil.sendCommandErrorMessage("Unsupported setting type.");
                    return;
                }
            }

            Apex.sendChatMessage("§aSet §7" + module.get().getName() + " §a" + settingName + " §ato §7" + value);

        } catch (Exception e) {
            ChatUtil.sendCommandErrorMessage("Invalid value for setting.");
        }
    }

    private String normalize(String input) {
        return input
                .replace(" ", "")
                .replace("_", "")
                .toLowerCase();
    }
}
