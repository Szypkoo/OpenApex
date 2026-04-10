package lol.apex.feature.integration.api;

/**
 * Implemented specifically for support of both ViaFabric and ViaFabricPlus
 */
public abstract class ViaVersionIntegration extends OptionalIntegration {
    /**
     * Gets the current protocol version as an integer.
     * If it {@link #isPresent() isn't present} then it will return the client's native protocol version.
     **/
    public abstract int getProtocolVersion();
}
