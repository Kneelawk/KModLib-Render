package com.kneelawk.kmodlib.render.blockmodel.sprite;

import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import com.kneelawk.kmodlib.render.blockmodel.KBlockModels;

public interface UnbakedSpriteSupplier {
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

    Codec<? extends UnbakedSpriteSupplier> getCodec();

    boolean bakesToSprite();

    @Nullable Sprite bakeToSprite(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter,
                                  @Nullable ModelBakeSettings rotationContainer, Identifier modelId);

    @Nullable BakedSpriteSupplier bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter,
                                       @Nullable ModelBakeSettings rotationContainer, Identifier modelId);
}
