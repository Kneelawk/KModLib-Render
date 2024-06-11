package com.kneelawk.kmodlib.client.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.joml.Matrix4f;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;

@EventBusSubscriber(Dist.CLIENT)
public class RenderToOverlayClient {
    private static @Nullable RenderTarget framebuffer = null;

    private static boolean isWindowInvalid() {
        Window window = Minecraft.getInstance().getWindow();

        return window.getScreenWidth() == 0 || window.getScreenHeight() == 0;
    }

    private static @NotNull RenderTarget getFramebuffer() {
        Window window = Minecraft.getInstance().getWindow();

        RenderTarget framebuffer = RenderToOverlayClient.framebuffer;
        if (framebuffer == null) {
            framebuffer = new TextureTarget(window.getWidth(), window.getHeight(), true,
                Minecraft.ON_OSX);
            framebuffer.setClearColor(0f, 0f, 0f, 0f);
            RenderToOverlayClient.framebuffer = framebuffer;
        }

        if (window.getWidth() != framebuffer.width ||
            window.getHeight() != framebuffer.height) {
            framebuffer.resize(window.getWidth(), window.getHeight(),
                Minecraft.ON_OSX);
        }

        return framebuffer;
    }

    @SubscribeEvent
    public static void onWorldRenderEnd(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            if (isWindowInvalid()) return;

            Minecraft mc = Minecraft.getInstance();

            RenderSystem.setProjectionMatrix(event.getProjectionMatrix(), VertexSorting.DISTANCE_TO_ORIGIN);

            RenderTarget framebuffer = getFramebuffer();
            framebuffer.clear(Minecraft.ON_OSX);

            framebuffer.bindWrite(false);

            RenderToOverlay.EVENT.invoker().renderToOverlay(new OverlayLevelRenderContext(event));
//            testRender(event.getPoseStack(), event.getCamera().getPos());

            ((MultiBufferSource.BufferSource) RenderToOverlay.CONSUMERS).endBatch();

            mc.getMainRenderTarget().bindWrite(false);

            // Framebuffer.draw() messes with the projection matrix, so we're keeping a backup.
            Matrix4f projBackup = RenderSystem.getProjectionMatrix();
            VertexSorting sorterBackup = RenderSystem.getVertexSorting();
            RenderSystem.enableBlend();
            framebuffer.blitToScreen(mc.getWindow().getWidth(), mc.getWindow().getHeight(), false);
            RenderSystem.disableBlend();
            RenderSystem.setProjectionMatrix(projBackup, sorterBackup);
        }
    }

//    private static void testRender(MatrixStack stack, Vec3d cameraPos) {
//        stack.push();
//        stack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
//        RenderUtils.drawCube(stack, RenderToOverlay.CONSUMERS.getBuffer(RenderLayer.LINES), 0f, 64f, 0f, 0.5f, 0.5f,
//            0.5f, 0xFFFFFFFF);
//        stack.pop();
//    }
}
