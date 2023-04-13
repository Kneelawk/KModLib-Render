package com.kneelawk.kmodlib.render.blockmodel.quad;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;

import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

public record StaticUnbakedQuadSupplier(Identifier sprite) implements UnbakedQuadSupplier {
    public static final Codec<StaticUnbakedQuadSupplier> CODEC =
        Identifier.CODEC.xmap(StaticUnbakedQuadSupplier::new, StaticUnbakedQuadSupplier::sprite);

    @Override
    public Codec<? extends UnbakedQuadSupplier> getCodec() {
        return CODEC;
    }

    @Override
    public boolean bakesToSprite() {
        return true;
    }

    @Override
    public @NotNull Sprite bakeToSprite(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter,
                                        ModelBakeSettings rotationContainer, Identifier modelId) {
        return textureGetter.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, sprite));
    }

    @Override
    public @NotNull BakedQuadSupplier bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter,
                                           ModelBakeSettings rotationContainer, Identifier modelId) {
        return new StaticBakedQuadSupplier(bakeToSprite(baker, textureGetter, rotationContainer, modelId));
    }
}
