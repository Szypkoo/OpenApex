package lol.apex.feature.module.implementation.legit;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;

import lol.apex.feature.module.base.Module;

import lol.apex.feature.module.base.*;

@ModuleInfo( 
    name = "Eagle", 
    description = "Sneaks on block edges to help you bridge.",
    category = Category.LEGIT
)
public class EagleModule extends Module {
    private boolean bridging; 

    @EventHook
    public void onTick(ClientTickEvent event) {
        if(mc.player == null) return; 
        if(!this.enabled()) return;

        if(mc.player.getMainHandStack().getItem() instanceof BlockItem || mc.player.getOffHandStack().getItem() instanceof BlockItem) {
            if(mc.player.getPitch() < 70 || !mc.player.isOnGround()) {
                if(bridging) {
                    mc.options.sneakKey.setPressed(false);
                    bridging = false;
                }
            }
            if (!mc.player.isOnGround()) return;
            BlockPos blockPos = mc.player.getBlockPos().down();
            ClientWorld world = mc.world; 

            if (world.getBlockState(blockPos).isReplaceable() && world.getBlockState(blockPos.down()).isReplaceable() && world.getBlockState(blockPos.down().down()).isReplaceable()) {
                mc.options.sneakKey.setPressed(true);
                bridging = true;
            } else {
                if(bridging) {
                    mc.options.sneakKey.setPressed(false); 
                    bridging = false;
                }
            }
        }
    }
}
