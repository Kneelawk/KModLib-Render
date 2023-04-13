package com.kneelawk.kmodlib.render.blockmodel.util;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import com.kneelawk.kmodlib.render.blockmodel.quad.QuadRequest;

import static java.lang.Math.abs;

public class QuadEmitterUtils {
    private static final float ORIGIN_X = 0.5f;
    private static final float ORIGIN_Y = 0.5f;
    private static final float ORIGIN_Z = 0.5f;

    public static QuadRequest quadRequest(QuadEmitter emitter, BlockRenderView view, BlockState state, BlockPos pos,
                                          Supplier<Random> randomSupplier, RenderMaterial material, int tintIndex,
                                          @Nullable ModelBakeSettings rotationContainer, Direction nominalFace,
                                          int color, float left, float bottom, float right, float top, float depth) {
        float newDepth;
        if (abs(depth) < QuadEmitter.CULL_FACE_EPSILON) {
            newDepth = 0f;
        } else {
            newDepth = depth;
        }

        Vector4f vec0;
        Vector4f vec1;
        Vector4f vec2;
        Vector4f vec3;

        switch (nominalFace) {
            case DOWN -> {
                vec0 = new Vector4f(left - ORIGIN_X, newDepth - ORIGIN_Y, top - ORIGIN_Z, 1f);
                vec1 = new Vector4f(left - ORIGIN_X, newDepth - ORIGIN_Y, bottom - ORIGIN_Z, 1f);
                vec2 = new Vector4f(right - ORIGIN_X, newDepth - ORIGIN_Y, bottom - ORIGIN_Z, 1f);
                vec3 = new Vector4f(right - ORIGIN_X, newDepth - ORIGIN_Y, top - ORIGIN_Z, 1f);
            }
            case UP -> {
                vec0 = new Vector4f(left - ORIGIN_X, 1f - newDepth - ORIGIN_Y, 1f - top - ORIGIN_Z, 1f);
                vec1 = new Vector4f(left - ORIGIN_X, 1f - newDepth - ORIGIN_Y, 1f - bottom - ORIGIN_Z, 1f);
                vec2 = new Vector4f(right - ORIGIN_X, 1f - newDepth - ORIGIN_Y, 1f - bottom - ORIGIN_Z, 1f);
                vec3 = new Vector4f(right - ORIGIN_X, 1f - newDepth - ORIGIN_Y, 1f - top - ORIGIN_Z, 1f);
            }
            case NORTH -> {
                vec0 = new Vector4f(1f - left - ORIGIN_X, top - ORIGIN_Y, newDepth - ORIGIN_Z, 1f);
                vec1 = new Vector4f(1f - left - ORIGIN_X, bottom - ORIGIN_Y, newDepth - ORIGIN_Z, 1f);
                vec2 = new Vector4f(1f - right - ORIGIN_X, bottom - ORIGIN_Y, newDepth - ORIGIN_Z, 1f);
                vec3 = new Vector4f(1f - right - ORIGIN_X, top - ORIGIN_Y, newDepth - ORIGIN_Z, 1f);
            }
            case SOUTH -> {
                vec0 = new Vector4f(left - ORIGIN_X, top - ORIGIN_Y, 1f - newDepth - ORIGIN_Z, 1f);
                vec1 = new Vector4f(left - ORIGIN_X, bottom - ORIGIN_Y, 1f - newDepth - ORIGIN_Z, 1f);
                vec2 = new Vector4f(right - ORIGIN_X, bottom - ORIGIN_Y, 1f - newDepth - ORIGIN_Z, 1f);
                vec3 = new Vector4f(right - ORIGIN_X, top - ORIGIN_Y, 1f - newDepth - ORIGIN_Z, 1f);
            }
            case WEST -> {
                vec0 = new Vector4f(newDepth - ORIGIN_X, top - ORIGIN_Y, left - ORIGIN_Z, 1f);
                vec1 = new Vector4f(newDepth - ORIGIN_X, bottom - ORIGIN_Y, left - ORIGIN_Z, 1f);
                vec2 = new Vector4f(newDepth - ORIGIN_X, bottom - ORIGIN_Y, right - ORIGIN_Z, 1f);
                vec3 = new Vector4f(newDepth - ORIGIN_X, top - ORIGIN_Y, right - ORIGIN_Z, 1f);
            }
            case EAST -> {
                vec0 = new Vector4f(1f - newDepth - ORIGIN_X, top - ORIGIN_Y, 1f - left - ORIGIN_Z, 1f);
                vec1 = new Vector4f(1f - newDepth - ORIGIN_X, bottom - ORIGIN_Y, 1f - left - ORIGIN_Z, 1f);
                vec2 = new Vector4f(1f - newDepth - ORIGIN_X, bottom - ORIGIN_Y, 1f - right - ORIGIN_Z, 1f);
                vec3 = new Vector4f(1f - newDepth - ORIGIN_X, top - ORIGIN_Y, 1f - right - ORIGIN_Z, 1f);
            }
            default -> throw new AssertionError(
                "Encountered non-direction direction " + nominalFace + "! Is someone mixing into enums???");
        }

        Direction transformedFace;

        if (rotationContainer != null) {
            Matrix4f matrix = rotationContainer.getRotation().getMatrix();

            transformedFace = Direction.transform(matrix, nominalFace);

            vec0.mul(matrix);
            vec1.mul(matrix);
            vec2.mul(matrix);
            vec3.mul(matrix);
        } else {
            transformedFace = nominalFace;
        }

        Direction cullFace;
        if (abs(depth) < QuadEmitter.CULL_FACE_EPSILON) {
            cullFace = transformedFace;
        } else {
            cullFace = null;
        }

        return new QuadRequest(emitter, view, state, pos, randomSupplier, material, tintIndex, transformedFace,
            cullFace, new float[]{vec0.x, vec1.x, vec2.x, vec3.x}, new float[]{vec0.y, vec1.y, vec2.y, vec3.y},
            new float[]{vec0.z, vec1.z, vec2.z, vec3.z}, new float[]{left, left, right, right},
            new float[]{1 - top, 1 - bottom, 1 - bottom, 1 - top}, new int[]{color, color, color, color});
    }

