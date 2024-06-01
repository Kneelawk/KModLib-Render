package com.kneelawk.kmodlib.client.overlay;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

import org.joml.Matrix4f;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;

class OverlayWorldRenderContext implements OverlayRenderContext {
    private final WorldRenderContext delegate;

    OverlayWorldRenderContext(WorldRenderContext delegate) {this.delegate = delegate;}

    @Override
    public VertexConsumerProvider buffers() {
        return RenderToOverlay.CONSUMERS;
    }

    @Override
    public WorldRenderer renderer() {
        return delegate.worldRenderer();
    }

    @Override
    public MatrixStack stack() {
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
