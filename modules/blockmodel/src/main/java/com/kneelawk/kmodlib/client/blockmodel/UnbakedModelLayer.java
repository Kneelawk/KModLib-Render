package com.kneelawk.kmodlib.client.blockmodel;

import java.util.Collection;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

/**
 * An unbaked model layer.
 */
public interface UnbakedModelLayer {
    /**
     * The codec for encoding and decoding all unbaked model layers via {@link KBlockModels#BLOCK_MODEL_LAYER_REGISTRY}.
     */
    Codec<UnbakedModelLayer> CODEC =
        KBlockModels.BLOCK_MODEL_LAYER_REGISTRY.getCodec().dispatch(UnbakedModelLayer::getCodec, Function.identity());

    /**
     * @return the codec registered with {@link KBlockModels#BLOCK_MODEL_LAYER_REGISTRY}.
     */
    MapCodec<? extends UnbakedModelLayer> getCodec();

    /**
     * @return the identifiers of the models this model layer depends on.
     */
    Collection<Identifier> getModelDependencies();

    /**
     * Used for collecting all the unbaked models this model depends on.
     *
     * @param modelLoader a function for retrieving unbaked models by their id.
     */
    void setParents(Function<Identifier, UnbakedModel> modelLoader);

    /**
     * Bakes this unbaked model layer to a {@link BakedModelLayer}.
     *
     * @param baker             the baker supplied to this model.
     * @param textureGetter     the texture getter for this model.
     * @param rotationContainer the rotation container for this model.
     * @param modelId           the model id of this model.
     * @return a baked model layer, or <code>null</code> if an error occurred.
     */
    @Nullable BakedModelLayer bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter,
                                   ModelBakeSettings rotationContainer, Identifier modelId);
}
