package lol.apex.feature.module.implementation.movement;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.client.PreUpdateEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.module.setting.implementation.SliderSetting;

@ModuleInfo(
        name = "VehicleFly",
        description = "Allows you to fly using a vehicle.",
        category = Category.MOVEMENT
)
public class VehicleFlyModule extends Module {
    private final BoolSetting correct = new BoolSetting("Fix gravity", true);
    private final SliderSetting gravity = new SliderSetting("Gravity", 0f, -10f, 10f, 0.001f);

    @EventHook
    private void onMotion(PreUpdateEvent e) {
        final var vehicle = mc.player.getVehicle();
        if (vehicle == null) return;
        final var vel = vehicle.getVelocity();
        final var adjust = correct.getValue() ? vehicle.getFinalGravity() : 0;
        vehicle.setVelocity(vel.x, gravity.getValue() + adjust, vel.z);
    }
}
