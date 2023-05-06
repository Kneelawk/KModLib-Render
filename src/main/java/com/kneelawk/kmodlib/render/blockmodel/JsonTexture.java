package com.kneelawk.kmodlib.render.blockmodel;

import java.util.Optional;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.kneelawk.kmodlib.render.blockmodel.sprite.UnbakedSpriteSupplier;

public record JsonTexture(@Nullable UnbakedSpriteSupplier texture, int tintIndex) {
    private static final int DEFAULT_TINT_INDEX = -1;
    private static final Codec<JsonTexture> RECORD_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        UnbakedSpriteSupplier.CODEC.optionalFieldOf("texture").forGetter(tex -> Optional.ofNullable(tex.texture)),
        Codec.INT.optionalFieldOf("tintindex", DEFAULT_TINT_INDEX).forGetter(JsonTexture::tintIndex)
    ).apply(instance, JsonTexture::new));

    private JsonTexture(Optional<UnbakedSpriteSupplier> texture, int tintIndex) {
        this(texture.orElse(null), tintIndex);
    }

    public static final Codec<JsonTexture> CODEC = Codec.either(UnbakedSpriteSupplier.CODEC, RECORD_CODEC)
        .xmap(either -> either.map(id -> new JsonTexture(id, DEFAULT_TINT_INDEX), Function.identity()),
            tex -> tex.tintIndex == DEFAULT_TINT_INDEX ? Either.left(tex.texture) : Either.right(tex));
}
