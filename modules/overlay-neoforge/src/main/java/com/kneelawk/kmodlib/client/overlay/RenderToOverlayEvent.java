package com.kneelawk.kmodlib.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.neoforged.bus.api.Event;

import org.joml.Matrix4f;

/**
 * Fired in order to render to the overlay.
 */
public class RenderToOverlayEvent extends Event {
    private final LevelRenderer levelRenderer;
    private final PoseStack poseStack;
    private final Matrix4f projectionMatrix;
    private final int renderTick;
    private final float partialTick;
    private final Camera camera;
    private final Frustum frustum;
    private final MultiBufferSource provider;

    public RenderToOverlayEvent(LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix,
                                int renderTick, float partialTick, Camera camera, Frustum frustum,
                                MultiBufferSource provider) {
        this.levelRenderer = levelRenderer;
        this.poseStack = poseStack;
        this.projectionMatrix = projectionMatrix;
        this.renderTick = renderTick;
        this.partialTick = partialTick;
        this.camera = camera;
        this.frustum = frustum;
        this.provider = provider;
    }

    public LevelRenderer getLevelRenderer() {
        return levelRenderer;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public int getRenderTick() {
        return renderTick;
    }

    public float getPartialTick() {
        return partialTick;
    }

    public Camera getCamera() {
        return camera;
    }

    public Frustum getFrustum() {
        return frustum;
    }

    public MultiBufferSource getProvider() {
        return provider;
    }
}
