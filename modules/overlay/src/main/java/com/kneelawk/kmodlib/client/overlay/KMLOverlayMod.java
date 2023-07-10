package com.kneelawk.kmodlib.client.overlay;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.Window;

public class KMLOverlayMod implements ClientModInitializer {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    private @Nullable Framebuffer framebuffer = null;

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.END.register(this::render);
    }

    private boolean isWindowInvalid() {
        Window window = MC.getWindow();

        return window.getWidth() == 0 || window.getHeight() == 0;
    }

    private @NotNull Framebuffer getFramebuffer() {
        Window window = MC.getWindow();

        Framebuffer framebuffer = this.framebuffer;
        if (framebuffer == null) {
            framebuffer = new SimpleFramebuffer(window.getFramebufferWidth(), window.getFramebufferHeight(), true,
                MinecraftClient.IS_SYSTEM_MAC);
            framebuffer.setClearColor(0f, 0f, 0f, 0f);
            this.framebuffer = framebuffer;
        }

        if (window.getFramebufferWidth() != framebuffer.textureWidth ||
            window.getFramebufferHeight() != framebuffer.textureHeight) {
            framebuffer.resize(window.getFramebufferWidth(), window.getFramebufferHeight(),
                MinecraftClient.IS_SYSTEM_MAC);
        }

        return framebuffer;
    }

    private void render(WorldRenderContext ctx) {
        if (isWindowInvalid()) return;

        WorldRenderContext newCtx = new OverlayWorldRenderContext(ctx);

        Framebuffer framebuffer = getFramebuffer();
        framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);

        framebuffer.beginWrite(false);

        RenderToOverlay.EVENT.invoker().renderToOverlay(newCtx);

        ((VertexConsumerProvider.Immediate) RenderToOverlay.CONSUMERS).draw();

        MC.getFramebuffer().beginWrite(false);

        // Framebuffer.draw() messes with the projection matrix, so we're keeping a backup.
        Matrix4f projBackup = RenderSystem.getProjectionMatrix();
        VertexSorter sorterBackup = RenderSystem.getVertexSorting();
        RenderSystem.enableBlend();
        framebuffer.draw(MC.getWindow().getFramebufferWidth(), MC.getWindow().getFramebufferHeight(), false);
        RenderSystem.disableBlend();
        RenderSystem.setProjectionMatrix(projBackup, sorterBackup);
    }
}
