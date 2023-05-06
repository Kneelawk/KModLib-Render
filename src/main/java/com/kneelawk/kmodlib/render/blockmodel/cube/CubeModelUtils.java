package com.kneelawk.kmodlib.render.blockmodel.cube;

import java.util.Objects;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
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
import com.kneelawk.kmodlib.render.blockmodel.JsonMaterial;
import com.kneelawk.kmodlib.render.blockmodel.JsonTexture;
import com.kneelawk.kmodlib.render.blockmodel.sprite.BakedSpriteSupplier;
import com.kneelawk.kmodlib.render.blockmodel.sprite.UnbakedSpriteSupplier;
import com.kneelawk.kmodlib.render.blockmodel.util.FacePos;

public class CubeModelUtils {
    public static @Nullable BakedModelLayer createBlock(ModelBakeSettings rotationContainer, boolean rotate,
                                                        boolean cullFaces, boolean quarterFaces, float depth,
                                                        @NotNull JsonMaterial material, @Nullable JsonTexture down,
                                                        @Nullable JsonTexture up, @Nullable JsonTexture north,
                                                        @Nullable JsonTexture south, @Nullable JsonTexture west,
                                                        @Nullable JsonTexture east, Baker baker,
                                                        Function<SpriteIdentifier, Sprite> textureGetter,
                                                        Identifier modelId) {
        float depthClamped = MathHelper.clamp(depth, 0.0f, 0.5f);
        float depthMaxed = Math.min(depth, 0.5f);
        return CubeModelUtils.createBlock(rotate ? rotationContainer : null, cullFaces, quarterFaces, depthClamped,
            depthMaxed, material.toRenderMaterial(), new UnbakedSpriteSupplier[]{
                down != null ? down.texture() : null, up != null ? up.texture() : null,
                north != null ? north.texture() : null, south != null ? south.texture() : null,
                west != null ? west.texture() : null, east != null ? east.texture() : null
            }, new int[]{
                down != null ? down.tintIndex() : -1, up != null ? up.tintIndex() : -1,
                north != null ? north.tintIndex() : -1, south != null ? south.tintIndex() : -1,
                west != null ? west.tintIndex() : -1, east != null ? east.tintIndex() : -1
            }, baker, textureGetter, modelId);
    }

    public static @Nullable BakedModelLayer createBlock(@Nullable ModelBakeSettings rotation, boolean cullFaces,
                                                        boolean quarterFaces, float sideDepth, float faceDepth,
                                                        RenderMaterial material,
                                                        @Nullable UnbakedSpriteSupplier[] unbakedSprites,
                                                        int[] tintIndices, Baker baker,
                                                        Function<SpriteIdentifier, Sprite> textureGetter,
                                                        Identifier modelId) {
        boolean bakesToSprite = true;
        for (UnbakedSpriteSupplier supplier : unbakedSprites) {
            if (supplier == null) continue;

            if (!supplier.bakesToSprite()) {
                bakesToSprite = false;
                break;
            }
        }

        if (bakesToSprite) {
            Sprite[] sprites = new Sprite[6];
            for (int i = 0; i < 6; i++) {
                UnbakedSpriteSupplier supplier = unbakedSprites[i];
                if (supplier == null) continue;

                Sprite sprite = supplier.bakeToSprite(baker, textureGetter, rotation, modelId);
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
                UnbakedSpriteSupplier supplier = unbakedSprites[i];
                if (supplier == null) continue;

                BakedSpriteSupplier bakedSprite = supplier.bake(baker, textureGetter, rotation, modelId);
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
                                @Nullable Sprite[] sprites, int[] tintIndices) {
        for (Direction normal : Direction.values()) {
            Sprite sprite = sprites[normal.getId()];
            if (sprite == null) continue;

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
        FacePos[] corners = getCorners(sideDepth, faceDepth);
        for (FacePos corner : corners) {
            corner.emit(emitter, normal, rotation);
            putQuadSettings(emitter, rotation, normal, cullFaces, material, sprite, tintIndex);
        }
    }

    private static FacePos[] getCorners(float sideDepth, float faceDepth) {
        float depthClamped = MathHelper.clamp(sideDepth, 0.0f, 0.5f);
        float depthMaxed = Math.min(faceDepth, 0.5f);
        return new FacePos[]{
            new FacePos(0.0f + depthClamped, 0.0f + depthClamped, 0.5f, 0.5f, depthMaxed),
            new FacePos(0.5f, 0.0f + depthClamped, 1.0f - depthClamped, 0.5f, depthMaxed),
            new FacePos(0.0f + depthClamped, 0.5f, 0.5f, 1.0f - depthClamped, depthMaxed),
            new FacePos(0.5f, 0.5f, 1.0f - depthClamped, 1.0f - depthClamped, depthMaxed)
        };
    }

    private static void buildFace(QuadEmitter emitter, @Nullable ModelBakeSettings rotation, Direction normal,
                                  boolean cullFaces, float sideDepth, float faceDepth, RenderMaterial material,
                                  Sprite sprite,
                                  int tintIndex) {
        new FacePos(0.0f + sideDepth, 0.0f + sideDepth, 1.0f - sideDepth, 1.0f - sideDepth, faceDepth).emit(emitter,
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
