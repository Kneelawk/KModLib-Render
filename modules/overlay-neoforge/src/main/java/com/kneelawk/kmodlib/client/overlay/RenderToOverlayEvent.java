package com.kneelawk.kmodlib.client.overlay;

import net.neoforged.bus.api.Event;

import org.joml.Matrix4f;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Fired in order to render to the overlay.
 */
public class RenderToOverlayEvent extends Event {
    private final WorldRenderer levelRenderer;
    private final MatrixStack poseStack;
    private final Matrix4f modelViewMatrix;
    private final Matrix4f projectionMatrix;
    private final int renderTick;
    private final float partialTick;
    private final Camera camera;
    private final Frustum frustum;
    private final VertexConsumerProvider provider;

    public RenderToOverlayEvent(WorldRenderer levelRenderer, MatrixStack poseStack, Matrix4f modelViewMatrix,
                                Matrix4f projectionMatrix, int renderTick, float partialTick, Camera camera,
                                Frustum frustum, VertexConsumerProvider provider) {
        this.levelRenderer = levelRenderer;
        this.poseStack = poseStack;
        this.modelViewMatrix = modelViewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.renderTick = renderTick;
        this.partialTick = partialTick;
        this.camera = camera;
        this.frustum = frustum;
        this.provider = provider;
    }

    public WorldRenderer getLevelRenderer() {
        return levelRenderer;
    }

    public MatrixStack getPoseStack() {
        return poseStack;
    }

    public Matrix4f getModelViewMatrix() {
        return modelViewMatrix;
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

    public VertexConsumerProvider getProvider() {
        return provider;
    }
}
