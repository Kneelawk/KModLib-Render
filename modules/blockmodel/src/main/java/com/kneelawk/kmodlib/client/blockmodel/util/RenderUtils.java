package com.kneelawk.kmodlib.client.blockmodel.util;

import java.util.List;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

/**
 * Utility functions for rendering.
 */
public class RenderUtils {
    /**
     * Copies a vanilla baked model to a FRAPI quad emitter.
     *
     * @param from     the vanilla baked model to copy from.
     * @param to       the FRAPI quad emitter to copy to.
     * @param material the material to use when writing to the quad emitter.
     */
    public static void fromVanilla(BakedModel from, QuadEmitter to, RenderMaterial material) {
        Random random = Random.create(42);

        for (Direction dir : Direction.values()) {
            List<BakedQuad> quads = from.getQuads(null, dir, random);
            for (BakedQuad quad : quads) {
                to.fromVanilla(quad, material, dir);
                to.emit();
            }
        }

        List<BakedQuad> quads = from.getQuads(null, null, random);
        for (BakedQuad quad : quads) {
            to.fromVanilla(quad, material, null);
            to.emit();
        }
    }
}
