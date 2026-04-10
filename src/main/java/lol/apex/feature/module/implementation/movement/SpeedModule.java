package lol.apex.feature.module.implementation.movement;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.player.PlayerJumpEvent;
import lol.apex.event.player.PlayerJumpingFactorEvent;
import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.implementation.movement.speed.*;
import lol.apex.feature.module.implementation.movement.speed.grim.OldGrimSpeed;
import lol.apex.feature.module.implementation.movement.speed.intave.Intave12Speed;
import lol.apex.feature.module.implementation.movement.speed.intave.Intave13Speed;
import lol.apex.feature.module.implementation.movement.speed.ThemisSpeed;
import lol.apex.feature.module.implementation.movement.speed.polar.PolarNewSpeed;
import lol.apex.feature.module.implementation.movement.speed.polar.PolarSpeed;
import lol.apex.feature.module.implementation.movement.speed.spartan.SpartanFastLatestSpeed;
import lol.apex.feature.module.implementation.movement.speed.spartan.SpartanFastSpeed;
import lol.apex.feature.module.implementation.movement.speed.spartan.SpartanSlowSpeed;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lombok.RequiredArgsConstructor;

@ModuleInfo(
        name = "Speed",
        description = "Allows you to move faster than in Vanilla.",
        category = Category.MOVEMENT
)
public class SpeedModule extends Module {
    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.VANILLA);
    public final SliderSetting speed = new SliderSetting("Speed", 0.42f, 0.1f, 5.0f, 0.01f);

    // Custom
    public final BoolSetting custom_changeJumpHeight = new BoolSetting("Change Jump Height", false).hide(() -> mode.getValue() != Mode.CUSTOM);
    public final SliderSetting custom_jumpHeight = new SliderSetting("Jump Height", 0.42f, 0.1f, 5.0f, 0.01f).hide(() -> mode.getValue() != Mode.CUSTOM || !custom_changeJumpHeight.getValue());
    public final BoolSetting custom_jumpFromGround = new BoolSetting("Jump From Ground", true).hide(() -> mode.getValue() != Mode.CUSTOM);
    public final BoolSetting custom_useJumpBoost = new BoolSetting("Use Jump Boost", false).hide(() -> mode.getValue() != Mode.CUSTOM);
    public final SliderSetting custom_jumpBoost = new SliderSetting("Jump Boost Amount", 0.42f, 0.1f, 5.0f, 0.01f).hide(() -> mode.getValue() != Mode.CUSTOM || !custom_useJumpBoost.getValue());
    public final BoolSetting custom_changeTimerSpeed = new BoolSetting("Change Timer Speed", false).hide(() -> mode.getValue() != Mode.CUSTOM);
    public final SliderSetting custom_timerAmount = new SliderSetting("Timer Speed", 1.0f, 0.1f, 10.0f, 0.1f).hide(() -> mode.getValue() != Mode.CUSTOM || !custom_changeTimerSpeed.getValue());
    public final BoolSetting custom_checkGround = new BoolSetting("Ground Check", true).hide(() -> mode.getValue() != Mode.CUSTOM);
    public final BoolSetting custom_usePulldown = new BoolSetting("Use Pulldown", false).hide(() -> mode.getValue() != Mode.CUSTOM);
    public final SliderSetting custom_pullDown = new SliderSetting("Pull Down", 0.08f, 0.0f, 1.0f, 0.01f).hide(() -> mode.getValue() != Mode.CUSTOM || !custom_usePulldown.getValue());
    public final SliderSetting custom_fallPullDown = new SliderSetting("Fall Pull Down", 0.04f, 0.0f, 1.0f, 0.01f).hide(() -> mode.getValue() != Mode.CUSTOM || !custom_usePulldown.getValue());


    @RequiredArgsConstructor
    public enum Mode {
        CUSTOM("Custom"),
        VANILLA("Vanilla"),
        HOP("Hop"),
        LEGIT("Legit"),
        OLD_GRIM("Old Grim"),
        VERUS("Verus"),
        INTAVE_13("Intave 13"),
        INTAVE_12("Intave 12"),
        NCP("NCP"),
        MATRIX("Matrix"),
        KARHU("Karhu"),
        CUBECRAFT("CubeCraft"),
        KRYPTIC("Kryptic 3 Tick"),
        POLAR("Polar"),
        FAKEPIXEL("Fakepixel"),
        THEMIS("Themis"),
        VULCAN("Vulcan"),
        GODSEYE("GodsEye"),
        KRYPTICTIMER("Kryptic Timer"),
        SPARTAN_FAST("Spartan Fast"),
        SPARTAN_SLOW("Spartan Slow"),
        SPARTAN_FAST_LATEST("Spartan Fast Latest"),
        POLAR_NEW("Polar New");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @EventHook
    public void onJumpChangeFactor(PlayerJumpingFactorEvent event) {
        if (mode.getValue() == Mode.POLAR) {
            PolarSpeed.onJumpChangeFactor(event);
        }
    }

    @EventHook
    public void onMove(PlayerMoveEvent event) {
        switch (mode.getValue()) {
            case VERUS -> VerusSpeed.onMove(event);
            case POLAR -> PolarSpeed.onMove(event);
            case VANILLA -> VanillaSpeed.onMove(this, event);
            case HOP -> HopSpeed.onMove(this, event);
            case OLD_GRIM -> OldGrimSpeed.onMove(event);
            case INTAVE_12 -> Intave12Speed.onMove(event);
            case INTAVE_13 -> Intave13Speed.onMove(event);
            case CUBECRAFT -> CubeCraftSpeed.onMove(event);
            case NCP -> NCPSpeed.onMove(event);
            case MATRIX -> MatrixSpeed.onMove(event);
            case FAKEPIXEL -> FakepixelSpeed.onMove(event);
            case VULCAN -> VulcanSpeed.onMove(event);
            case GODSEYE -> GodsEyeSpeed.onMove(event);
            case CUSTOM -> CustomSpeed.onMove(this, event);
            case SPARTAN_FAST -> SpartanFastSpeed.onMove(event);
            case SPARTAN_SLOW -> SpartanSlowSpeed.onMove(event);
            case SPARTAN_FAST_LATEST -> SpartanFastLatestSpeed.onMove(event);
            case POLAR_NEW -> PolarNewSpeed.onMove(event);

            case KARHU, KRYPTIC -> {
            }
        }

        event.setCancelled(true);
    }

    @EventHook
    public void onJump(PlayerJumpEvent event) {
        if (mode.getValue() == Mode.POLAR_NEW) {
            PolarNewSpeed.onJump(event);
        }
    }

    @EventHook
    public void onTick(ClientTickEvent event) {
        switch (mode.getValue()) {
            case VERUS -> VerusSpeed.onTick(event);
            case INTAVE_12 -> Intave12Speed.onTick(event);
            case INTAVE_13 -> Intave13Speed.onTick(event);
            case KARHU, KRYPTIC -> KarhuSpeed.onTick(event);
            case CUBECRAFT -> CubeCraftSpeed.onTick(event);
            case HOP -> HopSpeed.onTick(event);
            case LEGIT -> LegitSpeed.onTick(event);
            case THEMIS -> ThemisSpeed.onTick(event);
            case VULCAN -> VulcanSpeed.onTick(event);
            case GODSEYE -> GodsEyeSpeed.onTick(event);
            case CUSTOM -> CustomSpeed.onTick(this, event);
            case KRYPTICTIMER -> KrypticTimerSpeed.onTick(event);
            case POLAR_NEW -> PolarNewSpeed.onTick(event);

        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }
}
