package lol.apex.mixin;

import lol.apex.Apex;
import lol.apex.feature.module.implementation.visual.AnimationsModule;
import lol.apex.util.animation.ItemAnimationUtil;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class ItemUseRendererMixin {
    @Shadow
    private void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch,
                                       Hand hand, float swingProgress, ItemStack item, float equipProgress,
                                       MatrixStack matrices, OrderedRenderCommandQueue vertexConsumers, int light)
    {}

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"), cancellable = true)
    private void hideShield(AbstractClientPlayerEntity player, float tickDelta, float pitch,
                            Hand hand, float swingProgress, ItemStack item, float equipProgress,
                            MatrixStack matrices, OrderedRenderCommandQueue vertexConsumers, int light,
                            CallbackInfo ci) {
        if (hand == Hand.OFF_HAND && ItemAnimationUtil.isBlocking()) {
            ci.cancel();
        }

        if (hand == Hand.MAIN_HAND && ItemAnimationUtil.getSpoofedItem() != null && ItemAnimationUtil.getSpoofedItem() != item) {
            ItemStack spoofed = ItemAnimationUtil.getSpoofedItem();
            ci.cancel();

            this.renderFirstPersonItem(
                    player,
                    tickDelta,
                    pitch,
                    hand,
                    swingProgress,
                    spoofed,
                    equipProgress,
                    matrices,
                    vertexConsumers,
                    light
            );

        }
    }


    @Redirect(
            method = "renderFirstPersonItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isUsingItem()Z",
                    ordinal = 1
            )
    )
    private boolean hookIsUseItem(AbstractClientPlayerEntity instance) {
        var stack = instance.getMainHandStack();

        if (ItemAnimationUtil.isBlocking() && stack.getComponents().contains(DataComponentTypes.TOOL)) {
            return true;
        }
        return instance.isUsingItem();
    }

    @Redirect(method = "renderFirstPersonItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getActiveHand()Lnet/minecraft/util/Hand;",
            ordinal = 1
    ))
    private Hand hookActiveHand(AbstractClientPlayerEntity instance) {
        var itemStack = instance.getMainHandStack();

        if (itemStack.isIn(ItemTags.SWORDS) && ItemAnimationUtil.isBlocking()) {
            return Hand.MAIN_HAND;
        }

        return instance.getActiveHand();
    }

    @Redirect(method = "renderFirstPersonItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getItemUseTimeLeft()I",
            ordinal = 2
    ))
    private int hookItemUseItem(AbstractClientPlayerEntity instance) {
        var stack = instance.getMainHandStack();

        if (ItemAnimationUtil.getSpoofedItem() != null || (ItemAnimationUtil.isBlocking() && stack.isIn(ItemTags.SWORDS))) {
            return 7200;
        }

        return instance.getItemUseTimeLeft();
    }

    @ModifyArg(
            method = "renderFirstPersonItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEquipOffset(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Arm;F)V",
                    ordinal = 3
            ),
            index = 2)
    private float injectIgnoreBlocking(float equipProgress) {
        if (ItemAnimationUtil.isBlocking() || ItemAnimationUtil.getSpoofedItem() != null) {
            return 0.0F;
        }

        return equipProgress;
    }

    @Redirect(
            method = "renderFirstPersonItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getUseAction()Lnet/minecraft/item/consume/UseAction;"
            )
    )
    private UseAction hookUseAction(ItemStack instance) {
        if (ItemAnimationUtil.isBlocking() && instance.isIn(ItemTags.SWORDS)) { //works only while holding
            //ChatUtils.print("Sword found set use action");
            return UseAction.BLOCK;
        }
        return instance.getUseAction();
    }

    @Inject(
            method = "renderFirstPersonItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V",
                    ordinal = 4,
                    shift = At.Shift.BEFORE
            )
    )
    public void doAnimation(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress,
                            MatrixStack matrices, OrderedRenderCommandQueue vertexConsumers, int light, CallbackInfo ci) {
        float f =  MathHelper.sin((float) (swingProgress * swingProgress * Math.PI));
        AnimationsModule animation = Apex.moduleManager.getByClass(AnimationsModule.class);

        if (animation != null && animation.enabled()) {
            ItemAnimationUtil.animate(matrices, player.getHandSwingProgress(tickDelta), f);
        }
    }

    @Inject(method = "resetEquipProgress", at = @At("HEAD"), cancellable = true)
    private void injectIgnorePlace(Hand hand, CallbackInfo ci) {
        if (ItemAnimationUtil.isBlocking() || ItemAnimationUtil.getSpoofedItem() != null) {
            ci.cancel();
        }
    }
}