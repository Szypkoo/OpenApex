package lol.apex.feature.integration.impl.viaversion;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.api.ViaFabricPlusBase;
import lol.apex.feature.integration.api.ViaVersionIntegration;
import lol.apex.util.java.ReflectionUtil;
import net.minecraft.SharedConstants;
import org.jspecify.annotations.Nullable;

/**
 * Integrates with ViaFabricPlus
 */
public final class VFPIntegration extends ViaVersionIntegration {
    private final @Nullable ViaFabricPlusBase api;
    private static final boolean EXISTS = ReflectionUtil
            .classExists("com.viaversion.viafabricplus.ViaFabricPlus");
    public static final VFPIntegration INSTANCE = new VFPIntegration();

    public VFPIntegration() {
        api = EXISTS ? ViaFabricPlus.getImpl() : null;
    }

    @Override
    public int getProtocolVersion() {
        if (api == null) {
            return SharedConstants.getProtocolVersion();
        }
        return api.getTargetVersion().getVersion();
    }

    @Override
    public boolean isPresent() {
        return EXISTS;
    }
}
