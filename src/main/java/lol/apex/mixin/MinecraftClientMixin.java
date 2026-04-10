package lol.apex.mixin;

import lol.apex.Apex;
import lol.apex.event.client.ItemFastUseEvent;
import lol.apex.event.player.PlayerOutlineEvent;
import lol.apex.event.player.WorldChangeEvent;
import lol.apex.event.client.PreGameInputEvent;
import lol.apex.event.render.RenderScreenEvent;
import lol.apex.event.render.RenderTickEvent;
import lol.apex.feature.ui.imgui.ImGuiImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    private int itemUseCooldown;

    @Shadow
    @Final
    private Window window;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectConstructorFinish(RunArgs args, CallbackInfo ci) {
        ImGuiImpl.create(window.getHandle());
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void injectClose(CallbackInfo ci) {
        Apex.instance.stop();
        ImGuiImpl.dispose();
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;handleInputEvents()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void prehandleInputs$tick(CallbackInfo ci) {
        PreGameInputEvent event = new PreGameInputEvent();
        Apex.eventBus.post(event);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void injectRender(CallbackInfo ci) {
        Apex.eventBus.post(new RenderTickEvent());
    }

    @Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemEnabled(Lnet/minecraft/resource/featuretoggle/FeatureSet;)Z"))
    private void onDoItemUseHand(CallbackInfo info) {
        ItemFastUseEvent event = new ItemFastUseEvent(itemUseCooldown);
        Apex.eventBus.post(event);

        if (event.isCancelled()) {
            this.itemUseCooldown = event.getCooldown();
        }
    }

    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    public void hasOutline(Entity entity, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        PlayerOutlineEvent event = new PlayerOutlineEvent(entity);
        Apex.eventBus.post(event);

        if (event.isCancelled()) {
            callbackInfoReturnable.setReturnValue(true);
        }
    }

    @ModifyArg(
            method = "updateWindowTitle",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/Window;setTitle(Ljava/lang/String;)V"
            ))
    private String setTitle(String original) {
        return Apex.getWindowTitle();
    } 

    @Inject(method = "joinWorld", at = @At("HEAD"))
    private void onWorldChange(ClientWorld world, CallbackInfo ci) {
        Apex.eventBus.post(new WorldChangeEvent());
    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        RenderScreenEvent event = new RenderScreenEvent();
        event.setScreen(screen);

        Apex.eventBus.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
