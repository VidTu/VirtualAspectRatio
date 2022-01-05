package ru.vidtu.virtualaspectratio.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.vidtu.virtualaspectratio.VARConfig;
import ru.vidtu.virtualaspectratio.VirtualAspectRatio;

/**
 * The Mixin for {@link GameRenderer}.
 * Only used if OptiFabric is loaded and not using <code>named</code> mappings.
 * @author VidTu
 */
@Mixin(GameRenderer.class)
public abstract class OFGameRendererMixin {
    @Shadow
    private float zoom;

    @Shadow private float zoomX;

    @Shadow private float zoomY;

    @Shadow @Final
    private MinecraftClient client;

    @Shadow public abstract Matrix4f getBasicProjectionMatrix(Camera camera, float f, boolean bl);

    @Shadow protected abstract double getFov(Camera camera, float tickDelta, boolean changingFov);

    @Shadow private float viewDistance;

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;getBasicProjectionMatrix(Lnet/minecraft/client/render/Camera;FZ)Lnet/minecraft/util/math/Matrix4f;"))
    public Matrix4f renderWorld_getBasicProjectionMatrix(GameRenderer gr, Camera camera, float f, boolean bl) {
        if (VARConfig.enabled && !VARConfig.disallowed) return VirtualAspectRatio.varBasicMatrix(client, getFov(camera, f, bl), zoom, zoomX, zoomY, viewDistance * 4F);
        return getBasicProjectionMatrix(camera, f, bl);
    }

    @Redirect(method = "Lnet/minecraft/class_757;renderHand(Lnet/minecraft/class_4587;Lnet/minecraft/class_4184;FZZZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;getBasicProjectionMatrix(Lnet/minecraft/client/render/Camera;FZ)Lnet/minecraft/util/math/Matrix4f;"), remap = false)
    public Matrix4f renderHand_getBasicProjectionMatrix_OF(GameRenderer gr, Camera camera, float f, boolean bl) {
        if (VARConfig.enabled && !VARConfig.disallowed && VARConfig.hand) return VirtualAspectRatio.varBasicMatrix(client, getFov(camera, f, bl), zoom, zoomX, zoomY, viewDistance * 4F);
        return getBasicProjectionMatrix(camera, f, bl);
    }
}
