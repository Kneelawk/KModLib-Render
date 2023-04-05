package com.kneelawk.kmodlib.render.blockmodel;

import java.util.Collection;
import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;

public interface UnbakedModelLayer {
    Codec<UnbakedModelLayer> CODEC =
        KBlockModels.BLOCK_MODEL_LAYER_REGISTRY.getCodec().dispatch(UnbakedModelLayer::getCodec, Function.identity());

    Codec<? extends UnbakedModelLayer> getCodec();

    Collection<Identifier> getModelDependencies();

    void setParents(Function<Identifier, UnbakedModel> modelLoader);
}
