package com.kneelawk.kmodlib.render.blockmodel.util;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import com.kneelawk.kmodlib.render.blockmodel.quad.QuadRequest;

public record QuadPos(float left, float bottom, float right, float top, float depth) {
    public void emit(QuadEmitter emitter, Direction face, @Nullable ModelBakeSettings rotationContainer) {
        if (rotationContainer != null) {
            QuadEmitterUtils.square(emitter, rotationContainer, face, left, bottom, right, top, depth);
        } else {
            emitter.square(face, left, bottom, right, top, depth);
        }

        emitter.sprite(0, 0, left, 1f - top);
        emitter.sprite(1, 0, left, 1f - bottom);
        emitter.sprite(2, 0, right, 1f - bottom);
        emitter.sprite(3, 0, right, 1f - top);
    }

    public QuadRequest toQuadRequest(QuadEmitter emitter, BlockRenderView view, BlockState state, BlockPos pos,
                                     Supplier<Random> randomSupplier, RenderMaterial material, int tintIndex,
                                     Direction face, @Nullable ModelBakeSettings rotationContainer, int color) {
        return QuadEmitterUtils.quadRequest(emitter, view, state, pos, randomSupplier, material, tintIndex,
            rotationContainer, face, color, left, bottom, right, top, depth);
    }
}
