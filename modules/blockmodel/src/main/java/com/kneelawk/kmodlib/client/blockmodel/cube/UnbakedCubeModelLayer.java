package com.kneelawk.kmodlib.client.blockmodel.cube;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
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

import com.kneelawk.kmodlib.client.blockmodel.BakedModelLayer;
import com.kneelawk.kmodlib.client.blockmodel.JsonMaterial;
import com.kneelawk.kmodlib.client.blockmodel.JsonTexture;
import com.kneelawk.kmodlib.client.blockmodel.UnbakedModelLayer;
import com.kneelawk.kmodlib.client.blockmodel.sprite.UnbakedStaticSpriteSupplier;
import com.kneelawk.kmodlib.client.blockmodel.util.CubeModelUtils;

public record UnbakedCubeModelLayer(@Nullable JsonTexture down, @Nullable JsonTexture up, @Nullable JsonTexture north,
                                    @Nullable JsonTexture south, @Nullable JsonTexture west, @Nullable JsonTexture east,
                                    JsonMaterial material, float depth, boolean cullFaces, boolean rotate,
                                    boolean quarterFaces) implements UnbakedModelLayer {
    public static final Codec<UnbakedCubeModelLayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        JsonTexture.CODEC.optionalFieldOf("down").forGetter(layer -> Optional.ofNullable(layer.down)),
        JsonTexture.CODEC.optionalFieldOf("up").forGetter(layer -> Optional.ofNullable(layer.up)),
        JsonTexture.CODEC.optionalFieldOf("north").forGetter(layer -> Optional.ofNullable(layer.north)),
        JsonTexture.CODEC.optionalFieldOf("south").forGetter(layer -> Optional.ofNullable(layer.south)),
        JsonTexture.CODEC.optionalFieldOf("west").forGetter(layer -> Optional.ofNullable(layer.west)),
        JsonTexture.CODEC.optionalFieldOf("east").forGetter(layer -> Optional.ofNullable(layer.east)),
        JsonMaterial.CODEC.optionalFieldOf("material", JsonMaterial.DEFAULT)
            .forGetter(UnbakedCubeModelLayer::material),
        Codec.FLOAT.optionalFieldOf("depth", 0.0f).forGetter(UnbakedCubeModelLayer::depth),
        Codec.BOOL.optionalFieldOf("cull_faces", true).forGetter(UnbakedCubeModelLayer::cullFaces),
        Codec.BOOL.optionalFieldOf("rotate", true).forGetter(UnbakedCubeModelLayer::rotate),
        Codec.BOOL.optionalFieldOf("quarter_faces", false).forGetter(UnbakedCubeModelLayer::quarterFaces)
    ).apply(instance, UnbakedCubeModelLayer::new));

    public UnbakedCubeModelLayer(@Nullable JsonTexture down, @Nullable JsonTexture up, @Nullable JsonTexture north,
                                 @Nullable JsonTexture south, @Nullable JsonTexture west, @Nullable JsonTexture east,
                                 JsonMaterial material, float depth, boolean cullFaces, boolean rotate,
                                 boolean quarterFaces) {
        this.down = down;
        this.up = up;
        this.north = north;
        this.south = south;
        this.west = west;
        this.east = east;
        this.material = material;
        this.depth = depth;
        this.cullFaces = cullFaces;
        this.rotate = rotate;
        this.quarterFaces = quarterFaces;
    }

    public UnbakedCubeModelLayer(@Nullable Identifier down, @Nullable Identifier up, @Nullable Identifier north,
                                 @Nullable Identifier south, @Nullable Identifier west, @Nullable Identifier east,
                                 JsonMaterial material, float depth, boolean cullFaces, boolean rotate,
                                 boolean quarterFaces) {
        this(down != null ? new JsonTexture(new UnbakedStaticSpriteSupplier(down), -1) : null,
            up != null ? new JsonTexture(new UnbakedStaticSpriteSupplier(up), -1) : null,
            north != null ? new JsonTexture(new UnbakedStaticSpriteSupplier(north), -1) : null,
            south != null ? new JsonTexture(new UnbakedStaticSpriteSupplier(south), -1) : null,
            west != null ? new JsonTexture(new UnbakedStaticSpriteSupplier(west), -1) : null,
            east != null ? new JsonTexture(new UnbakedStaticSpriteSupplier(east), -1) : null, material, depth,
            cullFaces, rotate, quarterFaces);
    }

    public UnbakedCubeModelLayer(Optional<JsonTexture> down, Optional<JsonTexture> up, Optional<JsonTexture> north,
                                 Optional<JsonTexture> south, Optional<JsonTexture> west, Optional<JsonTexture> east,
                                 JsonMaterial material, float depth, boolean cullFaces, boolean rotate,
                                 boolean quarterFaces) {
        this(down.orElse(null), up.orElse(null), north.orElse(null), south.orElse(null), west.orElse(null),
            east.orElse(null), material, depth, cullFaces, rotate, quarterFaces);
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
        return CubeModelUtils.createBlock(rotationContainer, rotate, cullFaces, quarterFaces, depth, material, down, up,
            north, south, west, east, textureGetter, modelId);
    }
}
