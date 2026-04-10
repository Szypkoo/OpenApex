package lol.apex.feature.module.implementation.player;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.Apex;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.event.packet.PacketEvent;
import lol.apex.event.player.WorldChangeEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.implementation.combat.AuraRecodeModule;
import lol.apex.feature.module.implementation.movement.InventoryMoveModule;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.player.InventoryUtil;
import lol.apex.util.player.PlayerUtil;
import lol.apex.util.math.TimerUtil;
import lombok.Getter;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.Comparator;

@ModuleInfo(
        name = "InventoryManager",
        description = "Sorts the items in your inventory.",
        category = Category.PLAYER
)
public class InventoryManagerModule extends Module {

    @Getter
    private final SliderSetting swordSlot = new SliderSetting("Sword Slot", 1f, 0f, 9f, 1);
    @Getter
    private final SliderSetting pickaxeSlot = new SliderSetting("Pickaxe Slot", 2f, 0f, 9f, 1);
    @Getter
    private final SliderSetting axeSlot = new SliderSetting("Axe Slot", 3f, 0f, 9f, 1);
    @Getter
    private final SliderSetting blocksSlot = new SliderSetting("Blocks Slot", 4f, 0f, 9f, 1);
    @Getter
    private final SliderSetting gappleSlot = new SliderSetting("Gapple Slot", 5f, 0f, 9f, 1);
    @Getter
    private final SliderSetting bowSlot = new SliderSetting("Bow Slot", 6f, 0f, 9f, 1);
    @Getter
    private final SliderSetting waterBucketSlot = new SliderSetting("Water bucket Slot", 7f, 0f, 9f, 1);


    @Getter
    private final BoolSetting keepSnowballs = new BoolSetting("Keep Snowballs", true);
    @Getter
    private final BoolSetting keepTNT = new BoolSetting("Keep TNT", true);
    @Getter
    private final BoolSetting keepArrows = new BoolSetting("Keep Arrows", true);
    @Getter
    private final BoolSetting keepFintAndSteal = new BoolSetting("Keep F&S", true);

    @Getter
    private final BoolSetting dropUselessArmor = new BoolSetting("Drop Useless Armor", true);

    @Getter
    private final BoolSetting autoDisable = new BoolSetting("Auto Disable", true);

    @Getter
    private final SliderSetting delay = new SliderSetting("Delay", 25, 0f, 500f, 1);


    public final TimerUtil timer = new TimerUtil();

    @EventHook
    public void onWorldChange(WorldChangeEvent event) {
        if (autoDisable.getValue()) {
        //    Apex.sendChatMessage("Inventory Manager has been disabled due to world change.");
            Apex.notificationRenderer.push("Inventory Manager", "Disabled on world change.");

            toggle();
        }
    }

