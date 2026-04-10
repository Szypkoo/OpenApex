package lol.apex.feature.module.base;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class SubModuleWithParent<P extends Module> extends SubModule {
    private final @NonNull P parent;

    public SubModuleWithParent(@NonNull P parent, String name, @Nullable String description) {
        super(name, description);
        this.parent = parent;
    }

    public SubModuleWithParent(@NonNull P parent, String name) {
        super(name);
        this.parent = parent;
    }

    public SubModuleWithParent(@NonNull P parent, String name, @Nullable String description, @Nullable String category) {
        super(name, description, category);
        this.parent = parent;
    }
}
