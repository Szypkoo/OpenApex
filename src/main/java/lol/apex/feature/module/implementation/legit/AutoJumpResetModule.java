package lol.apex.feature.module.implementation.legit;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.setting.implementation.SliderSetting;

import lol.apex.feature.module.base.Module;
import lol.apex.util.player.PlayerUtil;
import lol.apex.util.rotation.MathUtil;
import lol.apex.feature.module.base.*;

@ModuleInfo( 
    name = "AutoJumpReset",
    description = "Resets your jump, so your target takes more knockback.",
    category = Category.LEGIT
)
public class AutoJumpResetModule extends Module {
    private SliderSetting chance = new SliderSetting("Chance",
            0f, 0f, 100f, 1);

    @EventHook
    public void onTick(ClientTickEvent event) {
        if(MathUtil.randomInt(1, 100) <= chance.getValue().intValue()) {
            if(mc.player == null) return; 
            if(mc.currentScreen != null) return; 
            if(mc.player.isUsingItem()) return; 
            if(mc.player.hurtTime == 0) return; 
            if(mc.player.hurtTime == mc.player.maxHurtTime) return; 
            if(!mc.player.isOnGround()) return; 
            if(mc.player.hurtTime == 9 && MathUtil.randomInt(1, 100) <= chance.getValue().intValue()) {
                PlayerUtil.jump();
            }
        }
    }
}
