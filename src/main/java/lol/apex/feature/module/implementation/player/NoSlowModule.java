package lol.apex.feature.module.implementation.player;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.player.PlayerUseMultiplierEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.EnumSetting;
import lol.apex.util.game.PacketUtil;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

@ModuleInfo(
        name = "NoSlow",
        description = "Reduces slowness when using items.",
        category = Category.PLAYER
)
public class NoSlowModule extends Module {
    public static final NoSlowModule INSTANCE = new NoSlowModule();
    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.VANILLA);
    public final BoolSetting onlyOnGround = new BoolSetting("Only On Ground", true);

    @RequiredArgsConstructor
    public enum Mode {
        VANILLA("Vanilla"),
        GRIM("Grim"),
        GRIM_3("Grim 3"),
        HYPIXEL("Hypixel");

        private final String name;

        @Override
        public String toString() {
            return name;
        }
    }

    private int tick = 0;

    @EventHook
    public void onUsingItem(PlayerUseMultiplierEvent e) {
        if (mc.player == null || mc.world == null) {
            tick = 0;
            return;
        }

        if (mc.player.isGliding() || mc.player.isSneaking()) {
            tick = 0;
            return;
        }

        if (onlyOnGround.getValue() && !mc.player.isOnGround()) {
            return;
        }

        if (mode.getValue() == Mode.HYPIXEL) {

            if (mc.player.getMainHandStack().isIn(net.minecraft.registry.tag.ItemTags.SWORDS)) {

                e.setCancelled(true);

                if (tick == 0) {
                    int current = mc.player.getInventory().getSelectedSlot();
                    int next = (current + 1) % 9;

                    PacketUtil.sendPacket(
                            new UpdateSelectedSlotC2SPacket(next)
                    );

                    PacketUtil.sendPacket(
                            new UpdateSelectedSlotC2SPacket(current)
                    );

                    tick++;
                }
            }

            return;
        }

        if (mode.getValue() == Mode.VANILLA || mode.getValue() == Mode.GRIM) {
            e.setCancelled(true);
            return;
        }

        if (mode.getValue() == Mode.GRIM_3) {
            boolean boost = mc.player.age % 3 == 0 || mc.player.age % 4 == 0;
            if (boost) {
                e.setCancelled(true);
            }
        }
    }
}