package lol.apex.util.game;

import lol.apex.feature.integration.impl.viaversion.GenericViaIntegration;
import lombok.experimental.UtilityClass;

/**
 * Convenience methods using {@link GenericViaIntegration}
 **/
@UtilityClass
public final class VersionUtil {
    public static final int R1_8 = 47;
    // on tick exempt versions
    public static final int R1_17 = 755;
    public static final int R1_20_6 = 766;

    /**
     * @see GenericViaIntegration#getProtocolVersion()
     */
    public static int getVersion() {
        return GenericViaIntegration.INSTANCE.getProtocolVersion();
    }

    // why not the ProtocolVersion class provided by ViaVersion?
    // we don't have access to it if ViaFabric or ViaFabricPlus isn't installed.

    public static boolean is(int v) {
        return getVersion() == v;
    }

    public static boolean older(int v) {
        return getVersion() < v;
    }

    public static boolean newer(int v) {
        return getVersion() > v;
    }

    public static boolean isOrOlder(int v) {
        return getVersion() <= v;
    }

    public static boolean isOrNewer(int v) {
        return getVersion() >= v;
    }
}
