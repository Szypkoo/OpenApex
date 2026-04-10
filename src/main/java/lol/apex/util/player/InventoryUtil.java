package lol.apex.util.player;


import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lol.apex.util.CommonVars;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BeaconBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BellBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.CakeBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.block.CartographyTableBlock;
import net.minecraft.block.CaveVinesBodyBlock;
import net.minecraft.block.CaveVinesHeadBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.CobwebBlock;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.block.DecoratedPotBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.DragonEggBlock;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.GrindstoneBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.LightBlock;
import net.minecraft.block.LoomBlock;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SlimeBlock;
import net.minecraft.block.SmithingTableBlock;
import net.minecraft.block.StonecutterBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.EmptyBlockView;

public class InventoryUtil implements CommonVars {
    private InventoryUtil() {

    }

    public static int getBlockFromHotbar() {
        for (int i = 0; i < 9; i++) {
            var stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof net.minecraft.item.BlockItem) {
                return i;
            }
        }
        return -1;
    }

    private static int previousSlot = -1; 

    public static void setInventorySlot(int slot) {
		mc.player.getInventory().setSelectedSlot(slot);
     //   ((lol.karane.mixin.interfaces.ClientPlayerInteractionAccessor)mc.interactionManager).syncSelectedSlot();
    }

    public static int getAxeSlot() {
        Inventory playerInventory = mc.player.getInventory();

        for (int itemIndex = 0; itemIndex < 9; itemIndex++) {
            if (playerInventory.getStack(itemIndex).getItem() instanceof AxeItem)
                return itemIndex;
        }

        return -1;
    }

    public static boolean selectItemFromHotbar(Item item) {
        return selectItemFromHotbar(i -> i == item);
    }

    public static boolean selectItemFromHotbar(Predicate<Item> item) {
        PlayerInventory inv = mc.player.getInventory();

        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = inv.getStack(i);
            if (!item.test(itemStack.getItem()))
                continue;

            inv.setSelectedSlot(i);
            return true;
        }

        return false;
    }

    public static boolean selectAxe() {
        int itemIndex = getAxeSlot();

        if (itemIndex != -1) {
            mc.player.getInventory().setSelectedSlot(itemIndex);
            return true;
        } else return false;
    }

    public static FindItemResult findItem(Item item) {
        return find(s -> s.isOf(item));
    }  

    public static int findItemInHotbar(Item item) {
        if (mc.player == null) return -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isOf(item)) {
                return i;
            }
        }
        return -1;
    }

    public static void swap(final ScreenHandler screenHandler, final int originalSlot, final int newSlot) {
        mc.interactionManager.clickSlot(screenHandler.syncId, originalSlot, newSlot, SlotActionType.SWAP, mc.player);
    }

    public static FindItemResult find(Predicate<ItemStack> predicate) {
        if(mc.player == null) {
            return new FindItemResult(-1, 0);
        } 

        int slot = -1; 
        int count = 0; 

        for(int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if(predicate.test(stack)) {
                if(slot == -1) slot = i; 
                count += stack.getCount();
            }
        }
        return new FindItemResult(slot, count);
    }

    public static List<Slot> filterSlots(final ScreenHandler screenHandler, final Predicate<Slot> filterCondition, final boolean shuffle) {
        final List<Slot> filteredSlots = screenHandler.slots.stream().filter(filterCondition).collect(Collectors.toList());

        if (shuffle)
            Collections.shuffle(filteredSlots);

        return filteredSlots;
    }
     
    public static void move(int from, int to) {
        click(SlotUtil.indexToId(from));
        click(SlotUtil.indexToId(to));
    } 

    public static void pickup(int i) {
        click(SlotUtil.indexToId(i));
    } 

    public static void click(int id) {
        mc.interactionManager.clickSlot( 
            mc.player.currentScreenHandler.syncId, 
            id, 0, SlotActionType.PICKUP, mc.player
        ); 
    } 

    public static boolean swap(int slot, boolean swpBack) {
        if(slot == SlotUtil.offhand) return true;
        if(slot < 0 || slot > 8) return false;
        if(swpBack && previousSlot == -1) previousSlot = mc.player.getInventory().getSelectedSlot();
        else if (!swpBack) previousSlot = -1; 
        mc.player.getInventory().setSelectedSlot(slot);

        return true;
    }

    public record FindItemResult(int slot, int count) {
        public boolean found() {
            return slot != -1 && count > 0;
        }
    } 

    public static boolean isArmor(ItemStack stack) {
        if(stack.getItem() == Items.PLAYER_HEAD || stack.getItem() == Items.PUMPKIN) {
            return false;
        } 

        return stack.getComponents().get(DataComponentTypes.EQUIPPABLE) != null;
    }
    public static boolean isInventoryFull() {
        return false;
    }

    public static int calculateEnchantmentLevel(final ItemStack itemStack, final RegistryKey<Enchantment> enchantment) {
        final DynamicRegistryManager drm = mc.world.getRegistryManager();
        final RegistryWrapper.Impl<Enchantment> registryWrapper = drm.getOrThrow(RegistryKeys.ENCHANTMENT);
        return EnchantmentHelper.getLevel(registryWrapper.getOrThrow(enchantment), itemStack);
    }     

    public static void drop(ScreenHandler handler, int slot) {
        mc.interactionManager.clickSlot(handler.syncId, slot, 1, SlotActionType.THROW, mc.player);
    } 

    public static void shiftClick(final ScreenHandler screenHandler, final int slot, final int mouseButton) {
        mc.interactionManager.clickSlot(screenHandler.syncId, slot, mouseButton, SlotActionType.QUICK_MOVE, mc.player);
    } 

    public static boolean isGoodItem(ItemStack stack) {
        Item item = stack.getItem(); 

        if (item == Items.PLAYER_HEAD || item == Items.PUMPKIN || item == Items.CARVED_PUMPKIN) {
            return false;
        }

        if (item instanceof BlockItem blockItem) {
            return isGoodBlock(blockItem.getBlock());
        }

        return item instanceof EnderPearlItem
        || item instanceof PotionItem
        || item instanceof ShieldItem
        || item instanceof FireChargeItem
        || item.getComponents().contains(DataComponentTypes.FOOD);
    } 

    public static double getSwordValue(ItemStack stack) {
        if(!(stack.isIn(ItemTags.SWORDS))) {
            return 0.0;
        } 

        double score = PlayerUtil.getStackAttackDamage(stack);

        final int sharpnessLevel = calculateEnchantmentLevel(stack, Enchantments.SHARPNESS) + 1;
        score *= sharpnessLevel; 

        score += calculateEnchantmentLevel(stack, Enchantments.FIRE_ASPECT); 
        float durabilityRatio = stack.getDamage() / (float) stack.getMaxDamage(); 
        score -= durabilityRatio * 0.1; 
        return score;
    } 

    public static double getArmorValue(ItemStack stack) {
        if(!isArmor(stack)) {
            return 0.0;
        } 

        double score = PlayerUtil.getArmorProtection(stack);
        int protectionLevel = calculateEnchantmentLevel(stack, Enchantments.PROTECTION); 
        score *= protectionLevel; 

        score += calculateEnchantmentLevel(stack, Enchantments.THORNS); 
        score += calculateEnchantmentLevel(stack, Enchantments.UNBREAKING) * 0.5; 
        score += calculateEnchantmentLevel(stack, Enchantments.PROJECTILE_PROTECTION) * 0.25;
        float durabilityRatio = stack.getDamage() / (float) stack.getMaxDamage(); 
        score -= durabilityRatio * 0.1;
        return score;
    } 

    public static double getToolValue(ItemStack stack) {
        ToolComponent component = stack.get(DataComponentTypes.TOOL); 
        if(component == null) return 0; 

        double score = component.damagePerBlock(); 

        int efficencyLevel = calculateEnchantmentLevel(stack, Enchantments.EFFICIENCY); 
        score *= efficencyLevel; 
        score += calculateEnchantmentLevel(stack, Enchantments.UNBREAKING); 
        float durabilityRatio = stack.getDamage() / (float) stack.getMaxDamage(); 
        score -= durabilityRatio * 0.1;
        return score;
    }


    public static boolean isGoodBlock(final Block block) {
        return !isBlockInteractable(block)
                && block.getDefaultState().getOutlineShape(EmptyBlockView.INSTANCE, mc.player.getBlockPos(), ShapeContext.of(mc.player)) == VoxelShapes.fullCube()
                && !(block instanceof TntBlock)
                && !(block instanceof FallingBlock);
    }

    public static boolean isBlockInteractable(final Block block) {
        return interactableBlocks.contains(block);
    }

    private static final List<Block> interactableBlocks = Registries.BLOCK.stream()
            .filter(block ->
                    block instanceof TrapdoorBlock ||
                            block instanceof SweetBerryBushBlock ||
                            block instanceof AbstractFurnaceBlock ||
                            block instanceof AbstractSignBlock ||
                            block instanceof AnvilBlock ||
                            block instanceof BarrelBlock ||
                            block instanceof BeaconBlock ||
                            block instanceof BedBlock ||
                            block instanceof BellBlock ||
                            block instanceof BrewingStandBlock ||
                            block instanceof ButtonBlock ||
                            block instanceof CakeBlock ||
                            block instanceof CandleCakeBlock ||
                            block instanceof CartographyTableBlock ||
                            block instanceof CaveVinesBodyBlock ||
                            block instanceof CaveVinesHeadBlock ||
                            block instanceof ChestBlock ||
                            block instanceof ChiseledBookshelfBlock ||
                            block instanceof CommandBlock ||
                            block instanceof ComparatorBlock ||
                            block instanceof ComposterBlock ||
                            block instanceof CraftingTableBlock ||
                            block instanceof DaylightDetectorBlock ||
                            block instanceof DecoratedPotBlock ||
                            block instanceof DispenserBlock ||
                            block instanceof DoorBlock ||
                            block instanceof DragonEggBlock ||
                            block instanceof EnchantingTableBlock ||
                            block instanceof EnderChestBlock ||
                            block instanceof FenceBlock ||
                            block instanceof FenceGateBlock ||
//                            block instanceof TableBloc ||
                            block instanceof FlowerPotBlock ||
                            block instanceof GrindstoneBlock ||
                            block instanceof HopperBlock ||
                            block instanceof JigsawBlock ||
                            block instanceof JukeboxBlock ||
                            block instanceof LecternBlock ||
                            block instanceof LeverBlock ||
                            block instanceof LightBlock ||
                            block instanceof LoomBlock ||
                            block instanceof NoteBlock ||
                            block instanceof PistonExtensionBlock ||
                            block instanceof RedstoneWireBlock ||
                            block instanceof RepeaterBlock ||
                            block instanceof RespawnAnchorBlock ||
                            block instanceof ShulkerBoxBlock ||
                            block instanceof SmithingTableBlock ||
                            block instanceof StonecutterBlock ||
                            block instanceof FlowerBlock ||
                            block instanceof StructureBlock ||
                            block instanceof SlimeBlock ||
                            block instanceof CobwebBlock)
            .toList();

    public static int findSplashPotion(StatusEffect type, int duration, int amplifier) {
        PlayerInventory inv = mc.player.getInventory();
        StatusEffectInstance potion = new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(type), duration, amplifier);

        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = inv.getStack(i);

            if (!(itemStack.getItem() instanceof SplashPotionItem))
                continue;

            //String s = PotionUtil.getPotion(itemStack).getEffects().toString();
            String s = itemStack.get(DataComponentTypes.POTION_CONTENTS).getEffects().toString();
            if (s.contains(potion.toString())) {
                return i;
            }
        }

        return -1;
    }

    public static boolean isSplashPotion(StatusEffect type, int duration, int amplifier, ItemStack itemStack) {
        StatusEffectInstance potion = new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(type), duration, amplifier);

        return itemStack.getItem() instanceof SplashPotionItem &&
                itemStack.get(DataComponentTypes.POTION_CONTENTS).getEffects().toString().contains(potion.toString());
    }

        public static boolean findAndSelectItem(Class<? extends Item> itemClass) {
        int slot = findItemInHotbarClass(itemClass);
        if (slot != -1) {
            mc.player.getInventory().setSelectedSlot(slot);
            return true;
        }
        return false;
    }

    public static int findItemInHotbarClass(Class<? extends Item> itemClass) {
        if (mc.player == null) return -1;

        Inventory inventory = mc.player.getInventory();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && itemClass.isInstance(stack.getItem())) {
                return i;
            }
        }
        return -1;
    }
}