package lol.apex.feature.module.implementation.movement;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.BlockCollideEvent;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.entity.EntityBlockCollideEvent;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.implementation.movement.noweb.*;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.util.player.PlayerUtil;

import lol.apex.feature.module.base.*;
import lombok.RequiredArgsConstructor;

@ModuleInfo( 
    name = "NoWeb",
    description = "Negates slowness from cobwebs.",
    category = Category.MOVEMENT
)
public class NoWebModule extends Module {
    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.GRIM);

    @RequiredArgsConstructor
    public enum Mode {
        GRIM("Grim"),
        INTAVE_12("Intave 12"),
        KARHU("Karhu"),
        VERUS("Verus"),
        VULCAN("Vulcan"),
        MATRIX("Matrix"),
        COLLISION("Collision"),
        THEMIS("Themis"),
        GODSEYE("GodsEye"),
        SPARTAN("Spartan");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @EventHook
    public void onTick(ClientTickEvent event) {
        if(!PlayerUtil.isInWeb(mc.player)) {
            return;
        }

        switch (mode.getValue()) {
            case GRIM -> GrimNoWeb.onTick(this, event);
            case INTAVE_12 -> Intave12NoWeb.onTick(this, event);
            case KARHU -> KarhuNoWeb.onTick(this, event);
            case VERUS -> VerusNoWeb.onTick(this, event);
            case VULCAN -> VulcanNoWeb.onTick(this, event);
            case MATRIX -> MatrixNoWeb.onTick(this, event);
            case THEMIS -> ThemisNoWeb.onTick(event);
            case GODSEYE -> GodsEyeNoWeb.onTick(this, event);
        }
    }

    @EventHook
    public void onBlockCollide(BlockCollideEvent event){
        if (mode.getValue().equals(Mode.COLLISION)) {
            SolidNoWeb.onBlockCollide(this, event);
        }
    }
    @EventHook
    public void onEntityBlockCollide(EntityBlockCollideEvent event){
        if (mode.getValue().equals(Mode.COLLISION)) {
            SolidNoWeb.onEntityBlockCollide(this, event);
        }
    }


    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }
}
