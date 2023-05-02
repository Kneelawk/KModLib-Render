package com.kneelawk.kmodlib.render.blockmodel.cube;

import java.util.Objects;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import com.kneelawk.kmodlib.render.KMLRLog;
import com.kneelawk.kmodlib.render.blockmodel.BakedMeshModelLayer;
import com.kneelawk.kmodlib.render.blockmodel.BakedModelLayer;
import com.kneelawk.kmodlib.render.blockmodel.sprite.BakedSpriteSupplier;
import com.kneelawk.kmodlib.render.blockmodel.sprite.UnbakedSpriteSupplier;
import com.kneelawk.kmodlib.render.blockmodel.util.QuadPos;

public class CubeModelUtils {
    public static @Nullable BakedModelLayer createBlock(@Nullable ModelBakeSettings rotation, boolean cullFaces,
                                                        boolean quarterFaces, float sideDepth, float faceDepth,
                                                        RenderMaterial material, UnbakedSpriteSupplier[] unbakedSprites,
                                                        int[] tintIndices, Baker baker,
                                                        Function<SpriteIdentifier, Sprite> textureGetter,
                                                        Identifier modelId) {
        boolean bakesToSprite = true;
        for (UnbakedSpriteSupplier supplier : unbakedSprites) {
            if (!supplier.bakesToSprite()) {
                bakesToSprite = false;
                break;
            }
        }

        if (bakesToSprite) {
            Sprite[] sprites = new Sprite[6];
            for (int i = 0; i < 6; i++) {
                Sprite sprite = unbakedSprites[i].bakeToSprite(baker, textureGetter, rotation, modelId);
                if (sprite == null) {
                    KMLRLog.LOG.warn("Bake to sprite of {} returned null", unbakedSprites[i]);
                    return null;
                }
                sprites[i] = sprite;
            }

            MeshBuilder meshBuilder =
                Objects.requireNonNull(RendererAccess.INSTANCE.getRenderer(), "No Renderer access!").meshBuilder();
            emitCube(meshBuilder.getEmitter(), rotation, cullFaces, quarterFaces, sideDepth, faceDepth, material,
                sprites, tintIndices);

            return new BakedMeshModelLayer(meshBuilder.build());
        } else {
            BakedSpriteSupplier[] bakedSprites = new BakedSpriteSupplier[6];
            for (int i = 0; i < 6; i++) {
                BakedSpriteSupplier bakedSprite = unbakedSprites[i].bake(baker, textureGetter, rotation, modelId);
                if (bakedSprite == null) {
                    KMLRLog.LOG.warn("Bake of {} returned null", unbakedSprites[i]);
                    return null;
                }
                bakedSprites[i] = bakedSprite;
            }

            return new BakedSpriteCubeModelLayer(rotation, cullFaces, quarterFaces, sideDepth, faceDepth, material,
                bakedSprites, tintIndices);
        }
    }

    public static void emitCube(QuadEmitter emitter, @Nullable ModelBakeSettings rotation, boolean cullFaces,
                                boolean quarterFaces, float sideDepth, float faceDepth, RenderMaterial material,
                                Sprite[] sprites, int[] tintIndices) {
        for (Direction normal : Direction.values()) {
            Sprite sprite = sprites[normal.getId()];
            int tintIndex = tintIndices[normal.getId()];

            if (quarterFaces) {
                buildQuarteredFace(emitter, rotation, normal, cullFaces, sideDepth, faceDepth, material, sprite,
                    tintIndex);
            } else {
                buildFace(emitter, rotation, normal, cullFaces, sideDepth, faceDepth, material, sprite, tintIndex);
            }
        }
    }

    private static void buildQuarteredFace(QuadEmitter emitter, @Nullable ModelBakeSettings rotation, Direction normal,
                                           boolean cullFaces, float sideDepth, float faceDepth, RenderMaterial material,
                                           Sprite sprite, int tintIndex) {
        QuadPos[] corners = getCorners(sideDepth, faceDepth);
        for (QuadPos corner : corners) {
            corner.emit(emitter, normal, rotation);
            putQuadSettings(emitter, rotation, normal, cullFaces, material, sprite, tintIndex);
        }
    }

    private static QuadPos[] getCorners(float sideDepth, float faceDepth) {
        float depthClamped = MathHelper.clamp(sideDepth, 0.0f, 0.5f);
        float depthMaxed = Math.min(faceDepth, 0.5f);
        return new QuadPos[]{
            new QuadPos(0.0f + depthClamped, 0.0f + depthClamped, 0.5f, 0.5f, depthMaxed),
            new QuadPos(0.5f, 0.0f + depthClamped, 1.0f - depthClamped, 0.5f, depthMaxed),
            new QuadPos(0.0f + depthClamped, 0.5f, 0.5f, 1.0f - depthClamped, depthMaxed),
            new QuadPos(0.5f, 0.5f, 1.0f - depthClamped, 1.0f - depthClamped, depthMaxed)
        };
    }

    private static void buildFace(QuadEmitter emitter, @Nullable ModelBakeSettings rotation, Direction normal,
                                  boolean cullFaces, float sideDepth, float faceDepth, RenderMaterial material,
                                  Sprite sprite,
                                  int tintIndex) {
        new QuadPos(0.0f + sideDepth, 0.0f + sideDepth, 1.0f - sideDepth, 1.0f - sideDepth, faceDepth).emit(emitter,
            normal, rotation);
        putQuadSettings(emitter, rotation, normal, cullFaces, material, sprite, tintIndex);
    }

    private static void putQuadSettings(QuadEmitter emitter, @Nullable ModelBakeSettings rotation, Direction normal,
                                        boolean cullFaces, RenderMaterial material, Sprite sprite, int tintIndex) {
        emitter.spriteColor(0, -1, -1, -1, -1);
        emitter.material(material);

        emitter.colorIndex(tintIndex);

        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_NORMALIZED);

        emitter.cullFace(
            cullFaces ? (rotation != null ? Direction.transform(rotation.getRotation().getMatrix(), normal) : normal) :
                null);

        emitter.emit();
    }
}
