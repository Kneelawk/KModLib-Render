package com.kneelawk.kmodlib.render.blockmodel.quad;

import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;

import com.mojang.serialization.Codec;

import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

/**
 * Represents a raw, unbaked quad supplier, decoded from json.
 */
public interface UnbakedQuadSupplier {
    /**
     * @return this unbaked quad supplier's codec, for json-encoding and model-data-generation purposes.
     */
    Codec<? extends UnbakedQuadSupplier> getCodec();

    /**
     * @return whether this quad supplier can be baked into a static sprite.
     */
    boolean bakesToSprite();

    /**
     * Bakes this quad supplier to a static sprite for use in baked {@link Mesh}s.
     * <p>
     * This should return {@code null} either if there was an error in this quad supplier's configuration, or if this
     * quad supplier cannot be baked to a static sprite.
     *
     * @param baker             the {@link Baker} supplied during the baking process.
     * @param textureGetter     the texture getter supplied during the baking process.
     * @param rotationContainer the rotation container supplied during the baking process.
     * @param modelId           the model id of the model being baked.
     * @return this quad supplier as a {@link Sprite} or {@code null} either if this quad supplier's configuration is
     * invalid, or if this quad supplier cannot be baked to a static sprite.
     */
    @Nullable Sprite bakeToSprite(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter,
                                  ModelBakeSettings rotationContainer, Identifier modelId);

    /**
     * Bakes this quad supplier to a {@link BakedQuadSupplier}.
     * <p>
     * This should only return {@code null} if there was an error in this quad supplier's configuration. If this quad
     * supplier would normally bake to a sprite, a valid {@link BakedQuadSupplier} should still be returned. See
     * {@link StaticBakedQuadSupplier} for these kinds of situations.
     *
     * @param baker             the {@link Baker} supplied during the baking process.
     * @param textureGetter     the texture getter supplied during the baking process.
     * @param rotationContainer the rotation container supplied during the baking process.
     * @param modelId           the model id of the model being baked.
     * @return a {@link BakedQuadSupplier} or {@code null} if this quad supplier's configuration is invalid.
     */
    @Nullable BakedQuadSupplier bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter,
                                     ModelBakeSettings rotationContainer, Identifier modelId);
}
