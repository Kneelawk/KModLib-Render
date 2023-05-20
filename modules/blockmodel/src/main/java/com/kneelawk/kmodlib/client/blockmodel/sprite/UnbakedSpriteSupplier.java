package com.kneelawk.kmodlib.client.blockmodel.sprite;

import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import com.kneelawk.kmodlib.client.blockmodel.KBlockModels;

/**
 * Unbaked sprite supplier for use in just about every kind of model that uses sprites.
 */
public interface UnbakedSpriteSupplier {
    /**
     * The unbaked sprite supplier codec.
     * <p>
     * Note that this is either an identifier or an object registry lookup. If this is an identifier, it is considered
     * to be the identifier of a static texture.
     */
    @SuppressWarnings("unchecked")
    Codec<UnbakedSpriteSupplier> CODEC = Codec.either(UnbakedStaticSpriteSupplier.CODEC,
            KBlockModels.SPRITE_SUPPLIER_REGISTRY.getCodec()
                .dispatch(UnbakedSpriteSupplier::getCodec, codec -> (Codec<UnbakedSpriteSupplier>) codec))
        .xmap(either -> either.map(Function.identity(), Function.identity()), supplier -> {
            if (supplier instanceof UnbakedStaticSpriteSupplier s) {
                return Either.left(s);
            } else {
                return Either.right(supplier);
            }
        });

    /**
     * @return the codec registered with {@link KBlockModels#SPRITE_SUPPLIER_REGISTRY}.
     */
    Codec<? extends UnbakedSpriteSupplier> getCodec();

    /**
     * @return whether this sprite supplier bakes to a static sprite.
     */
    boolean bakesToSprite();

    /**
     * Bakes this sprite supplier into a static sprite if possible.
     *
     * @param textureGetter     the supplied texture getter function.
     * @param rotationContainer the supplied rotation container.
     * @param modelId           the baking model's id.
     * @return this as a static sprite, or <code>null</code> if this cannot bake to a static sprite, an error occurred,
     * or this just bakes to <code>null</code>.
     */
    @Nullable Sprite bakeToSprite(Function<SpriteIdentifier, Sprite> textureGetter,
                                  @Nullable ModelBakeSettings rotationContainer, Identifier modelId);

    /**
     * Bakes this sprite supplier into a baked sprite supplier.
     *
     * @param textureGetter     the supplied texture getter function.
     * @param rotationContainer the supplied rotation container.
     * @param modelId           the baking model's id.
     * @return a baked sprite supplier, or <code>null</code> if an error occurred or this just bakes to
     * <code>null</code>.
     */
    @Nullable BakedSpriteSupplier bake(Function<SpriteIdentifier, Sprite> textureGetter,
                                       @Nullable ModelBakeSettings rotationContainer, Identifier modelId);
}