    @EventHook
    public void onPreUpdate(ClientTickEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (!(mc.currentScreen instanceof InventoryScreen) && !Apex.moduleManager.getByClass(InventoryMoveModule.class).enabled()) {
            return;
        }

        AuraRecodeModule auraModule = Apex.moduleManager.getByClass(AuraRecodeModule.class);
        //  Scaffold scaffoldModule = Apex.moduleManager.getModule(Scaffold.class);
        if ((auraModule.enabled() && auraModule.target != null)) {
            return;
        }

        ScreenHandler screenHandler = mc.player.currentScreenHandler;
        if (!(screenHandler instanceof PlayerScreenHandler playerScreenHandler)) {
            return;
        }

        Slot bestSword = getBestSword(playerScreenHandler);
        Slot perfSwordSlot = screenHandler.getSlot(getSwordSlot().getValue().intValue() + 35);

        Slot bestPickaxe = getBestPickaxe(playerScreenHandler);
        Slot perfPickaxeSlot = screenHandler.getSlot(getPickaxeSlot().getValue().intValue() + 35);

        Slot bestAxe = getBestAxe(playerScreenHandler);
        Slot perfAxeSlot = screenHandler.getSlot(getAxeSlot().getValue().intValue() + 35);

        Slot mostBlocks = getMostBlocks(playerScreenHandler);
        Slot perfBlocksSlot = screenHandler.getSlot(getBlocksSlot().getValue().intValue() + 35);

        Slot bestBow = getBestBow(playerScreenHandler);
        Slot perfBowSlot = screenHandler.getSlot(getBowSlot().getValue().intValue() + 35);

        Slot bestGapple = getBestGapple(playerScreenHandler);
        Slot perfGappleSlot = screenHandler.getSlot(getGappleSlot().getValue().intValue() + 35);

        Slot bestWaterBucket = getWaterBucket(playerScreenHandler);
        Slot perfWaterBucketSlot = screenHandler.getSlot(getWaterBucketSlot().getValue().intValue() + 35);


        InventoryUtil.filterSlots(playerScreenHandler, slot -> !slot.getStack().isEmpty(), true).forEach(validSlot -> {
            if (!canMove(getDelay().getValue().longValue()) || InventoryUtil.isGoodItem(validSlot.getStack())) {
                return;
            }

       //     if (validSlot.getStack().getItem().getComponents().get(DataComponentTypes.EQUIPPABLE) != null) {
       //         return;
       //     }

            arrangeBestSword(screenHandler, perfSwordSlot, bestSword);
            arrangeBestPickaxe(screenHandler, perfPickaxeSlot, bestPickaxe);
            arrangeBestAxe(screenHandler, perfAxeSlot, bestAxe);
            arrangeMostBlocks(screenHandler, perfBlocksSlot, mostBlocks);
            arrangeBestBow(screenHandler, perfBowSlot, bestBow);
            arrangeBestGapple(screenHandler, perfGappleSlot, bestGapple);
            arrangeWaterBucket(screenHandler, perfWaterBucketSlot, bestWaterBucket);

            if (validSlot.getIndex() == perfSwordSlot.getIndex() && validSlot.getStack().isIn(ItemTags.SWORDS)) {
                return;
            }
            if (validSlot.getIndex() == perfPickaxeSlot.getIndex() && validSlot.getStack().isIn(ItemTags.PICKAXES)) {
                return;
            }
            if (validSlot.getIndex() == perfAxeSlot.getIndex() && validSlot.getStack().getItem() instanceof AxeItem) {
                return;
            }
            if (validSlot.getStack().getItem() instanceof BucketItem) {
                return;
            }

            if (validSlot.getIndex() == perfBowSlot.getIndex() && validSlot.getStack().getItem() instanceof BowItem) {
                return;
            }

            if (validSlot.getIndex() == perfGappleSlot.getIndex() && validSlot.getStack().getItem() == Items.GOLDEN_APPLE) {
                return;
            }

            ItemStack stack = validSlot.getStack();
            Item item = stack.getItem();

            if (keepSnowballs.getValue() && item == Items.SNOWBALL) {
                return;
            }

            if (keepFintAndSteal.getValue() && item == Items.FLINT_AND_STEEL) {
                return;
            }

            if (item == Items.FISHING_ROD) {
                return;
            }

            if (keepTNT.getValue() && item == Items.TNT) {
                return;
            }

            if (keepArrows.getValue() && item == Items.ARROW) {
                return;
            }

            if (dropUselessArmor.getValue() && isUselessArmor(playerScreenHandler, validSlot)) {
                InventoryUtil.drop(playerScreenHandler, validSlot.id);
                timer.reset();
                return;
            }
        });
    }

    @EventHook
    public void onPacketRecieve(PacketEvent.Receive event) {
        if (event.getPacket() instanceof ScreenHandlerSlotUpdateS2CPacket slotUpdate
                && slotUpdate.getStack().getItem() != Items.AIR
                && mc.player != null
                && slotUpdate.getSyncId() == mc.player.playerScreenHandler.syncId) {
            timer.reset();
        }
    }


    private void arrangeBestSword(final ScreenHandler screenHandler, final Slot preferredSwordSlot, final Slot bestSwordSlot) {
        if (bestSwordSlot != null && bestSwordSlot.getIndex() != preferredSwordSlot.getIndex()) {
            double bestSwordValue = InventoryUtil.getSwordValue(bestSwordSlot.getStack());
            double preferredSwordValue = InventoryUtil.getSwordValue(preferredSwordSlot.getStack());

            if (bestSwordValue > preferredSwordValue) {
                InventoryUtil.swap(screenHandler, bestSwordSlot.id, preferredSwordSlot.id - 36);
                timer.reset();
            }
        }
    }

