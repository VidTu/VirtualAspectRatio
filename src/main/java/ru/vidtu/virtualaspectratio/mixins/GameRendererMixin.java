package ru.vidtu.virtualaspectratio.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.vidtu.virtualaspectratio.VARConfig;
import ru.vidtu.virtualaspectratio.VirtualAspectRatio;

/**
 * The Mixin for {@link GameRenderer}.
 * Only used if OptiFabric is not loaded.
 * @author VidTu
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow private float zoom;

    @Shadow private float zoomX;

    @Shadow private float zoomY;

    @Shadow public abstract float method_32796();

    @Shadow public abstract Matrix4f getBasicProjectionMatrix(double fov);

    @Shadow @Final private MinecraftClient client;

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;getBasicProjectionMatrix(D)Lnet/minecraft/util/math/Matrix4f;"))
    public Matrix4f renderWorld_getBasicProjectionMatrix(GameRenderer gr, double fov) {
        if (VARConfig.enabled && !VARConfig.disallowed) return VirtualAspectRatio.varBasicMatrix(client, fov, zoom, zoomX, zoomY, method_32796());
        return getBasicProjectionMatrix(fov);
    }

    @Redirect(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;getBasicProjectionMatrix(D)Lnet/minecraft/util/math/Matrix4f;"))
    public Matrix4f renderHand_getBasicProjectionMatrix(GameRenderer gr, double fov) {
        if (VARConfig.enabled && !VARConfig.disallowed && VARConfig.hand) return VirtualAspectRatio.varBasicMatrix(client, fov, zoom, zoomX, zoomY, method_32796());
        return getBasicProjectionMatrix(fov);
    }
}
