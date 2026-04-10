package lol.apex.feature.module.implementation.combat;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.player.PlayerAttackEventPre;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.implementation.combat.criticals.*;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.player.PlayerUtil;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.Entity;

import lol.apex.feature.module.base.*;

@ModuleInfo( 
    name = "Criticals", 
    description = "Always get critical hits.",
    category = Category.COMBAT
)
public class CriticalsModule extends Module {
    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.PACKET);
    public final SliderSetting particles = new SliderSetting("Particles", 25f, 0f, 100f, 1f).hide(()-> !(mode.getValue() == Mode.VISUAL));

    @RequiredArgsConstructor
    public enum Mode {
        AUTO_JUMP("Auto Jump"),
        MOSPIXEL("Mospixel"),
        PACKET("Packet"),
        VISUAL("Visual"),
        THEMIS("Themis"),
        SPARTAN("Spartan");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    public Entity target;

    @EventHook
    public void onTick(ClientTickEvent event) {
        if(!(mode.getValue() == Mode.AUTO_JUMP)) return;

        PlayerUtil.jump();
    }

    @EventHook
    public void onAttack(PlayerAttackEventPre event) {
        if(mc.player == null || mc.world == null) {
            return;
        }

        target = event.getTarget();
        causeCrit(event);
    }

    public void causeCrit(PlayerAttackEventPre event) {

        switch (mode.getValue()) {
            case MOSPIXEL -> MospixelCriticals.onAttack(event);
            case PACKET -> PacketCriticals.onAttack(event);
            case VISUAL -> VisualCriticals.onAttack(this, event);
            case THEMIS -> ThemisCriticals.onAttack(event);
            case SPARTAN -> SpartanCriticals.onAttack(event);
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }
}
