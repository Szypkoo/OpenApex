package lol.apex.feature.file;

import com.google.gson.JsonElement;
import lol.apex.Apex;
import lol.apex.util.CommonUtil;
import lol.apex.util.io.FileUtil;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public abstract class BaseFile<T extends JsonElement> {
    public static final Path BASE_DIR = CommonUtil.getClientDir().toPath();

    private final String name;
    private final Path path;
    private final Class<T> tClass;

    public BaseFile(String name, Class<T> tClass) {
        this.name = name.endsWith(".json") ? name : name + ".json";
        this.path = BASE_DIR.resolve(this.name);
        this.tClass = tClass;
    }

    protected abstract void load(T in);
    protected abstract T save();

    @SneakyThrows
    public T loadFromFile() {
        try {
            var loaded = FileUtil.loadJson(path, tClass);

            if (loaded == null) {
                loaded = tClass.getDeclaredConstructor().newInstance();
            }

            load(loaded);
            return loaded;
        } catch (IOException e) {
            Apex.LOGGER.error("Failed to load {} from file", name, e);
            return tClass.getDeclaredConstructor().newInstance();
        }
    }

    public void saveToFile() {
        try {
            Files.createDirectories(path.getParent());
            FileUtil.saveJson(path, save());

        } catch (IOException e) {
            Apex.LOGGER.error("Failed to save {}", name, e);
        }
    }
}
