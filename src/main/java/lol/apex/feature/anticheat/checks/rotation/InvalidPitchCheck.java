package lol.apex.feature.anticheat.checks.rotation;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.anticheat.Check;
import net.minecraft.entity.player.PlayerEntity;

public class InvalidPitchCheck extends Check {
    public InvalidPitchCheck(PlayerEntity player) {
        super(player, "Invalid Pitch");
    }

    @Override
    public void onTickPre(ClientTickEvent event) {
        if(getPlayer() == null) return;

        PlayerEntity player = getPlayer();
        final float pitch = Math.abs(player.getPitch());
        final float maxPitch = player.isHoldingOntoLadder() ? 91.2f : 90f;

        if (pitch > maxPitch) {
            flag("pitch=" + pitch);
        }
        super.onTickPre(event);
    }
}
