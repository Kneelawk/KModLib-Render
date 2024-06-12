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

public record UnbakedBottomTopModelLayer(@Nullable JsonTexture side, @Nullable JsonTexture bottom,
                                         @Nullable JsonTexture top, JsonMaterial material, float depth,
                                         boolean cullFaces, boolean rotate, boolean quarterFaces)
    implements UnbakedModelLayer {
    public static final MapCodec<UnbakedBottomTopModelLayer> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        JsonTexture.CODEC.optionalFieldOf("side").forGetter(layer -> Optional.ofNullable(layer.side)),
        JsonTexture.CODEC.optionalFieldOf("bottom").forGetter(layer -> Optional.ofNullable(layer.bottom)),
        JsonTexture.CODEC.optionalFieldOf("top").forGetter(layer -> Optional.ofNullable(layer.top)),
        JsonMaterial.CODEC.optionalFieldOf("material", JsonMaterial.DEFAULT)
            .forGetter(UnbakedBottomTopModelLayer::material),
        Codec.FLOAT.optionalFieldOf("depth", 0.0f).forGetter(UnbakedBottomTopModelLayer::depth),
        Codec.BOOL.optionalFieldOf("cull_faces", true).forGetter(UnbakedBottomTopModelLayer::cullFaces),
        Codec.BOOL.optionalFieldOf("rotate", true).forGetter(UnbakedBottomTopModelLayer::rotate),
        Codec.BOOL.optionalFieldOf("quarter_faces", false).forGetter(UnbakedBottomTopModelLayer::quarterFaces)
    ).apply(instance, UnbakedBottomTopModelLayer::new));

    public UnbakedBottomTopModelLayer(@Nullable JsonTexture side, @Nullable JsonTexture bottom,
                                      @Nullable JsonTexture top, JsonMaterial material, float depth, boolean cullFaces,
                                      boolean rotate, boolean quarterFaces) {
        this.side = side;
        this.bottom = bottom;
        this.top = top;
        this.material = material;
        this.depth = depth;
        this.cullFaces = cullFaces;
        this.rotate = rotate;
        this.quarterFaces = quarterFaces;
    }

    public UnbakedBottomTopModelLayer(@Nullable Identifier side, @Nullable Identifier bottom, @Nullable Identifier top,
                                      JsonMaterial material, float depth, boolean cullFaces, boolean rotate,
                                      boolean quarterFaces) {
        this(side != null ? new JsonTexture(new UnbakedStaticSpriteSupplier(side), -1) : null,
            bottom != null ? new JsonTexture(new UnbakedStaticSpriteSupplier(bottom), -1) : null,
            top != null ? new JsonTexture(new UnbakedStaticSpriteSupplier(top), -1) : null, material, depth, cullFaces,
            rotate, quarterFaces);
    }

    public UnbakedBottomTopModelLayer(Optional<JsonTexture> side, Optional<JsonTexture> bottom,
                                      Optional<JsonTexture> top, JsonMaterial material, float depth,
                                      boolean cullFaces, boolean rotate, boolean quarterFaces) {
        this(side.orElse(null), bottom.orElse(null), top.orElse(null), material, depth, cullFaces, rotate,
            quarterFaces);
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
                                          ModelBakeSettings rotationContainer) {
        return CubeModelUtils.createBlock(rotationContainer, rotate, cullFaces, quarterFaces, depth, material, bottom,
            top, side, side, side, side, textureGetter);
    }
}
