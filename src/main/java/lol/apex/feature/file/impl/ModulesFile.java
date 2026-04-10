package lol.apex.feature.file.impl;

import com.google.gson.JsonObject;
import lol.apex.Apex;
import lol.apex.feature.file.BaseFile;
import lol.apex.feature.module.base.Module;

public class ModulesFile extends BaseFile<JsonObject> {
    public static final ModulesFile DEFAULT = new ModulesFile("default.json");

    public ModulesFile(String name) {
        super("Configs/" + name, JsonObject.class);
    }

    @Override
    public void load(JsonObject in) {
        for (Module module : Apex.moduleManager) {
            if (in.has(module.getName())) {
                module.fromJson(in.get(module.getName()));
            }
        }
    }

    @Override
    public JsonObject save() {
        var object = new JsonObject();
        for (Module module : Apex.moduleManager) {
            object.add(module.getName(), module.toJson());
        }

        if (object.isEmpty()) {
            Apex.LOGGER.warn("Nothing to save for {}", getName());
        }
        return object;
    }
}
