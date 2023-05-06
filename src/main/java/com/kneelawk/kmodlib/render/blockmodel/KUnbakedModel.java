package com.kneelawk.kmodlib.render.blockmodel;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.client.render.model.UnbakedModel;

/**
 * A model that can be loaded from files with the <code>.kr.json</code> extension, based on type key.
 */
public interface KUnbakedModel extends UnbakedModel {
    /**
     * Codec for loading all {@link KUnbakedModel}s via {@link KBlockModels#BLOCK_MODEL_REGISTRY}.
     */
    Codec<KUnbakedModel> CODEC =
        KBlockModels.BLOCK_MODEL_REGISTRY.getCodec().dispatch(KUnbakedModel::getCodec, Function.identity());

    /**
     * @return the codec registered with {@link KBlockModels#BLOCK_MODEL_REGISTRY}.
     */
    Codec<? extends KUnbakedModel> getCodec();
}
