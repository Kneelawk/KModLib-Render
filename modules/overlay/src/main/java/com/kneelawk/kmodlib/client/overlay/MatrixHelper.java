package com.kneelawk.kmodlib.client.overlay;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import com.kneelawk.kmodlib.client.overlay.mixin.GameRendererAccessor;

/**
 * Matrix utilities.
 */
public class MatrixHelper {
    /**
     * Sets up the projection matrix for 3D rendering use in {@link WorldRenderEvents#END}
     * because it is not guaranteed to be intact from world rendering.
     *
     * @param context the world render context.
     */
    public static void setupProjectionMatrix(WorldRenderContext context) {
        setupProjectionMatrix(context.gameRenderer(), context.camera(), context.tickDelta());
    }

    /**
     * Sets up the projection matrix for 3D rendering use in {@link WorldRenderEvents#END}
     * because it is not guaranteed to be intact from world rendering.
     *
     * @param gameRenderer the {@link GameRenderer}
     * @param camera       the game renderer's camera.
     * @param tickDelta    the world render tick delta.
     */
    public static void setupProjectionMatrix(GameRenderer gameRenderer, Camera camera, float tickDelta) {
        GameRendererAccessor accessor = (GameRendererAccessor) gameRenderer;
        MinecraftClient client = MinecraftClient.getInstance();
        MatrixStack matrixStack = new MatrixStack();
        double d = accessor.kmodlib_overlay_getFov(camera, tickDelta, true);
        matrixStack.multiplyPositionMatrix(gameRenderer.getBasicProjectionMatrix(d));
        accessor.kmodlib_overlay_tiltViewWhenHurt(matrixStack, tickDelta);
        if (client.options.getBobView().getValue()) {
            accessor.kmodlib_overlay_bobView(matrixStack, tickDelta);
        }

        float f = client.options.getDistortionEffectScale().getValue().floatValue();
        assert client.player != null;
        float g = MathHelper.lerp(tickDelta, client.player.prevNauseaIntensity, client.player.nauseaIntensity) * f * f;
        if (g > 0.0F) {
            int i = client.player.hasStatusEffect(StatusEffects.NAUSEA) ? 7 : 20;
            float h = 5.0F / (g * g + 5.0F) - g * 0.04F;
            h *= h;
            RotationAxis axis = RotationAxis.of(
                new Vector3f(0.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F));
            matrixStack.multiply(axis.rotationDegrees(((float) accessor.getTicks() + tickDelta) * (float) i));
            matrixStack.scale(1.0F / h, 1.0F, 1.0F);
            float j = -((float) accessor.getTicks() + tickDelta) * (float) i;
            matrixStack.multiply(axis.rotationDegrees(j));
        }

        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        gameRenderer.loadProjectionMatrix(matrix4f);
    }
}
