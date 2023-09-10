package com.kneelawk.kmodlib.client.overlay.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
    @Invoker("getFov")
    double kmodlib_overlay_getFov(Camera camera, float tickDelta, boolean changingFov);

    @Invoker("tiltViewWhenHurt")
    void kmodlib_overlay_tiltViewWhenHurt(MatrixStack matrices, float tickDelta);

    @Invoker("bobView")
    void kmodlib_overlay_bobView(MatrixStack matrices, float tickDelta);

    @Accessor
    int getTicks();
}
