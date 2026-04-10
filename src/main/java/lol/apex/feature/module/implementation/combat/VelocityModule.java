package lol.apex.feature.module.implementation.combat;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.packet.PacketEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.implementation.combat.velocity.*;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lombok.RequiredArgsConstructor;

@ModuleInfo(
        name = "Velocity",
        description = "Uses heavy dick and balls to drag across the floor to reduce velocity.",
        category = Category.COMBAT
)
public class VelocityModule extends Module {
    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.VANILLA);
    public final BoolSetting ignoreSPress = new BoolSetting("Ignore S Press", true);
    public final BoolSetting ignoreOnFire = new BoolSetting("Ignore On Fire", true);
    public final SliderSetting chance = new SliderSetting("Reset Chance", 50, 1, 100, 1);
    public final SliderSetting horizontalVanilla = new SliderSetting("Horizontal", 0, 0, 100, 1).hide(() -> !(mode.getValue() == Mode.VANILLA));
    public final SliderSetting verticalVanilla = new SliderSetting("Vertical", 0, 0, 100, 1).hide(() -> !(mode.getValue() == Mode.VANILLA));

    @RequiredArgsConstructor
    public enum Mode {
        VANILLA("Vanilla"),
        JUMPRESET("Jump Reset"),
        CANCEL("Cancel"),
        VULCAN("Vulcan"),
        MATRIX("Matrix"),
        GRIM("Grim"),
        GODSEYE("GodsEye"),
        POLAR("Polar");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @EventHook
    public void onPacket(PacketEvent.Receive event) {
        switch (mode.getValue()) {
            case VANILLA -> VanillaVelocity.onPacket(this, event);
            case CANCEL -> CancelVelocity.onPacket(this, event);
            case JUMPRESET -> ResetVelocity.onPacket(this, event);
            case GRIM -> GrimVelocity.onPacket(this, event);
            case VULCAN -> VulcanVelocity.onPacket(this, event);
            case MATRIX -> MatrixVelocity.onPacket(this, event);
            case GODSEYE -> GodsEyeVelocity.onPacket(event);
            case POLAR -> PolarVelocity.onPacket(this, event);
        }
    }

    @Override
    public String getSuffix() {
        if (mode.getValue() == Mode.VANILLA) {
            String horizontal = horizontalVanilla.getValue().toString();
            String vertical = verticalVanilla.getValue().toString();
            return horizontal + "% - " + vertical + "%";
        }

        return mode.getValue().toString();
    }
}
