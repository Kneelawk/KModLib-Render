package com.kneelawk.kmodlib.render.blockmodel.quad;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.texture.Sprite;

/**
 * Draws the requested quad using the contained sprite.
 *
 * @param sprite the sprite to draw onto any requested quads.
 */
public record StaticBakedQuadSupplier(Sprite sprite) implements BakedQuadSupplier {
    @Override
    public void draw(@NotNull QuadRequest request) {
        request.emitQuad(sprite);
    }
}
