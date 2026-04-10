package lol.apex.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import lol.apex.feature.ui.screen.ImGuiMainMenu;
import lol.apex.util.CommonVars;

@Mixin(MinecraftClient.class)
public class TitleScreenMixin implements CommonVars {
    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void replaceTitleScreen(Screen screen, CallbackInfo ci) {

        if (screen instanceof TitleScreen || (screen == null && mc.world == null)) {
            mc.setScreen(ImGuiMainMenu.INSTANCE);
            ci.cancel();
        }
    }
}