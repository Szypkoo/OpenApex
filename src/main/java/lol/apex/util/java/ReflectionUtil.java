package lol.apex.util.java;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.Nullable;

@UtilityClass
public final class ReflectionUtil {
    public static boolean classExists(String name) {
        return getOptClass(name) != null;
    }

    public static @Nullable Class<?> getOptClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
