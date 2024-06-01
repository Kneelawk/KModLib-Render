package com.kneelawk.kmodlib.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.Matrix4f;

/**
 * The variables available when rendering to an overlay.
 */
public interface OverlayRenderContext {
    /**
     * {@return the vertex buffers that get rendered to the overlay}
     */
    MultiBufferSource buffers();

    /**
     * {@return the renderer this overlay is being called from}
     */
    LevelRenderer renderer();

    /**
     * {@return the stack used for rendering}
     */
    PoseStack stack();

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