    public static void square(QuadEmitter emitter, ModelBakeSettings rotationContainer, Direction nominalFace,
                              float left, float bottom, float right, float top, float depth) {
        Matrix4f matrix = rotationContainer.getRotation().getMatrix();

        Direction transformedFace = Direction.transform(matrix, nominalFace);
        float newDepth;
        if (abs(depth) < QuadEmitter.CULL_FACE_EPSILON) {
            emitter.cullFace(transformedFace);
            newDepth = 0f;
        } else {
            emitter.cullFace(null);
            newDepth = depth;
        }

        emitter.nominalFace(transformedFace);

        Vector4f vec0;
        Vector4f vec1;
        Vector4f vec2;
        Vector4f vec3;

        switch (nominalFace) {
            case DOWN -> {
                vec0 = new Vector4f(left - ORIGIN_X, newDepth - ORIGIN_Y, top - ORIGIN_Z, 1f);
                vec1 = new Vector4f(left - ORIGIN_X, newDepth - ORIGIN_Y, bottom - ORIGIN_Z, 1f);
                vec2 = new Vector4f(right - ORIGIN_X, newDepth - ORIGIN_Y, bottom - ORIGIN_Z, 1f);
                vec3 = new Vector4f(right - ORIGIN_X, newDepth - ORIGIN_Y, top - ORIGIN_Z, 1f);
            }
            case UP -> {
                vec0 = new Vector4f(left - ORIGIN_X, 1f - newDepth - ORIGIN_Y, 1f - top - ORIGIN_Z, 1f);
                vec1 = new Vector4f(left - ORIGIN_X, 1f - newDepth - ORIGIN_Y, 1f - bottom - ORIGIN_Z, 1f);
                vec2 = new Vector4f(right - ORIGIN_X, 1f - newDepth - ORIGIN_Y, 1f - bottom - ORIGIN_Z, 1f);
                vec3 = new Vector4f(right - ORIGIN_X, 1f - newDepth - ORIGIN_Y, 1f - top - ORIGIN_Z, 1f);
            }
            case NORTH -> {
                vec0 = new Vector4f(1f - left - ORIGIN_X, top - ORIGIN_Y, newDepth - ORIGIN_Z, 1f);
                vec1 = new Vector4f(1f - left - ORIGIN_X, bottom - ORIGIN_Y, newDepth - ORIGIN_Z, 1f);
                vec2 = new Vector4f(1f - right - ORIGIN_X, bottom - ORIGIN_Y, newDepth - ORIGIN_Z, 1f);
                vec3 = new Vector4f(1f - right - ORIGIN_X, top - ORIGIN_Y, newDepth - ORIGIN_Z, 1f);
            }
            case SOUTH -> {
                vec0 = new Vector4f(left - ORIGIN_X, top - ORIGIN_Y, 1f - newDepth - ORIGIN_Z, 1f);
                vec1 = new Vector4f(left - ORIGIN_X, bottom - ORIGIN_Y, 1f - newDepth - ORIGIN_Z, 1f);
                vec2 = new Vector4f(right - ORIGIN_X, bottom - ORIGIN_Y, 1f - newDepth - ORIGIN_Z, 1f);
                vec3 = new Vector4f(right - ORIGIN_X, top - ORIGIN_Y, 1f - newDepth - ORIGIN_Z, 1f);
            }
            case WEST -> {
                vec0 = new Vector4f(newDepth - ORIGIN_X, top - ORIGIN_Y, left - ORIGIN_Z, 1f);
                vec1 = new Vector4f(newDepth - ORIGIN_X, bottom - ORIGIN_Y, left - ORIGIN_Z, 1f);
                vec2 = new Vector4f(newDepth - ORIGIN_X, bottom - ORIGIN_Y, right - ORIGIN_Z, 1f);
                vec3 = new Vector4f(newDepth - ORIGIN_X, top - ORIGIN_Y, right - ORIGIN_Z, 1f);
            }
            case EAST -> {
                vec0 = new Vector4f(1f - newDepth - ORIGIN_X, top - ORIGIN_Y, 1f - left - ORIGIN_Z, 1f);
                vec1 = new Vector4f(1f - newDepth - ORIGIN_X, bottom - ORIGIN_Y, 1f - left - ORIGIN_Z, 1f);
                vec2 = new Vector4f(1f - newDepth - ORIGIN_X, bottom - ORIGIN_Y, 1f - right - ORIGIN_Z, 1f);
                vec3 = new Vector4f(1f - newDepth - ORIGIN_X, top - ORIGIN_Y, 1f - right - ORIGIN_Z, 1f);
            }
            default -> throw new AssertionError("Encountered non-direction direction! Is someone mixing into enums???");
        }

        vec0.mul(matrix);
        vec1.mul(matrix);
        vec2.mul(matrix);
        vec3.mul(matrix);

        emitter.pos(0, vec0.x + ORIGIN_X, vec0.y + ORIGIN_Y, vec0.z + ORIGIN_Z);
        emitter.pos(1, vec1.x + ORIGIN_X, vec1.y + ORIGIN_Y, vec1.z + ORIGIN_Z);
        emitter.pos(2, vec2.x + ORIGIN_X, vec2.y + ORIGIN_Y, vec2.z + ORIGIN_Z);
        emitter.pos(3, vec3.x + ORIGIN_X, vec3.y + ORIGIN_Y, vec3.z + ORIGIN_Z);
    }
}