    private void arrangeWaterBucket(final ScreenHandler screenHandler, final Slot preferredSlot, final Slot waterBucketSlot) {
        if (waterBucketSlot != null && waterBucketSlot.getIndex() != preferredSlot.getIndex()) {
            InventoryUtil.swap(screenHandler, waterBucketSlot.id, preferredSlot.id - 36);
            timer.reset();
        }
    }

    private Slot getWaterBucket(final ScreenHandler screenHandler) {
        return InventoryUtil.filterSlots(screenHandler,
                slot -> slot.getStack().getItem() == Items.WATER_BUCKET,
                false
        ).stream().findFirst().orElse(null);
    }

    private Slot getBestSword(final ScreenHandler screenHandler) {
        return InventoryUtil.filterSlots(screenHandler, slot -> slot.getStack().isIn(ItemTags.SWORDS), false)
                .stream()
                .max(Comparator.comparing(swordSlot -> InventoryUtil.getSwordValue(swordSlot.getStack())))
                .orElse(null);
    }

    private void arrangeBestPickaxe(final ScreenHandler screenHandler, final Slot preferredPickaxeSlot, final Slot bestPickaxeSlot) {
        if (bestPickaxeSlot != null && bestPickaxeSlot.getIndex() != preferredPickaxeSlot.getIndex()) {
            double bestPickaxeValue = InventoryUtil.getToolValue(bestPickaxeSlot.getStack());
            double preferredPickaxeValue = InventoryUtil.getToolValue(preferredPickaxeSlot.getStack());

            if (bestPickaxeValue > preferredPickaxeValue) {
                InventoryUtil.swap(screenHandler, bestPickaxeSlot.id, preferredPickaxeSlot.id - 36);
                timer.reset();
            }
        }
    }

    private Slot getBestPickaxe(final ScreenHandler screenHandler) {
        return InventoryUtil.filterSlots(screenHandler, slot -> slot.getStack().isIn(ItemTags.PICKAXES), false)
                .stream()
                .max(Comparator.comparing(pickaxeSlot -> InventoryUtil.getToolValue(pickaxeSlot.getStack())))
                .orElse(null);
    }

    private void arrangeBestAxe(final ScreenHandler screenHandler, final Slot preferredAxeSlot, final Slot bestAxeSlot) {
        if (bestAxeSlot != null && bestAxeSlot.getIndex() != preferredAxeSlot.getIndex()) {
            double bestAxeValue = InventoryUtil.getToolValue(bestAxeSlot.getStack());
            double preferredAxeValue = InventoryUtil.getToolValue(preferredAxeSlot.getStack());

            if (bestAxeValue > preferredAxeValue) {
                InventoryUtil.swap(screenHandler, bestAxeSlot.id, preferredAxeSlot.id - 36);
                timer.reset();
            }
        }
    }

    private Slot getBestAxe(final ScreenHandler screenHandler) {
        return InventoryUtil.filterSlots(screenHandler, slot -> slot.getStack().getItem() instanceof AxeItem, false)
                .stream()
                .max(Comparator.comparing(axeSlot -> InventoryUtil.getToolValue(axeSlot.getStack())))
                .orElse(null);
    }

    private Slot getMostBlocks(final ScreenHandler screenHandler) {
        return InventoryUtil.filterSlots(screenHandler, slot ->
                                slot.getStack().getItem() instanceof BlockItem blockItem &&
                                        slot.getStack().getCount() > 0 &&
                                        InventoryUtil.isGoodBlock(blockItem.getBlock())
                        , false)
                .stream()
                .max(Comparator.comparing(blockSlot -> blockSlot.getStack().getCount()))
                .orElse(null);
    }

    private void arrangeMostBlocks(final ScreenHandler screenHandler, final Slot preferredBlockSlot, final Slot mostBlockSlot) {
        if (mostBlockSlot != null && mostBlockSlot.getIndex() != preferredBlockSlot.getIndex()) {
            double mostBlockCount = mostBlockSlot.getStack().getCount();
            double preferredBlockValue = preferredBlockSlot.getStack().getCount();

            if (mostBlockCount > preferredBlockValue) {
                InventoryUtil.swap(screenHandler, mostBlockSlot.id, preferredBlockSlot.id - 36);
                timer.reset();
            }
        }
    }

