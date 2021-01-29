package net.vene.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntityRenderer.class)
public class ArmMixin {
    @Redirect(method = "renderArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V", ordinal = 0))
    public void possiblyPreventArmRender(ModelPart modelPart, MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        if (!MinecraftClient.getInstance().getSession().getProfile().getId().toString().equals("e653abb0-fc87-425f-aca1-81a08dc4ee32")) {
            modelPart.render(matrices, vertices, light, overlay);
        }
    }
}