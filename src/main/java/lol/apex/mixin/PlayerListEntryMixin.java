package lol.apex.mixin;

import lol.apex.manager.implementation.CapeManager;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.Identifier;
import net.minecraft.util.AssetInfo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import com.mojang.authlib.GameProfile;

@SuppressWarnings("all")
@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {

    // thisn eeds fixing and idk how it is on ltest
    // latest

    @Shadow public abstract GameProfile getProfile();

    @Inject(method = "getSkinTextures", at = @At("TAIL"), cancellable = true)
    private void getCapeTexture(CallbackInfoReturnable<SkinTextures> cir) {
        String playerName = getProfile().name();
        Identifier capeId = CapeManager.getCapeForUser(playerName);

        if (capeId != null) {
            SkinTextures prev = cir.getReturnValue();

            AssetInfo.TextureAsset capeTexture =
                    new AssetInfo.TextureAssetInfo(capeId);

            SkinTextures newTextures = new SkinTextures(
                    prev.body(), capeTexture, prev.elytra(),
                    prev.model(),
                    prev.secure()
            );

            cir.setReturnValue(newTextures);
        }
    }
}