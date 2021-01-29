package net.vene.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.PlayerSkinTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;

@Mixin(PlayerSkinTexture.class)
public abstract class SkinMixin {
    @Shadow
    private static void stripAlpha(NativeImage image, int x1, int y1, int x2, int y2) {}
    
    private static boolean shouldPreventStrip = false;
    
    @Inject(method = "loadTexture", at = @At("HEAD"))
    public void saveInstance(InputStream stream, CallbackInfoReturnable<NativeImage> cir) {
        if (MinecraftClient.getInstance().getSession().getProfile().getId().toString().equals("e653abb0-fc87-425f-aca1-81a08dc4ee32")) {
            shouldPreventStrip = true;
        }
    }
    
    @Redirect(method = "remapTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/PlayerSkinTexture;stripAlpha(Lnet/minecraft/client/texture/NativeImage;IIII)V"))
    private static void preventAlphaStrip(NativeImage image, int x1, int y1, int x2, int y2) {
        if (!shouldPreventStrip) {
            stripAlpha(image, x1, y1, x2, y2);
        }
    }
    
    @Inject(method = "remapTexture", at = @At("RETURN"))
    private static void resetStripping(NativeImage image, CallbackInfoReturnable<NativeImage> cir) {
        shouldPreventStrip = false;
    }
}
