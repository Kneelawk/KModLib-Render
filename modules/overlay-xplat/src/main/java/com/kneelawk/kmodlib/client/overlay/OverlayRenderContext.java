package com.kneelawk.kmodlib.client.overlay;

import org.joml.Matrix4f;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;

/**
 * The variables available when rendering to an overlay.
 */
public interface OverlayRenderContext {
    /**
     * {@return the vertex buffers that get rendered to the overlay}
     */
    VertexConsumerProvider buffers();

    /**
     * {@return the renderer this overlay is being called from}
     */
    WorldRenderer renderer();

    /**
     * {@return the stack used for rendering}
     */
    MatrixStack stack();

    /**
     * {@return the model-view-matrix used for rendering}
     */
    Matrix4f modelViewMatrix();

    /**
     * {@return the projection matrix that was applied}
     */
    Matrix4f projectionMatrix();

    /**
     * {@return the camera}
     */
    Camera camera();

    /**
     * {@return the frustum}
     */
    Frustum frustum();
}
