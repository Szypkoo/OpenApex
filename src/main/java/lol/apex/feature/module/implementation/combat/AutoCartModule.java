package lol.apex.feature.module.implementation.combat;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.util.player.InventoryUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

@ModuleInfo( 
    name = "AutoCart",
    description = "Automatically places and ignites minecarts.",
    category = Category.COMBAT
)
public class AutoCartModule extends Module {
    public final BoolSetting autoSwitch = new BoolSetting("Auto Switch", true);

    private boolean wasUsingBow = false; 
    private BlockPos targetPos = null; 
    private int originalSlot = -1; 

    private int stage = 0; 
    private int waitTicks = 0; 

    @Override
    public void onEnable() {
        reset();
    } 

    @Override
    public void onDisable() {
        reset();
    } 

    private void reset() {
        wasUsingBow = false; 
        targetPos = null; 
        originalSlot = -1; 
        stage = 0; 
        waitTicks = 0;
    } 

    @EventHook
    public void onTick(ClientTickEvent event) {
        if(mc.player == null || mc.world == null) {
            return;
        } 

        boolean usingBow = mc.player.isUsingItem()
            && mc.player.getActiveItem().isOf(Items.BOW); 

        if(usingBow) {
            wasUsingBow = true;
            return;
        } 

        if(wasUsingBow && stage == 0) {
            targetPos = target(); 

            if (targetPos != null) {
                stage = 1; 
                originalSlot = mc.player.getInventory().getSelectedSlot();
            }

            wasUsingBow = false;
        } 

        switch (stage) {
            case 1 -> railPlacement(mc.player); 
            case 2 -> cartPlacement(mc.player); 
            case 3 -> ignition(mc.player, mc.world);
        }
    } 

    private void railPlacement(ClientPlayerEntity player) {
        int slot = findRail(); 

        if (slot == -1 || targetPos == null) {
            reset(); 
            return;
        }
        
        targetPos = snapToGround(targetPos);

        if (targetPos == null || !canPlaceRail(targetPos)) {
            reset();
            return;
        }

        player.getInventory().setSelectedSlot(slot); 
        place(targetPos); 

        stage = 2;
    } 

    private void cartPlacement(ClientPlayerEntity player) {
        int slot = findCart(); 

        if (slot == -1) {
            reset();
            return;
        } 

        player.getInventory().setSelectedSlot(slot); 
        place(targetPos); 

        stage = 3; 
        waitTicks = 1;
    } 

    private void ignition(ClientPlayerEntity player, ClientWorld world) {
        if(waitTicks-- > 0) {
            return;
        } 

        TntMinecartEntity cart = world.getEntitiesByClass(
        TntMinecartEntity.class,
                new Box(targetPos).expand(3),
                e -> true
        ).stream().findFirst().orElse(null); 

        if (cart != null) {
            mc.interactionManager.interactEntity(player, cart, Hand.MAIN_HAND);
            player.swingHand(Hand.MAIN_HAND);
            mc.interactionManager.interactEntity(player, cart, Hand.MAIN_HAND);
        } 

        if (autoSwitch.getValue() && originalSlot != -1) {
            player.getInventory().setSelectedSlot(originalSlot);
        }

        reset();
    } 

    private BlockPos target() {
        HitResult hit = mc.crosshairTarget; 

        if(hit == null) return null; 
    
        if (hit.getType() == Type.BLOCK) {
            BlockHitResult bhr = (BlockHitResult) hit;

            return bhr.getSide() == Direction.UP
                    ? bhr.getBlockPos().up()
                    : bhr.getBlockPos().offset(bhr.getSide());
        }

        Vec3d start = mc.player.getCameraPosVec(1.0f); 
        Vec3d direction = mc.player.getRotationVec(1.0f).multiply(5.0); 
        Vec3d end = start.add(direction);

        BlockHitResult bhr = mc.world.raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                mc.player
        )); 

        if (bhr != null && bhr.getType() == Type.BLOCK) {
            return bhr.getSide() == Direction.UP
                    ? bhr.getBlockPos().up()
                    : bhr.getBlockPos().offset(bhr.getSide());
        }

        return null;
    } 

    private BlockPos snapToGround(BlockPos pos) {
        if(mc.world == null) return null; 

        BlockPos p = pos; 

        for (int i = 0; i < 3 &&
                mc.world.getBlockState(p).isAir() &&
                mc.world.getBlockState(p.down()).isAir(); i++) {
            p = p.down();
        }

        return mc.world.getBlockState(p).isAir() ? null : p.up();
    } 

    private void place(BlockPos pos) {
        BlockHitResult bhr = new BlockHitResult(
                Vec3d.ofCenter(pos),
                Direction.UP,
                pos,
                false
        ); 

        mc.interactionManager.interactBlock(
                mc.player,
                Hand.MAIN_HAND,
                bhr
        );
    } 

    private boolean canPlaceRail(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos); 
        BlockState below = mc.world.getBlockState(pos.down()); 

        return state.isAir() && below.isSolidBlock(mc.world, pos.down());
    } 

    private int findRail() {
        int normal = InventoryUtil.findItemInHotbar(Items.RAIL); 
        if(normal != -1) return normal; 

        return -1;
    } 

    private int findCart() {
        return InventoryUtil.findItemInHotbar(Items.TNT_MINECART);
    }
}
