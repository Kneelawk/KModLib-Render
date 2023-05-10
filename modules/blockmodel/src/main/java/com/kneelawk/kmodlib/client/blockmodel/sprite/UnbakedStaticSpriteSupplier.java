package com.kneelawk.kmodlib.client.blockmodel.sprite;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;

import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

public record UnbakedStaticSpriteSupplier(Identifier sprite) implements UnbakedSpriteSupplier {
    public static final Codec<UnbakedStaticSpriteSupplier> CODEC =
        Identifier.CODEC.xmap(UnbakedStaticSpriteSupplier::new, UnbakedStaticSpriteSupplier::sprite);

    @Override
    public Codec<? extends UnbakedSpriteSupplier> getCodec() {
        return CODEC;
    }

    @Override
    public boolean bakesToSprite() {
        return true;
    }

    @Override
    public @NotNull Sprite bakeToSprite(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter,
                                        @Nullable ModelBakeSettings rotationContainer, Identifier modelId) {
        return textureGetter.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, sprite));
    }

    @Override
    public @NotNull BakedSpriteSupplier bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter,
                                             @Nullable ModelBakeSettings rotationContainer, Identifier modelId) {
        return new BakedStaticSpriteSupplier(bakeToSprite(baker, textureGetter, rotationContainer, modelId));
    }
}
