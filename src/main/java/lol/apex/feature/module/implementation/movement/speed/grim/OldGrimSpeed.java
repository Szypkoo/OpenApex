package lol.apex.feature.module.implementation.movement.speed.grim;

import lol.apex.event.player.PlayerMoveEvent;
import lol.apex.feature.module.base.SubModule;
import lol.apex.util.player.MoveUtil;

public class OldGrimSpeed extends SubModule {
    public OldGrimSpeed() {
        super("OldGrim", "Speed for Grim, only works when colliding with another player.", "Specific");
    }
    public static void onMove(PlayerMoveEvent event) {
        mc.world.getPlayers().stream()
                .filter(player -> player != mc.player &&
                        mc.player.getBoundingBox().expand(1.0, 1.0, 1.0)
                                .intersects(player.getBoundingBox()))
                .forEach(player -> MoveUtil.moveFlying(0.08));
    }
}
