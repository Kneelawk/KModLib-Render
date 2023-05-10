package com.kneelawk.kmodlib.client.blockmodel.util;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.util.math.Direction;

/**
 * Describes a quad on a direction-aligned face of a block.
 *
 * @param left   how far the left edge of this face is from the left side of block on this side.
 * @param bottom how far the bottom edge of this face is from the bottom side of the block on this side.
 * @param right  how far the right edge of this face is from the right side of the block on this side.
 * @param top    how far the top edge of this face is from the top side of the block on this side.
 * @param depth  how far into this block this face is set.
 */
public record FacePos(float left, float bottom, float right, float top, float depth) {
    /**
     * Emits this face to an emitter.
     *
     * @param emitter           the emitter to emit to.
     * @param face              the side of the block this is being emitted on.
     * @param rotationContainer any rotations this block may have.
     */
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
}
