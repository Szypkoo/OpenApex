package lol.apex.feature.module.implementation.legit;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.skidded.ClickUtil;
import lol.apex.util.math.TimerUtil;
import lol.apex.util.rotation.MathUtil;
import net.minecraft.util.hit.HitResult;

import lol.apex.feature.module.base.*;

@ModuleInfo( 
    name = "AutoClicker",
    description = "Automatically clicks for you.",
    category = Category.LEGIT
)
public class AutoClickerModule extends Module {
    private final BoolSetting disableWhileBreaking = new BoolSetting("Disable While Break", true);
    private final SliderSetting minLeftCps = new SliderSetting("Min Left CPS", 10, 0, 20, 1);
    private final SliderSetting maxLeftCps = new SliderSetting("Max Left CPS", 15, 0, 20, 1);
    private final SliderSetting minRightCps = new SliderSetting("Min Right CPS", 10, 0, 20, 1);
    private final SliderSetting maxRightCps = new SliderSetting("Max Right CPS", 20, 0, 20, 1);
    private final BoolSetting leftClicker = new BoolSetting("Left Clicker", true);
    private final BoolSetting rightClicker = new BoolSetting("Right Clicker", false);
    private final BoolSetting blockHit = new BoolSetting("Block Hit", false); // TODO: add blockhit and blockhit chance
    
    private final TimerUtil leftTimer = new TimerUtil();
    private final TimerUtil rightTimer = new TimerUtil();

    private double currentLeftCPS = minLeftCps.getValue();
    private double currentRightCPS = minRightCps.getValue();

    @EventHook
    public void onTick(ClientTickEvent event){
        if (mc.player == null || mc.interactionManager == null) return;
        if (mc.mouse.wasLeftButtonClicked() && leftClicker.getValue()){
            if (mc.player.raycast(mc.player.getBlockInteractionRange(), mc.player.handSwingProgress, false).getType() == HitResult.Type.BLOCK && disableWhileBreaking.getValue()){
                ClickUtil.action(ClickUtil.Button.LEFT, true);
                return;
            }
            if (leftTimer.passed(1000 / currentLeftCPS, true)){
                ClickUtil.action(ClickUtil.Button.LEFT, true);
                currentLeftCPS = MathUtil.randomInt(Math.round(minLeftCps.getValue()), Math.round(maxLeftCps.getValue()));
                ClickUtil.action(ClickUtil.Button.LEFT, false);
            }
        }
        if (mc.mouse.wasRightButtonClicked() && rightClicker.getValue()){
            if (rightTimer.passed(1000 / currentRightCPS, true)){
                ClickUtil.action(ClickUtil.Button.RIGHT, true);
                currentRightCPS = MathUtil.randomInt(Math.round(minRightCps.getValue()), Math.round(maxRightCps.getValue()));
                ClickUtil.action(ClickUtil.Button.RIGHT, false);

            }
        }
    }
}
