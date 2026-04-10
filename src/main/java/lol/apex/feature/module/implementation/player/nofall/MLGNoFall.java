package lol.apex.feature.module.implementation.player.nofall;

import lol.apex.Apex;
import lol.apex.event.client.PreMotionEvent;
import lol.apex.event.player.PlayerRotationEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.feature.module.implementation.player.NoFallModule;
import lol.apex.util.entity.simulation.SimulatedPlayer;
import lol.apex.util.rotation.BlockRotationUtil;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public final class MLGNoFall extends SubModule {
    public MLGNoFall() {
        super("MLGNoFall", "Tries to clutch using water buckets.", "Generic");
    }
    private static final Set<Item> ALLOWED_ITEMS = Set.of(
            Items.WATER_BUCKET,
            Items.COBWEB
    );
//    private static final Set<Block> ALLOWED_BLOCKS = Set.of(
//            Blocks.WATER,
//            Blocks.COBWEB,
//            Blocks.VINE
//    );
    private static boolean swapped = false;
    private static int oldSlot = -1;
    private static final int I_AM_ALWAYS_1_TICK_AHEAD = 3;
    private static @Nullable BlockHitResult r = null;

    private static int findInHotbar() {
        if (mc.player == null) throw new IllegalStateException("mc.player == null when calling findBlockSlot");
        // I wish I could just like Slots.Hotbar.forEach { i -> i.stack.item is ItemBlock } or something but cat
        for (int i = 0; i < 9; i++) {
            final var stack = mc.player.getInventory().getMainStacks().get(i);
            if (ALLOWED_ITEMS.contains(stack.getItem())) {
                return i;
            }
        }
        return -1;
    }

    @SuppressWarnings("unused")
    public static void onPreMotion(NoFallModule parent, PreMotionEvent event) {
        assert mc.player != null;
        final var safeFallDistance = mc.player.getAttributeValue(EntityAttributes.SAFE_FALL_DISTANCE);
        if (mc.player.fallDistance > safeFallDistance) {
            final var slot = findInHotbar();
            if (slot == -1) return;
            Apex.sendChatMessage("found slot");
            final var player = mc.player;
            final var reach = Math.max(player.getBlockInteractionRange(), player.getEntityInteractionRange());
            final var sim = SimulatedPlayer.fromPlayer(mc.player);
            for (int i = 0; i < I_AM_ALWAYS_1_TICK_AHEAD; i++) {
                Apex.sendChatMessage("ground? " + sim.onGround + " pos = " + sim.pos);
                if (sim.onGround) continue;
                sim.tick(false, mc.player.input.getMovementInput().y, mc.player.input.getMovementInput().x);
            }
            if (!sim.onGround) return;
            if (mc.player.getInventory().getSelectedSlot() != slot) {
                Apex.sendChatMessage("swap to slot");
                oldSlot = mc.player.getInventory().getSelectedSlot();
                swapped = true;
                mc.player.getInventory().setSelectedSlot(slot);
            }
            final var bp = BlockPos.ofFloored(sim.pos).down();
            r = new BlockHitResult(sim.pos, Direction.DOWN, bp, false);
            assert mc.interactionManager != null;
            final var result = mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            Apex.sendChatMessage("interact nigga block");
            if (!result.isAccepted()) {
                Apex.sendChatMessage("you're dying nigger. block place not accepted???");
                r = null;
            }
        } else if (swapped && oldSlot != -1 && r != null) {
            swapped = false;
            mc.player.getInventory().setSelectedSlot(oldSlot);
            oldSlot = -1;
            r = null;
        }
    }

    @SuppressWarnings("unused")
    public static void onRotate(NoFallModule _parent, @NonNull PlayerRotationEvent e) {
        if (r == null) return;
        final var rots = BlockRotationUtil.getRotationTowardsBlock(r);
        e.set(rots);
    }
}
