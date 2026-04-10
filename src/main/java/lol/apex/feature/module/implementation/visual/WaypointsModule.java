package lol.apex.feature.module.implementation.visual;

import dev.toru.clients.eventBus.EventHook;
import lol.apex.event.render.RenderWorldEvent;
import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.feature.module.setting.implementation.BoolSetting;
import lol.apex.feature.waypoint.WaypointRenderer;

@ModuleInfo(
        name = "Waypoints",
        description = "Client-side waypoints to pin-point locations.",
        category = Category.VISUAL
)
public class WaypointsModule extends Module {
    public final BoolSetting background = new BoolSetting("Background", true);

    @EventHook
    public void onRenderWorld(RenderWorldEvent event) {
        WaypointRenderer.onRenderWorld(event);
    }
}
