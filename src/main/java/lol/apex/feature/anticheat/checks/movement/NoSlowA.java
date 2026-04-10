package lol.apex.feature.anticheat.checks.movement;

import lol.apex.event.client.ClientTickEvent;
import lol.apex.feature.anticheat.Check;
import lol.apex.util.CommonUtil;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

public class NoSlowA extends Check {
    private int buf;

    public NoSlowA(PlayerEntity player) {
        super(player, "No Slow (A)");
    }

    // falses on ice, need to exempt or be more lenient on ice

    @Override
    public void onTickPre(ClientTickEvent event) {
        if(getPlayer() == null) {
            return;
        }

        if(!getPlayer().isUsingItem()) {
            buf = Math.max(buf -3, 0);
            return;
        }

        double deltaX = getPlayer().getX() - getPlayer().lastX;
        double deltaZ = getPlayer().getZ() - getPlayer().lastZ;
        double deltaXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        double maxAllowed = 0.0568;

        if(getPlayer().hasVehicle() || getPlayer().isClimbing() || getPlayer().knockedBack) {
            CommonUtil.log("exempted from noslow check");
            return;
        }
        if (getPlayer().hasStatusEffect(StatusEffects.SPEED)){
            StatusEffectInstance speedEffect = getPlayer().getStatusEffect(StatusEffects.SPEED);

           maxAllowed += 0.2 * (speedEffect.getAmplifier() + 1);
           CommonUtil.log("has speed");
        }

        if(deltaXZ > maxAllowed) {
            if(buf++>17) {
                flag(String.format("delta=%3f", deltaXZ));
            }
        } else {
            buf = Math.max(buf -5, 0);
        }

        super.onTickPre(event);
    }
}
