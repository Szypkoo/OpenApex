package lol.apex.feature.integration.impl.viaversion;

import lol.apex.Apex;
import lol.apex.feature.integration.api.ViaVersionIntegration;
import lol.apex.util.java.ReflectionUtil;
import net.minecraft.SharedConstants;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ViaFabricIntegration extends ViaVersionIntegration {
    private static final String VIA_FABRIC_PACKAGE = "com.viaversion.fabric.mc" +
            SharedConstants.getGameVersion()
            .name()
            .replace(".", "");
    private static final @Nullable Class<?> VIA_FABRIC_CLASS = ReflectionUtil
            .getOptClass(VIA_FABRIC_PACKAGE + ".ViaFabric");
    public static final boolean EXISTS = VIA_FABRIC_CLASS != null;

    public static final ViaFabricIntegration INSTANCE = new ViaFabricIntegration();

    private final @Nullable Object configInst;
    private final @Nullable Method getClientSideVersionMethod;

    public ViaFabricIntegration() {
        if (!EXISTS) {
            configInst = null;
            getClientSideVersionMethod = null;
            return;
        }

        if (VIA_FABRIC_CLASS == null) {
            configInst = null;
            getClientSideVersionMethod = null;
            return;
        }

        final Field vfConfigField;
        try {
            vfConfigField = VIA_FABRIC_CLASS.getField("config");
        } catch (NoSuchFieldException e) {
            Apex.LOGGER.error("Failed to get config field inside of ViaFabric class");
            configInst = null;
            getClientSideVersionMethod = null;
            return;
        }

        Object configInst1;
        Class<?> configClass;

        try {
            configInst1 = vfConfigField.get(null);
            configClass = configInst1.getClass();
        } catch (IllegalAccessException e) {
            Apex.LOGGER.error("Failed to get ViaFabric config field:", e);
            configInst = null;
            getClientSideVersionMethod = null;
            return;
        }
        configInst = configInst1;

        Method getClientSideVersionMethod1;

        try {
            getClientSideVersionMethod1 = configClass.getMethod("getClientSideVersion");
        } catch (NoSuchMethodException e) {
            Apex.LOGGER.error("Failed to find getClientSideVersion method inside of ViaFabric config class", e);
            getClientSideVersionMethod1 = null;
        }

        getClientSideVersionMethod = getClientSideVersionMethod1;
    }

    @Override
    public int getProtocolVersion() {
        if (!EXISTS) return SharedConstants.getProtocolVersion();

        try {
            if (configInst == null || getClientSideVersionMethod == null) return SharedConstants.getProtocolVersion();

            return (int) getClientSideVersionMethod.invoke(configInst);
        } catch (Exception e) {
            return SharedConstants.getProtocolVersion();
        }
    }

    @Override
    public boolean isPresent() {
        return false;
    }
}
