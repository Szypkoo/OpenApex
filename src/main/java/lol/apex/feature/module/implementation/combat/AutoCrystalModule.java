package lol.apex.feature.module.implementation.combat;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.player.PlayerUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import lol.apex.feature.module.base.*;
import lol.apex.feature.module.base.Module;

@ModuleInfo( 
    name = "AutoCrystal",
    description = "Automatically places and breaks crystals.",
    category = Category.COMBAT
)
public class AutoCrystalModule extends Module {
    private SliderSetting breakDelay = new SliderSetting("Break Delay", 0f, 0f, 20f, 0.1f);
    private SliderSetting placeDelay = new SliderSetting("Place Delay", 0f, 0f, 20f, 0.1f);;

    private long lastBreakTime = 0;
    private long lastPlaceTime = 0;

    @EventHook
    private void onTick(ClientTickEvent event) {
        long now = System.currentTimeMillis();
        if(mc.player == null) return;

        KeyBinding useItem = mc.options.useKey;

        if(useItem.isPressed() && now - lastPlaceTime >= placeDelay.getValue().intValue()) {
            placeCrystal();
            lastPlaceTime = now;
        }

        if(now - lastBreakTime >= breakDelay.getValue().intValue()) {
            breakCrystal();
            lastBreakTime = now;
        }
    }

    private void placeCrystal() {
        if(mc.player == null) return;

        if(mc.player.getMainHandStack().getItem() != Items.END_CRYSTAL) {
            return;
        }

        HitResult crosshair = mc.crosshairTarget;
        if(crosshair instanceof BlockHitResult blockHit) {
            BlockPos pos = blockHit.getBlockPos();
            var world = mc.world;

            boolean canPlace = world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN ||
                    world.getBlockState(pos).getBlock() == Blocks.BEDROCK;

            if(!canPlace) return;

            BlockPos above = pos.up();
            if(!world.isAir(above)) return;
            if(!world.getOtherEntities(null, new Box(above)).isEmpty()) return;

            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, blockHit);
            PlayerUtil.swingMainHand();
        }
    }

    private void breakCrystal() {
        if(mc.player == null) return;

        for(Entity entity : mc.world.getEntities()) {
            if(entity instanceof EndCrystalEntity) {
                PlayerUtil.attack(entity);
                PlayerUtil.swingMainHand();
                break;
            }
        }
    }
}