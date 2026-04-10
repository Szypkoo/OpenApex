package lol.apex.feature.module.implementation.movement;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.implementation.movement.wallclimb.KarhuWallClimb;
import lol.apex.feature.module.implementation.movement.wallclimb.SpartanFlagWallClimb;
import lol.apex.feature.module.implementation.movement.wallclimb.VerusWallClimb;
import lol.apex.feature.module.implementation.movement.wallclimb.VulcanWallClimb;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lombok.RequiredArgsConstructor;

@ModuleInfo(
        name = "WallClimb",
        description = "Allows you to climb up walls.",
        category = Category.MOVEMENT
)
public class WallClimbModule extends Module {
    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.VERUS);

    @RequiredArgsConstructor
    public enum Mode {
        VANILLA("Vanilla"),
        VERUS("Verus"),
        KARHU("Karhu"),
        VULCAN("Vulcan"),
        SPARTAN("Spartan Flag");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @EventHook
    public void onMove(PlayerMoveEvent event) {

        switch (mode.getValue()) {
            case VANILLA, VERUS -> VerusWallClimb.onMove(event);
            case KARHU -> KarhuWallClimb.onMove(event);
            case VULCAN -> VulcanWallClimb.onMove(event);
            case SPARTAN -> SpartanFlagWallClimb.onMove(event);
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }
}
