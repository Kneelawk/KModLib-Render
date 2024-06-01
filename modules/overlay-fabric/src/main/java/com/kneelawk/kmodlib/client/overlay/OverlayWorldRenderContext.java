package com.kneelawk.kmodlib.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.Matrix4f;

class OverlayWorldRenderContext implements OverlayRenderContext {
    private final WorldRenderContext delegate;

    OverlayWorldRenderContext(WorldRenderContext delegate) {this.delegate = delegate;}

    @Override
    public MultiBufferSource buffers() {
        return RenderToOverlay.CONSUMERS;
    }

    @Override
    public LevelRenderer renderer() {
        return delegate.worldRenderer();
    }

    @Override
    public PoseStack stack() {
        return delegate.matrixStack();
    }

    @Override
    public Matrix4f modelViewMatrix() {
        return delegate.positionMatrix();
    }

    @Override
    public Matrix4f projectionMatrix() {
        return delegate.projectionMatrix();
    }

    @Override
    public Camera camera() {
        return delegate.camera();
    }

    @Override
    public Frustum frustum() {
        return delegate.frustum();
    }
}
