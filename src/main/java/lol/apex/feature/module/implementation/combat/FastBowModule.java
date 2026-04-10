package lol.apex.feature.module.implementation.combat;


import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.PreUpdateEvent;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.util.game.PacketUtil;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import lol.apex.feature.module.base.Module;

import lol.apex.feature.module.base.*;

@ModuleInfo( 
    name = "FastBow",
    description = "Uses an exploit to make your bow into an overpowered weapon. (1.8)",
    category = Category.COMBAT
)
public class FastBowModule extends Module {
    private final BoolSetting noAir = new BoolSetting("Ground", false);
    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.C03);

    @RequiredArgsConstructor
    public enum Mode {
        C03("C03"),
        C06("C06");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    @EventHook // thus fuxdwse ut
    public void onTick(PreUpdateEvent event) {
        if (mc.player == null) return;

        if (noAir.getValue() && !mc.player.isOnGround()) {
            return;
        }

        if (mc.player.isUsingItem()) {
            ItemStack currentItem = mc.player.getInventory().getSelectedStack();
            if(!currentItem.isEmpty() && currentItem.getItem() instanceof BowItem) {
                PacketUtil.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND,
                        0, mc.player.getYaw(), mc.player.getPitch()));

                for (int i = 0; i < 20; i++) {
                    if (mode.getValue() == Mode.C03) {
                        PacketUtil.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(
                                mc.player.isOnGround(),
                                mc.player.horizontalCollision
                        ));
                    }

                    if (mode.getValue() == Mode.C06) {
                        PacketUtil.sendPacket(new PlayerMoveC2SPacket.Full(
                                mc.player.getX(),
                                mc.player.getY(),
                                mc.player.getZ(),
                                mc.player.getYaw(),
                                mc.player.getPitch(),
                                mc.player.isOnGround(),
                                mc.player.horizontalCollision
                        ));
                    }
                }

                PacketUtil.sendPacket(
                        new PlayerActionC2SPacket(
                                PlayerActionC2SPacket.Action.RELEASE_USE_ITEM,
                                BlockPos.ORIGIN,
                                Direction.DOWN
                        ));

                mc.player.getItemUseTimeLeft();
            }
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }
}