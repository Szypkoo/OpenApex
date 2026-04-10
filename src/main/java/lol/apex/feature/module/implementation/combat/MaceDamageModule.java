package lol.apex.feature.module.implementation.combat;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.player.PlayerAttackEventPre;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.game.PacketUtil;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.HitResult;

import lol.apex.feature.module.base.Module;

import lol.apex.feature.module.base.*;

@ModuleInfo( 
    name = "MaceDamage",
    description = "Makes the mace into a instant kill weapon.",
    category = Category.COMBAT
)
public class MaceDamageModule extends Module {
    private final SliderSetting height = new SliderSetting("Slam Height", 9f, 0f, 9.4f, 0.1f);
    private final SliderSetting amount = new SliderSetting("Height Gain", 1f, 1f, 15f, 1f);
    private final BoolSetting onlyMace = new BoolSetting("Only Mace", true);
    private final BoolSetting onlyEntity = new BoolSetting("Only Entity", true);

    @EventHook
    private void onAttack(PlayerAttackEventPre event) {
        if(!this.enabled()) return;
        if (mc.player == null || mc.getNetworkHandler() == null || mc.player.isInLava() || mc.player.isInSwimmingPose()){
            return;
        }             
        if(onlyMace.getValue().booleanValue() && !mc.player.isHolding(Items.MACE)) return;
        if(onlyEntity.getValue().booleanValue() && mc.crosshairTarget.getType() != HitResult.Type.ENTITY) return;

        double oldY = mc.player.getY();
        for (int i=0; i < amount.getValue().intValue(); i++) {
            PacketUtil.sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), oldY + (height.getValue().floatValue()*i), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), false, mc.player.horizontalCollision));
        }
        PacketUtil.sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), oldY + 0.0001, mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), false, mc.player.horizontalCollision));
    }
}
