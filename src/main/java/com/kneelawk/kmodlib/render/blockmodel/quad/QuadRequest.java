package com.kneelawk.kmodlib.render.blockmodel.quad;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

public record QuadRequest(QuadEmitter emitter, BlockRenderView blockView, BlockState state, BlockPos pos,
                          Supplier<Random> randomSupplier, RenderMaterial material, int tintIndex,
                          @Nullable Direction nominalFace, @Nullable Direction cullFace, float[] xs, float[] ys,
                          float[] zs, float[] us, float[] vs, int[] cs) {
    public void emitQuad(Sprite sprite) {
        for (int i = 0; i < 4; i++) {
            emitter.pos(i, xs[i], ys[i], zs[i]);
            emitter.sprite(i, 0, us[i], vs[i]);
            emitter.spriteColor(i, 0, cs[i]);
        }

        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_NORMALIZED);

        emitter.material(material);
        emitter.colorIndex(tintIndex);
        emitter.nominalFace(nominalFace);
        emitter.cullFace(cullFace);

        emitter.emit();
    }
}
