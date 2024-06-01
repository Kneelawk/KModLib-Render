package com.kneelawk.kmodlib.client.overlay;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

import org.joml.Matrix4f;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profiler;

class OverlayWorldRenderContext implements WorldRenderContext {
    private final WorldRenderContext delegate;

    OverlayWorldRenderContext(WorldRenderContext delegate) {this.delegate = delegate;}

    @Override
    public WorldRenderer worldRenderer() {
        return delegate.worldRenderer();
    }

    @Override
    public MatrixStack matrixStack() {
        return delegate.matrixStack();
    }

    @Override
    public RenderTickCounter tickCounter() {
        return delegate.tickCounter();
    }

    @Override
    public boolean blockOutlines() {
        return delegate.blockOutlines();
    }

    @Override
    public Camera camera() {
        return delegate.camera();
    }

    @Override
    public GameRenderer gameRenderer() {
        return delegate.gameRenderer();
    }

    @Override
    public LightmapTextureManager lightmapTextureManager() {
        return delegate.lightmapTextureManager();
    }

    @Override
    public Matrix4f projectionMatrix() {
        return delegate.projectionMatrix();
    }

    @Override
    public Matrix4f positionMatrix() {
        return delegate.positionMatrix();
    }

    @Override
    public ClientWorld world() {
        return delegate.world();
    }

    @Override
    public Profiler profiler() {
        return delegate.profiler();
    }

    @Override
    public boolean advancedTranslucency() {
        return delegate.advancedTranslucency();
    }

    @Override
    public @Nullable VertexConsumerProvider consumers() {
        return RenderToOverlay.CONSUMERS;
    }

    @Override
    public @Nullable Frustum frustum() {
        return delegate.frustum();
    }
}
