package com.kneelawk.kmodlib.client.overlay;

import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;

public class OverlayLevelRenderContext implements OverlayRenderContext {
    private final RenderLevelStageEvent event;

    public OverlayLevelRenderContext(RenderLevelStageEvent event) {this.event = event;}

    @Override
    public MultiBufferSource buffers() {
        return RenderToOverlay.CONSUMERS;
    }

    @Override
    public LevelRenderer renderer() {
        return event.getLevelRenderer();
    }

    @Override
    public PoseStack stack() {
        return event.getPoseStack();
    }

    @Override
    public Matrix4f modelViewMatrix() {
        return event.getModelViewMatrix();
    }

    @Override
    public Matrix4f projectionMatrix() {
        return event.getProjectionMatrix();
    }

    @Override
    public Camera camera() {
        return event.getCamera();
    }

    @Override
    public Frustum frustum() {
        return event.getFrustum();
    }
}
