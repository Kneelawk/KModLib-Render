package com.kneelawk.kmodlib.render.blockmodel;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.client.render.model.UnbakedModel;

public interface KUnbakedModel extends UnbakedModel {
    Codec<KUnbakedModel> CODEC =
        KBlockModels.BLOCK_MODEL_REGISTRY.getCodec().dispatch(KUnbakedModel::getCodec, Function.identity());

    Codec<? extends KUnbakedModel> getCodec();
}
