package lol.apex.feature.module.base;

public interface Togglable {
    boolean enabled();
    void enabled(boolean enabled);
    default void toggle() {
        this.enabled(!this.enabled());
    }
}
