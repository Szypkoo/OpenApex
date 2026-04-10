package lol.apex.feature.module.implementation.player;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.Apex;
import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.implementation.combat.AuraRecodeModule;
import lol.apex.feature.module.implementation.movement.InventoryMoveModule;
import lol.apex.feature.module.setting.implementation.SliderSetting;
import lol.apex.util.player.InventoryUtil;
import lol.apex.util.player.PlayerUtil;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.Comparator;

@ModuleInfo(
        name = "AutoArmor",
        description = "Automatically equips the best armor.",
        category = Category.PLAYER
)
public class AutoArmorModule extends Module {

    public final SliderSetting delay = new SliderSetting("Delay", 75, 0f, 500f, 1);

    @EventHook
    public void onTick(ClientTickEvent event) {
        if (mc.player == null) return;

        if (!(mc.currentScreen instanceof InventoryScreen) &&
                !Apex.moduleManager.getByClass(InventoryMoveModule.class).enabled()) {
            return;
        }

        var aura = Apex.moduleManager.getByClass(AuraRecodeModule.class);
        if (aura.enabled() && aura.target != null) return;

        ScreenHandler handler = mc.player.currentScreenHandler;
        if (!(handler instanceof PlayerScreenHandler playerHandler)) return;

        InventoryManagerModule manager = Apex.moduleManager.getByClass(InventoryManagerModule.class);

        if (!manager.canMove(delay.getValue().longValue())) return;

        for (EquipmentSlot type : EquipmentSlot.values()) {

            Slot bestSlot = playerHandler.slots.stream()
                    .filter(s -> isArmorForSlot(s, type))
                    .max(Comparator.comparingDouble(s -> getArmorValue(s.getStack())))
                    .orElse(null);

            if (bestSlot == null) continue;

            ItemStack equipped = mc.player.getEquippedStack(type);
            double equippedValue = getArmorValue(equipped);
            double bestValue = getArmorValue(bestSlot.getStack());

            if (equipped.isEmpty() || bestValue > equippedValue) {
                InventoryUtil.shiftClick(playerHandler, bestSlot.id, 0);
                manager.timer.reset();
                return;
            }
        }
    }

    private boolean isArmorForSlot(Slot slot, EquipmentSlot type) {
        ItemStack stack = slot.getStack();

        if (stack.isEmpty() || !InventoryUtil.isArmor(stack)) return false;

        EquippableComponent eq = stack.getComponents().get(DataComponentTypes.EQUIPPABLE);
        return eq != null && eq.slot() == type;
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