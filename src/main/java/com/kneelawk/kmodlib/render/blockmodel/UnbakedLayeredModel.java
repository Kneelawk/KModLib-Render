package com.kneelawk.kmodlib.render.blockmodel;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

public class UnbakedLayeredModel implements KUnbakedModel {
    private static final Identifier DEFAULT_TRANSFORMATION = new Identifier("block/block");
    public static final Codec<UnbakedLayeredModel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Identifier.CODEC.optionalFieldOf("transformation", DEFAULT_TRANSFORMATION)
            .forGetter(model -> model.transformation),
        Identifier.CODEC.fieldOf("particle").forGetter(model -> model.particle),
        Codec.list(UnbakedModelLayer.CODEC).fieldOf("layers").forGetter(model -> model.layers)
    ).apply(instance, UnbakedLayeredModel::new));

    private final Identifier transformation;
    private final Identifier particle;
    private final List<UnbakedModelLayer> layers;

    public UnbakedLayeredModel(Identifier transformation, Identifier particle, List<UnbakedModelLayer> layers) {
        this.transformation = transformation;
        this.particle = particle;
        this.layers = layers;
    }

    @Override
    public Codec<? extends KUnbakedModel> getCodec() {
        return CODEC;
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return Stream.concat(layers.stream().flatMap(layer -> layer.getModelDependencies().stream()),
            Stream.of(transformation)).distinct().toList();
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> modelLoader) {
        layers.forEach(layer -> layer.setParents(modelLoader));
    }

    @Nullable
    @Override
    public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter,
                           ModelBakeSettings rotationContainer, Identifier modelId) {
        return null;
    }
}
