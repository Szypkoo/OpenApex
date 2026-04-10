package lol.apex.feature.module.implementation.movement;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.Module;
import lol.apex.util.player.PlayerUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import lol.apex.feature.module.base.*;

@ModuleInfo( 
    name = "AutoHeadHitter",
    description = "Automatically hits your head to give you a boost.",
    category = Category.MOVEMENT
)
public class AutoHeadHitterModule extends Module {
    
    @EventHook
    public void onTick(ClientTickEvent event) {
        if(mc.player == null || mc.world == null) return; 

        if(!mc.player.isOnGround()) return; 
        if(!this.enabled()) return;
        if(!mc.player.isSprinting()) return;
        BlockPos headPos = mc.player.getBlockPos().up(2); 
        BlockState staet = mc.world.getBlockState(headPos); 
        
        if(!staet.isAir() && staet.getBlock() != Blocks.WATER && staet.getBlock() != Blocks.LAVA) {
            PlayerUtil.jump();
        }
    }
}
