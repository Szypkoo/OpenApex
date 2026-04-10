package lol.apex.feature.module.implementation.player; 

import dev.toru.clients.eventBus.EventHook;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.Apex;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.util.player.PlayerUtil;
import lol.apex.util.world.ScaffoldUtil;
import lol.apex.event.player.PlayerRotationEvent;
import lol.apex.util.rotation.BlockRotationUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(
        name = "Breaker",
        description = "Helps you avoid and break certain blocks around you.",
        category = Category.PLAYER
)
public class BreakerModule extends Module {

    public final BoolSetting rotate = new BoolSetting("Rotate", false);
    public final BoolSetting fire = new BoolSetting("Fire", false);
    public final BoolSetting tnt = new BoolSetting("TNT", false);

    public final BoolSetting pauseScaffold = new BoolSetting("Pause Scaffold", true);
    private BlockPos targetPos = null;
    private boolean targetPlace = false;

    private boolean isScaffolding() {
        return pauseScaffold.getValue() &&
           Apex.moduleManager.getByClass(ScaffoldModule.class) != null &&
           Apex.moduleManager.getByClass(ScaffoldModule.class).enabled();
    }

    @EventHook
    public void onTick(ClientTickEvent event) {
        if(mc.player == null || mc.world == null) {
            return;
        } 

        if (isScaffolding()) {
            targetPos = null;
            return;
        }

        BlockPos playerPos = mc.player.getBlockPos(); 
        
        targetPos = null; 
        targetPlace = false;

        if(tnt.getValue()) {
            BlockPos tntPos = findNearbyBlock(playerPos, 4, Blocks.TNT); 

            if(tntPos != null) {
                targetPos = tntPos;
                targetPlace = false; 

                if(!rotate.getValue()) {
                    PlayerUtil.attackBlock(tntPos, Direction.UP); 
                    mc.player.swingHand(Hand.MAIN_HAND); 
                    targetPos = null;
                }
                return;
            }
        } 

        if (fire.getValue()) {
            BlockPos firePos = findNearbyBlock(playerPos, 4, Blocks.FIRE); 
            if(firePos != null) {
                targetPos = firePos;
                targetPlace = false; 

                if(!rotate.getValue()) {
                    PlayerUtil.attackBlock(firePos, Direction.UP); 
                    mc.player.swingHand(Hand.MAIN_HAND); 
                    targetPos = null;
                }
                return;
            }
        }
    } 

    private BlockPos findNearbySourceLava(BlockPos center, int radius) {
        for (BlockPos pos : BlockPos.iterate(center.add(-radius, -radius, -radius), center.add(radius, radius, radius))) {
            if (mc.world.getBlockState(pos).getBlock() == Blocks.LAVA && mc.world.getBlockState(pos).getFluidState().isStill()) {
                return pos;
            }
        }
        return null;
    } 

    private BlockPos findNearbyBlock(BlockPos center, int radius, Block targetBlock) {
        for (BlockPos pos : BlockPos.iterate(center.add(-radius, -radius, -radius), center.add(radius, radius, radius))) {
            if (mc.world.getBlockState(pos).getBlock() == targetBlock) {
                return pos;
            }
        }
        return null;
    } 

    @EventHook
    public void onRotation(PlayerRotationEvent event) {
        if (isScaffolding()) {
            targetPos = null;
            return;
        }
        
        if(!rotate.getValue() || targetPos == null) {
             return;
        } 

        BlockHitResult hit = new BlockHitResult( 
                new Vec3d(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5),
                Direction.UP,
                targetPos,
                false); 

        var rot = BlockRotationUtil.getRotationTowardsBlock(hit); 
        if(rot != null) {
            event.set(rot);
        } 

        if(targetPlace) {
            placeBlock(targetPos);
        } else {
            PlayerUtil.attackBlock(targetPos, Direction.UP); 
            mc.player.swingHand(Hand.MAIN_HAND); 
        } 

        targetPos = null;
    } 

    private void placeBlock(BlockPos pos) {
        if(ScaffoldUtil.isValidBlockPosition(pos)) {
            return;
        }

        ScaffoldUtil.PosFace place = ScaffoldUtil.findPlaceableNeighbor(pos, false); 
        if(place == null) return; 

        BlockHitResult hitResult = new BlockHitResult(
            ScaffoldUtil.getRandomizedHitVec(place.bp(), place.dir()),
            place.dir(),
            place.bp(),
            false
        ); 

        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    @SuppressWarnings("unused")
    private int findBlockInHotbar() {
        if (mc.player == null) return -1;

        for (int i = 0; i < 9; i++) {
            final var stack = mc.player.getInventory().getMainStacks().get(i);
            if (stack.getItem() instanceof net.minecraft.item.BlockItem) {
                mc.player.getInventory().setSelectedSlot(i);
                return i;
            }
        }
        return -1;
    }
}