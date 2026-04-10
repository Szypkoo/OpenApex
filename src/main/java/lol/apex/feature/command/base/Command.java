package lol.apex.feature.command.base;

import org.jspecify.annotations.Nullable;
import java.util.Optional;

public abstract class Command {

    // Accessor methods read values from the annotation
    public String getName() {
        CommandInfo info = this.getClass().getAnnotation(CommandInfo.class);
        return info != null ? info.name() : "unknown";
    }

    public String getDescription() {
        CommandInfo info = this.getClass().getAnnotation(CommandInfo.class);
        return info != null ? info.description() : "";
    }

    public @Nullable String getAlias() {
        CommandInfo info = this.getClass().getAnnotation(CommandInfo.class);
        String alias = info != null ? info.alias() : "";
        return alias.isEmpty() ? null : alias;
    }

    public abstract void execute(String[] args);

    public boolean matches(String input) {
        if(input.equalsIgnoreCase(getName())) return true;
        String alias = getAlias();
        return alias != null && input.equalsIgnoreCase(alias);
    }
}