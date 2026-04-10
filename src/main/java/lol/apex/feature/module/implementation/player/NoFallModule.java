package lol.apex.feature.module.implementation.player;

import dev.toru.clients.eventBus.EventHook;
import dev.toru.clients.eventBus.EventPriority;
import lol.apex.event.client.PreMotionEvent;
import lol.apex.event.packet.PacketEvent;
import lol.apex.event.player.PlayerRotationEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.implementation.player.nofall.CubeCraftNoFall;
import lol.apex.feature.module.implementation.player.nofall.MLGNoFall;
import lol.apex.feature.module.implementation.player.nofall.OnGroundNoFall;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lombok.RequiredArgsConstructor;

@ModuleInfo(
        name = "NoFall",
        description = "Negates damage when falling.",
        category = Category.PLAYER
)
public class NoFallModule extends Module {
    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.UNIVERSAL);

    public final SliderSetting distance = new SliderSetting("Distance", 4, 1, 10, 1).hide(()->!mode.getValue().equals(Mode.UNIVERSAL));

    @RequiredArgsConstructor
    public enum Mode {
        UNIVERSAL("Universal"),
        ON_GROUND("On Ground"),
        MLG("MLG"),
        CUBECRAFT("CubeCraft");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @SuppressWarnings("incomplete-switch")
    @EventHook
    public void onMotion(PreMotionEvent event) {
        switch (mode.getValue()) {
            case MLG -> MLGNoFall.onPreMotion(this, event);
        }
    }

    @EventHook(priority = EventPriority.HIGHEST)
    private void onRotate(PlayerRotationEvent e) {
        if (mode.getValue() == Mode.MLG) {
            MLGNoFall.onRotate(this, e);
        }
    }

    @EventHook
    public void onPacket(PacketEvent.Send event) {
        if (mode.getValue() == Mode.ON_GROUND) {
            OnGroundNoFall.onPacket(this, event);
        }

        if (mode.getValue() == Mode.CUBECRAFT) {
            CubeCraftNoFall.onPacket(event);
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }
}
