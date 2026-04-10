package lol.apex.mixin;

import lol.apex.feature.ui.screen.ProxyScreen;
import lol.apex.feature.ui.screen.RouteWarningScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {
    protected MultiplayerScreenMixin() {
        super(Text.empty());
        throw new UnsupportedOperationException("Tried initializing a mixin");
    }

    @Inject(method = "connect", at = @At("HEAD"), cancellable = true)
    private void routeWarning(ServerInfo entry, CallbackInfo ci) {
        final var addr = entry.address;
        if (addr.endsWith(".liquidproxy.net") || addr.endsWith(".liquidbounce.net")) { // LiquidProxy route
            this.client.setScreen(new RouteWarningScreen(this, RouteWarningScreen.RouteType.LIQUID_PROXY, entry));
            ci.cancel();
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addProxyManager(CallbackInfo ci) {
        final var x = 0;
        final var y = this.height - 21;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Proxy Manager"),
                (button) -> this.client.setScreen(new ProxyScreen())
        ).dimensions(x, y, 200, 20).build());
    }
}
