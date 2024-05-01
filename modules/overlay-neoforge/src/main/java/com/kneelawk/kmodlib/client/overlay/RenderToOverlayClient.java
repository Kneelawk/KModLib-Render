package com.kneelawk.kmodlib.client.overlay;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.Window;

@EventBusSubscriber(Dist.CLIENT)
public class RenderToOverlayClient {
    private static @Nullable Framebuffer framebuffer = null;

    private static boolean isWindowInvalid() {
        Window window = MinecraftClient.getInstance().getWindow();

        return window.getWidth() == 0 || window.getHeight() == 0;
    }

    private static @NotNull Framebuffer getFramebuffer() {
        Window window = MinecraftClient.getInstance().getWindow();

        Framebuffer framebuffer = RenderToOverlayClient.framebuffer;
        if (framebuffer == null) {
            framebuffer = new SimpleFramebuffer(window.getFramebufferWidth(), window.getFramebufferHeight(), true,
                MinecraftClient.IS_SYSTEM_MAC);
            framebuffer.setClearColor(0f, 0f, 0f, 0f);
            RenderToOverlayClient.framebuffer = framebuffer;
        }

        if (window.getFramebufferWidth() != framebuffer.textureWidth ||
            window.getFramebufferHeight() != framebuffer.textureHeight) {
            framebuffer.resize(window.getFramebufferWidth(), window.getFramebufferHeight(),
                MinecraftClient.IS_SYSTEM_MAC);
        }

        return framebuffer;
    }

    @SubscribeEvent
    public static void onWorldRenderEnd(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            if (isWindowInvalid()) return;

            MinecraftClient mc = MinecraftClient.getInstance();

            RenderSystem.setProjectionMatrix(event.getProjectionMatrix(), VertexSorter.BY_DISTANCE);

            Framebuffer framebuffer = getFramebuffer();
            framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);

            framebuffer.beginWrite(false);

            NeoForge.EVENT_BUS.post(
                new RenderToOverlayEvent(event.getLevelRenderer(), event.getPoseStack(), event.getProjectionMatrix(),
                    event.getRenderTick(), event.getPartialTick(), event.getCamera(), event.getFrustum(),
                    RenderToOverlay.CONSUMERS));
//            testRender(event.getPoseStack(), event.getCamera().getPos());

            ((VertexConsumerProvider.Immediate) RenderToOverlay.CONSUMERS).draw();

            mc.getFramebuffer().beginWrite(false);

            // Framebuffer.draw() messes with the projection matrix, so we're keeping a backup.
            Matrix4f projBackup = RenderSystem.getProjectionMatrix();
            VertexSorter sorterBackup = RenderSystem.getVertexSorting();
            RenderSystem.enableBlend();
            framebuffer.draw(mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(), false);
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
