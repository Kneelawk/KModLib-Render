package com.kneelawk.kmodlib.client.blockmodel.cube;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import com.kneelawk.kmodlib.client.blockmodel.BakedModelLayer;
import com.kneelawk.kmodlib.client.blockmodel.JsonMaterial;
import com.kneelawk.kmodlib.client.blockmodel.JsonTexture;
import com.kneelawk.kmodlib.client.blockmodel.UnbakedModelLayer;
import com.kneelawk.kmodlib.client.blockmodel.sprite.UnbakedStaticSpriteSupplier;
import com.kneelawk.kmodlib.client.blockmodel.util.CubeModelUtils;

public record UnbakedColumnModelLayer(@Nullable JsonTexture side, @Nullable JsonTexture end, JsonMaterial material,
                                      float depth, boolean cullFaces, boolean rotate, boolean quarterFaces)
    implements UnbakedModelLayer {
    public static final MapCodec<UnbakedColumnModelLayer> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        JsonTexture.CODEC.optionalFieldOf("side").forGetter(layer -> Optional.ofNullable(layer.side)),
        JsonTexture.CODEC.optionalFieldOf("end").forGetter(layer -> Optional.ofNullable(layer.end)),
        JsonMaterial.CODEC.optionalFieldOf("material", JsonMaterial.DEFAULT)
            .forGetter(UnbakedColumnModelLayer::material),
        Codec.FLOAT.optionalFieldOf("depth", 0.0f).forGetter(UnbakedColumnModelLayer::depth),
        Codec.BOOL.optionalFieldOf("cull_faces", true).forGetter(UnbakedColumnModelLayer::cullFaces),
        Codec.BOOL.optionalFieldOf("rotate", true).forGetter(UnbakedColumnModelLayer::rotate),
        Codec.BOOL.optionalFieldOf("quarter_faces", false).forGetter(UnbakedColumnModelLayer::quarterFaces)
    ).apply(instance, UnbakedColumnModelLayer::new));

    public UnbakedColumnModelLayer(@Nullable JsonTexture side, @Nullable JsonTexture end, JsonMaterial material,
                                   float depth, boolean cullFaces, boolean rotate, boolean quarterFaces) {
        this.side = side;
        this.end = end;
        this.material = material;
        this.depth = depth;
        this.cullFaces = cullFaces;
        this.rotate = rotate;
        this.quarterFaces = quarterFaces;
    }

    public UnbakedColumnModelLayer(@Nullable Identifier side, @Nullable Identifier end, JsonMaterial material,
                                   float depth, boolean cullFaces, boolean rotate, boolean quarterFaces) {
        this(side != null ? new JsonTexture(new UnbakedStaticSpriteSupplier(side), -1) : null,
            end != null ? new JsonTexture(new UnbakedStaticSpriteSupplier(end), -1) : null, material, depth, cullFaces,
            rotate, quarterFaces);
    }

    public UnbakedColumnModelLayer(Optional<JsonTexture> side, Optional<JsonTexture> end, JsonMaterial material,
                                   float depth, boolean cullFaces, boolean rotate, boolean quarterFaces) {
        this(side.orElse(null), end.orElse(null), material, depth, cullFaces, rotate, quarterFaces);
    }

    @Override
    public MapCodec<? extends UnbakedModelLayer> getCodec() {
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
        return CubeModelUtils.createBlock(rotationContainer, rotate, cullFaces, quarterFaces, depth, material, end,
            end, side, side, side, side, textureGetter, modelId);
    }
}
