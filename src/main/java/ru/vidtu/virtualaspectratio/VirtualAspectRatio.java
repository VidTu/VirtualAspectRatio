package ru.vidtu.virtualaspectratio;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.CompletableFuture;

/**
 * Main class for the VirtualAspectRatio mod.
 * @author VidTu
 * @apiNote Why I've documented everything?
 */
public class VirtualAspectRatio implements ClientModInitializer {
    public static final Identifier PACKET_IDENTIFIER = new Identifier("virtualaspectratio", "main");
    public static final Logger LOG = LogManager.getLogger("VirtualAspectRatio");
    public static KeyBinding keyBindToggle;
    public static KeyBinding keyBindConfigGui;
    @Override
    public void onInitializeClient() {
        VARConfig.load();
        keyBindToggle = KeyBindingHelper.registerKeyBinding(new KeyBinding("virtualaspectratio.toggle", GLFW.GLFW_KEY_UNKNOWN, "VirtualAspectRatio"));
        keyBindConfigGui = KeyBindingHelper.registerKeyBinding(new KeyBinding("virtualaspectratio.config", GLFW.GLFW_KEY_UNKNOWN, "VirtualAspectRatio"));
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (keyBindToggle.wasPressed()) {
                VARConfig.enabled = !VARConfig.enabled;
                if (VARConfig.disallowed) {
                    mc.inGameHud.setOverlayMessage(new TranslatableText("virtualaspectratio.servertoggle.off")
                            .formatted(Formatting.DARK_RED).formatted(Formatting.BOLD), false);
                } else if (VARConfig.enabled) {
                    mc.inGameHud.setOverlayMessage(new TranslatableText("virtualaspectratio.toggle.on")
                            .formatted(Formatting.GREEN).formatted(Formatting.BOLD), false);
                } else {
                    mc.inGameHud.setOverlayMessage(new TranslatableText("virtualaspectratio.toggle.off")
                            .formatted(Formatting.RED).formatted(Formatting.BOLD), false);
                }
            }
            if (keyBindConfigGui.wasPressed()) {
                mc.setScreen(new VARConfigScreen(null));
            }
        });
        ClientPlayConnectionEvents.JOIN.register((h, s, c) -> VARConfig.disallowed = false);
        ClientPlayConnectionEvents.DISCONNECT.register((h, c) -> VARConfig.disallowed = false);
        ClientPlayNetworking.registerGlobalReceiver(PACKET_IDENTIFIER, (mc, h, buf, send) -> {
            VARConfig.disallowed = buf.readBoolean();
            if (VARConfig.disallowed) {
                mc.inGameHud.getChatHud().addMessage(new TranslatableText("virtualaspectratio.servertoggle.off").formatted(Formatting.RED));
            } else {
                mc.inGameHud.getChatHud().addMessage(new TranslatableText("virtualaspectratio.servertoggle.on").formatted(Formatting.GREEN));
            }
        });
    }

    /**
     * Implementation of {@link net.minecraft.client.render.GameRenderer#getBasicProjectionMatrix(double)} using custom {@link VARConfig#aspectRatio}.
     * @param mc Minecraft client instance
     * @param fov FOV parameter from the {@link net.minecraft.client.render.GameRenderer#getBasicProjectionMatrix(double)} method
     * @param zoom Zoom field from the {@link net.minecraft.client.render.GameRenderer} class
     * @param zoomX Zoom X field from the {@link net.minecraft.client.render.GameRenderer} class
     * @param zoomY Zoom Y field from the {@link net.minecraft.client.render.GameRenderer} class
     * @param farPlaneView Return value of the the {@link GameRenderer#method_32796()} method
     * @return Coolest basic matrix. (and wide)
     */
    public static Matrix4f varBasicMatrix(MinecraftClient mc, double fov, float zoom, float zoomX, float zoomY, float farPlaneView) {
        var matrixStack = new MatrixStack();
        matrixStack.peek().getModel().loadIdentity();
        if (zoom != 1F) {
            matrixStack.translate(zoomX, -zoomY, 0D);
            matrixStack.scale(zoom, zoom, 1F);
        }
        matrixStack.peek().getModel().multiply(Matrix4f.viewboxMatrix(fov, VARConfig.aspectRatioLog *
                        (VARConfig.respectDisplayRatio?((float)mc.getWindow().getFramebufferWidth() /
                                (float)mc.getWindow().getFramebufferHeight()):1F),
                0.05F, farPlaneView));
        return matrixStack.peek().getModel();
    }
}
