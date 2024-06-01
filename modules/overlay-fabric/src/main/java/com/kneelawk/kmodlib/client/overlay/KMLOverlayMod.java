package com.kneelawk.kmodlib.client.overlay;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;

public class KMLOverlayMod implements ClientModInitializer {
    private final Minecraft MC = Minecraft.getInstance();
    private @Nullable RenderTarget framebuffer = null;

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.END.register(this::render);
    }

    private boolean isWindowInvalid() {
        Window window = MC.getWindow();

        return window.getScreenWidth() == 0 || window.getScreenHeight() == 0;
    }

    private @NotNull RenderTarget getFramebuffer() {
        Window window = MC.getWindow();

        RenderTarget framebuffer = this.framebuffer;
        if (framebuffer == null) {
            framebuffer = new TextureTarget(window.getWidth(), window.getHeight(), true,
                Minecraft.ON_OSX);
            framebuffer.setClearColor(0f, 0f, 0f, 0f);
            this.framebuffer = framebuffer;
        }

        if (window.getWidth() != framebuffer.width ||
            window.getHeight() != framebuffer.height) {
            framebuffer.resize(window.getWidth(), window.getHeight(),
                Minecraft.ON_OSX);
        }

        return framebuffer;
    }

    private void render(WorldRenderContext ctx) {
        if (isWindowInvalid()) return;

        RenderSystem.setProjectionMatrix(ctx.projectionMatrix(), VertexSorting.DISTANCE_TO_ORIGIN);

        OverlayRenderContext newCtx = new OverlayWorldRenderContext(ctx);

        RenderTarget framebuffer = getFramebuffer();
        framebuffer.clear(Minecraft.ON_OSX);

        framebuffer.bindWrite(false);

        RenderToOverlay.EVENT.invoker().renderToOverlay(newCtx);
//        testRender(newCtx);

        ((MultiBufferSource.BufferSource) RenderToOverlay.CONSUMERS).endBatch();

        MC.getMainRenderTarget().bindWrite(false);

        // Framebuffer.draw() messes with the projection matrix, so we're keeping a backup.
        Matrix4f projBackup = RenderSystem.getProjectionMatrix();
        VertexSorting sorterBackup = RenderSystem.getVertexSorting();
        RenderSystem.enableBlend();
        framebuffer.blitToScreen(MC.getWindow().getWidth(), MC.getWindow().getHeight(), false);
        RenderSystem.disableBlend();
        RenderSystem.setProjectionMatrix(projBackup, sorterBackup);
    }

//    private void testRender(WorldRenderContext ctx) {
//        MatrixStack stack = ctx.matrixStack();
//        stack.push();
//        Vec3d cameraPos = ctx.camera().getPos();
//        stack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
//        RenderUtils.drawCube(stack, RenderToOverlay.CONSUMERS.getBuffer(RenderLayer.LINES), 0f, 64f, 0f, 0.5f, 0.5f,
//            0.5f, 0xFFFFFFFF);
//        stack.pop();
//    }
}
