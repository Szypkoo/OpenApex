package lol.apex.feature.module.implementation.combat;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.PreMotionEvent;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.implementation.combat.regen.*;
import lol.apex.feature.module.setting.implementation.*;

import lol.apex.feature.module.base.*;
import lombok.RequiredArgsConstructor;

@ModuleInfo( 
    name = "Regen", 
    description = "Uses an exploit to make you regenerate quicker. (1.8)",
    category = Category.COMBAT
)
public class RegenModule extends Module {
    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.VANILLA);
    public final SliderSetting minHealth = new SliderSetting("Min. Health", 4f, 0, 20f, 0.1f);
    private final SliderSetting vanillaSpeed = new SliderSetting("Speed (Vanilla)", 20, 1, 200, 1);

    @RequiredArgsConstructor
    private enum Mode {
        VANILLA("Vanilla"),
        VERUS("Verus");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @EventHook
    public void onPreMotion(PreMotionEvent event) {
        switch (mode.getValue()) {
            case VANILLA -> {
                VanillaRegen.onMotion(this, event);
                break;
            }

            case VERUS -> {
                VerusRegen.onMotion(this, event);
                break;
            }
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }
}
