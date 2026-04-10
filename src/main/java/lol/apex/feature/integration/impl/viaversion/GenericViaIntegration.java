package lol.apex.feature.integration.impl.viaversion;

import lol.apex.Apex;
import lol.apex.feature.integration.api.ViaVersionIntegration;
import net.minecraft.SharedConstants;
import org.jspecify.annotations.Nullable;

/**
 * Integrates with ViaFabricPlus or ViaFabric.
 * If none of them exists, it won't be present and will just return the game's native protocol version.
 */
public final class GenericViaIntegration extends ViaVersionIntegration {
    private static final ViaVersionIntegration[] INTEGRATIONS = new ViaVersionIntegration[]{
            // Prefer ViaFabricPlus over ViaFabric,
            // it has movement fixes and is installed most of the time.
            VFPIntegration.INSTANCE,
            // If we are out of date with the latest ViaFabricPlus version
            // and a user needs to use a newer version not supported by the latest VFP version
            // that supports our Minecraft version,
            // we should have compatibility with ViaFabric.
            ViaFabricIntegration.INSTANCE
    };
    private static final @Nullable ViaVersionIntegration activeIntegration;
    public static final GenericViaIntegration INSTANCE = new GenericViaIntegration();

    static {
        ViaVersionIntegration workingIntegration = null;
        for (final var integration : INTEGRATIONS) {
            if (integration.isPresent()) {
                workingIntegration = integration;
                Apex.LOGGER.info("Integration {} works! Using it.", integration.getClass().getSimpleName());
                break;
            }
        }
        if (workingIntegration == null) {
            Apex.LOGGER.warn("No working ViaVersion integration found!");
        }
        activeIntegration = workingIntegration;
    }

    @Override
    public int getProtocolVersion() {
        return activeIntegration != null
                ? activeIntegration.getProtocolVersion()
                : SharedConstants.getProtocolVersion();
    }

    @Override
    public boolean isPresent() {
        return activeIntegration != null;
    }
}
