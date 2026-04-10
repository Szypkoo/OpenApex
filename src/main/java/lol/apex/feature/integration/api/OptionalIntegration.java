package lol.apex.feature.integration.api;

/**
 * An optional integration which depends on something.
 */
public abstract class OptionalIntegration {
    /**
     * @return if the mod required for the integration is present.
     */
    public abstract boolean isPresent();
}