    private Slot getBestBow(final ScreenHandler screenHandler) {
        return InventoryUtil.filterSlots(screenHandler, slot -> slot.getStack().getItem() instanceof BowItem, false)
                .stream()
                .max(Comparator.comparing(slot ->
                        slot.getStack().getOrDefault(DataComponentTypes.ENCHANTMENTS, null) != null ? 1 : 0))
                .orElse(null);
    }

    private void arrangeBestBow(final ScreenHandler screenHandler, final Slot preferredBowSlot, final Slot bestBowSlot) {
        if (bestBowSlot != null && bestBowSlot.getIndex() != preferredBowSlot.getIndex()) {
            InventoryUtil.swap(screenHandler, bestBowSlot.id, preferredBowSlot.id - 36);
            timer.reset();
        }
    }

    private Slot getBestGapple(final ScreenHandler screenHandler) {
        return InventoryUtil.filterSlots(screenHandler,
                        slot -> slot.getStack().getItem() == Items.GOLDEN_APPLE, false)
                .stream()
                .max(Comparator.comparing(slot -> slot.getStack().getCount()))
                .orElse(null);
    }

    private void arrangeBestGapple(final ScreenHandler screenHandler, final Slot preferredGappleSlot, final Slot bestGappleSlot) {
        if (bestGappleSlot != null && bestGappleSlot.getIndex() != preferredGappleSlot.getIndex()) {
            int bestCount = bestGappleSlot.getStack().getCount();
            int preferredCount = preferredGappleSlot.getStack().getCount();
            if (bestCount > preferredCount) {
                InventoryUtil.swap(screenHandler, bestGappleSlot.id, preferredGappleSlot.id - 36);
                timer.reset();
            }
        }
    }

    public boolean canMove(long delay) {
        if (delay == 0) return true;
        return timer.getElapsedTime() >= delay;
    }

    private boolean isUselessArmor(ScreenHandler handler, Slot slot) {
        ItemStack stack = slot.getStack();

        if (!InventoryUtil.isArmor(stack)) return false;

        EquippableComponent eq = stack.getComponents().get(DataComponentTypes.EQUIPPABLE);
        if (eq == null) return false;

        EquipmentSlot type = eq.slot();


        double currentValue = getArmorValue(stack);

        var sameTypeArmor = handler.slots.stream()
                .filter(s -> !s.getStack().isEmpty())
                .filter(s -> InventoryUtil.isArmor(s.getStack()))
                .filter(s -> {
                    EquippableComponent comp = s.getStack().getComponents().get(DataComponentTypes.EQUIPPABLE);
                    return comp != null && comp.slot() == type;
                })
                .sorted((a, b) -> Double.compare(
                        getArmorValue(b.getStack()),
                        getArmorValue(a.getStack())
                ))
                .toList();

        if (sameTypeArmor.isEmpty()) return false;

        Slot best = sameTypeArmor.get(0);
        double bestValue = getArmorValue(best.getStack());

        if (currentValue < bestValue) return true;

        if (Math.abs(currentValue - bestValue) < 0.01 && best.id != slot.id) return true;

        return false;
    }

    public double getArmorValue(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !InventoryUtil.isArmor(stack)) {
            return 0;
        }

        double base = PlayerUtil.getArmorProtection(stack);

        int prot = InventoryUtil.calculateEnchantmentLevel(stack, net.minecraft.enchantment.Enchantments.PROTECTION);
        int unbreaking = InventoryUtil.calculateEnchantmentLevel(stack, net.minecraft.enchantment.Enchantments.UNBREAKING);
        int thorns = InventoryUtil.calculateEnchantmentLevel(stack, net.minecraft.enchantment.Enchantments.THORNS);
        int proj = InventoryUtil.calculateEnchantmentLevel(stack, net.minecraft.enchantment.Enchantments.PROJECTILE_PROTECTION);

        double score = base;

        score += prot * 0.75;
        score += proj * 0.25;
        score += unbreaking * 0.1;
        score += thorns * 0.2;

        float durability = stack.getDamage() / (float) stack.getMaxDamage();
        score -= durability * 0.2;

        return score;
    }
}
