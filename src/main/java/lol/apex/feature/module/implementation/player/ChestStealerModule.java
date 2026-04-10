package lol.apex.feature.module.implementation.player;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.player.InventoryUtil;
import lol.apex.util.math.TimerUtil;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.GenericContainerScreenHandler;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

@ModuleInfo(
        name = "ChestStealer",
        description = "Automatically loots open chests.",
        category = Category.PLAYER
)
public class ChestStealerModule extends Module {
    public final SliderSetting stealDelay = new SliderSetting("Steal Delay", 25, 0f, 500f, 1);
    public final BoolSetting smart = new BoolSetting("Smart", false);

    private TimerUtil timer = new TimerUtil();

    @EventHook
    public void onPreUpdate(ClientTickEvent event) {
        if (!(mc.currentScreen instanceof GenericContainerScreen container)) return;

        GenericContainerScreenHandler screenHandler = container.getScreenHandler();
        Inventory chestInventory = screenHandler.getInventory();

        if (!container.getTitle().getString().toLowerCase().contains("chest")) return;
        if (chestInventory.isEmpty() || InventoryUtil.isInventoryFull()) {
            container.close();
        }

        Map<EquipmentSlot, ItemStack> bestChestArmor = getBestChestArmor(chestInventory);
        ItemStack bestChestSword = getBestChestSword(chestInventory);
        ItemStack bestChestPickaxe = getBestChestTool(chestInventory, ItemTags.PICKAXES);
        ItemStack bestChestAxe = getBestChestTool(chestInventory, ItemTags.AXES);

        boolean tookItem = false;
        for (int i = 0; i < chestInventory.size(); i++) {
            ItemStack stack = chestInventory.getStack(i);
            if (stack.isEmpty()) continue;

            if (canMove() && (shouldTake(stack, bestChestArmor, bestChestSword, bestChestPickaxe, bestChestAxe) || !smart.getValue())) {
                InventoryUtil.shiftClick(screenHandler, i, 0);
                timer.reset();
                tookItem = true;
                break;
            }
        }

        if (smart.getValue() && !tookItem) {
            boolean hasValuableLeft = false;
            for (int i = 0; i < chestInventory.size(); i++) {
                ItemStack stack = chestInventory.getStack(i);
                if (stack.isEmpty()) continue;
                if (shouldTake(stack, bestChestArmor, bestChestSword, bestChestPickaxe, bestChestAxe)) {
                    hasValuableLeft = true;
                    break;
                }
            }
            if (!hasValuableLeft) {
                container.close();
            }
        }
    }


    public boolean shouldTake(ItemStack stack,
                              Map<EquipmentSlot, ItemStack> bestChestArmor,
                              ItemStack bestChestSword,
                              ItemStack bestChestPickaxe,
                              ItemStack bestChestAxe) {
        if (InventoryUtil.isGoodItem(stack)) {
            return true;
        }

        if (stack.isIn(ItemTags.SWORDS)) {
            final double value = InventoryUtil.getSwordValue(stack);
            final double current = InventoryUtil.getSwordValue(getBestHotbarSword());

            return stack == bestChestSword && value > current;
        }

        if (stack.isIn(ItemTags.PICKAXES)) {
            final double value = InventoryUtil.getToolValue(stack);
            final double current = InventoryUtil.getToolValue(getBestHotbarTool(ItemTags.PICKAXES));

            return stack == bestChestPickaxe && value > current;
        }

        if (stack.isIn(ItemTags.AXES)) {
            final double value = InventoryUtil.getToolValue(stack);
            final double current = InventoryUtil.getToolValue(getBestHotbarAxe());

            return stack == bestChestAxe && value > current;
        }

        if (!InventoryUtil.isArmor(stack)) return false;

        final EquippableComponent equip = stack.getComponents().get(DataComponentTypes.EQUIPPABLE);
        if (equip == null) return false;


        final EquipmentSlot slot = equip.slot();
        final ItemStack currentEquipped = mc.player.getEquippedStack(slot);
        final ItemStack bestInChest = bestChestArmor.getOrDefault(slot, ItemStack.EMPTY);

        if (stack != bestInChest) return false;


        final double stackValue = InventoryUtil.getArmorValue(stack);
        final double equippedValue = InventoryUtil.getArmorValue(currentEquipped);

        return stackValue > equippedValue;

    }

    public Map<EquipmentSlot, ItemStack> getBestChestArmor(Inventory chest) {
        return IntStream.range(0, chest.size())
                .mapToObj(chest::getStack)
                .filter(InventoryUtil::isArmor)
                .map(stack -> {
                    final EquippableComponent equip = stack.getComponents().get(DataComponentTypes.EQUIPPABLE);
                    return equip != null ? Map.entry(equip.slot(), stack) : null;
                })
                .filter(Objects::nonNull)
                .collect(HashMap::new, (map, entry) -> {
                    map.merge(entry.getKey(), entry.getValue(), (existing, replacement) ->
                            InventoryUtil.getArmorValue(replacement) > InventoryUtil.getArmorValue(existing)
                                    ? replacement : existing);
                }, HashMap::putAll);
    }

    public ItemStack getBestChestSword(Inventory chest) {
        return IntStream.range(0, chest.size())
                .mapToObj(chest::getStack)
                .filter(stack -> stack.isIn(ItemTags.SWORDS))
                .max(Comparator.comparingDouble(InventoryUtil::getSwordValue))
                .orElse(ItemStack.EMPTY);
    }

    public ItemStack getBestChestTool(Inventory chest, TagKey<Item> tag) {
        return IntStream.range(0, chest.size())
                .mapToObj(chest::getStack)
                .filter(stack -> stack.isIn(tag))
                .max(Comparator.comparingDouble(InventoryUtil::getToolValue))
                .orElse(ItemStack.EMPTY);
    }

    private ItemStack getBestHotbarSword() {
        return IntStream.range(0, 9)
                .mapToObj(i -> mc.player.getInventory().getStack(i))
                .filter(stack -> stack.isIn(ItemTags.SWORDS))
                .max(Comparator.comparingDouble(InventoryUtil::getSwordValue))
                .orElse(ItemStack.EMPTY);
    }

    private ItemStack getBestHotbarTool(TagKey<Item> tag) {
        return IntStream.range(0, 9)
                .mapToObj(i -> mc.player.getInventory().getStack(i))
                .filter(stack -> stack.isIn(tag))
                .max(Comparator.comparingDouble(InventoryUtil::getToolValue))
                .orElse(ItemStack.EMPTY);
    }

    private ItemStack getBestHotbarAxe() {
        return IntStream.range(0, 9)
                .mapToObj(i -> mc.player.getInventory().getStack(i))
                .filter(stack -> stack.getItem() instanceof AxeItem)
                .max(Comparator.comparingDouble(InventoryUtil::getToolValue))
                .orElse(ItemStack.EMPTY);
    }

    public boolean canMove() {
        final long delayMs = stealDelay.getValue().longValue();
        return delayMs == 0 || timer.passed(delayMs, true);
    }
}
