package com.kneelawk.kmodlib.render.blockmodel.util;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.util.math.Direction;

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
}
