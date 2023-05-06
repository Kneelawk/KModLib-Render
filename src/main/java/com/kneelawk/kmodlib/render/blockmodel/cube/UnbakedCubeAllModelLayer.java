package com.kneelawk.kmodlib.render.blockmodel.cube;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import com.kneelawk.kmodlib.render.blockmodel.BakedModelLayer;
import com.kneelawk.kmodlib.render.blockmodel.JsonMaterial;
import com.kneelawk.kmodlib.render.blockmodel.JsonTexture;
import com.kneelawk.kmodlib.render.blockmodel.UnbakedModelLayer;
import com.kneelawk.kmodlib.render.blockmodel.sprite.UnbakedStaticSpriteSupplier;

public record UnbakedCubeAllModelLayer(JsonTexture all, JsonMaterial material, float depth, boolean cullFaces,
                                       boolean rotate, boolean quarterFaces) implements UnbakedModelLayer {
    public static final Codec<UnbakedCubeAllModelLayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        JsonTexture.CODEC.fieldOf("all").forGetter(UnbakedCubeAllModelLayer::all),
        JsonMaterial.CODEC.optionalFieldOf("material", JsonMaterial.DEFAULT)
            .forGetter(UnbakedCubeAllModelLayer::material),
        Codec.FLOAT.optionalFieldOf("depth", 0.0f).forGetter(UnbakedCubeAllModelLayer::depth),
        Codec.BOOL.optionalFieldOf("cull_faces", true).forGetter(UnbakedCubeAllModelLayer::cullFaces),
        Codec.BOOL.optionalFieldOf("rotate", true).forGetter(UnbakedCubeAllModelLayer::rotate),
        Codec.BOOL.optionalFieldOf("quarter_faces", false).forGetter(UnbakedCubeAllModelLayer::quarterFaces)
    ).apply(instance, UnbakedCubeAllModelLayer::new));

    public UnbakedCubeAllModelLayer(Identifier all, JsonMaterial material, float depth, boolean cullFaces,
                                    boolean rotate, boolean quarterFaces) {
        this(new JsonTexture(new UnbakedStaticSpriteSupplier(all), -1), material, depth, cullFaces, rotate,
            quarterFaces);
    }

    @Override
    public Codec<? extends UnbakedModelLayer> getCodec() {
        return CODEC;
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return List.of();
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> modelLoader) {
    }

    @Override
    public @Nullable BakedModelLayer bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter,
                                          ModelBakeSettings rotationContainer, Identifier modelId) {
        return CubeModelUtils.createBlock(rotationContainer, rotate, cullFaces, quarterFaces, depth, material, all, all,
            all, all, all, all, baker, textureGetter, modelId);
    }
}
