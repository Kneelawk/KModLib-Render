package com.kneelawk.kmodlib.client.blockmodel.ct;

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
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import com.kneelawk.kmodlib.client.blockmodel.BakedModelLayer;
import com.kneelawk.kmodlib.client.blockmodel.JsonMaterial;
import com.kneelawk.kmodlib.client.blockmodel.UnbakedModelLayer;
import com.kneelawk.kmodlib.client.blockmodel.connector.ModelConnector;
import com.kneelawk.kmodlib.client.blockmodel.sprite.BakedSpriteSupplier;
import com.kneelawk.kmodlib.client.blockmodel.sprite.UnbakedSpriteSupplier;
import com.kneelawk.kmodlib.client.blockmodel.sprite.UnbakedStaticSpriteSupplier;

/**
 * Unbaked connected-texture model layer.
 *
 * @param exteriorCorners exterior corner sprite supplier.
 * @param interiorCorners interior corner sprite supplier.
 * @param horizontalEdges horizontal edges sprite supplier.
 * @param verticalEdges   vertical edges sprite supplier.
 * @param noEdges         no edges sprite supplier.
 * @param material        material to render with.
 * @param depth           depth to render at.
 * @param cullFaces       whether to cull faces.
 * @param interiorBorder  whether to have borders inside a corner between three blocks in an L shape.
 * @param tintIndex       tint index to render with.
 * @param connector       the connector to control which blocks to connect to.
 */
public record UnbakedCTLayer(UnbakedSpriteSupplier exteriorCorners, UnbakedSpriteSupplier interiorCorners,
                             UnbakedSpriteSupplier horizontalEdges, UnbakedSpriteSupplier verticalEdges,
                             @Nullable UnbakedSpriteSupplier noEdges, JsonMaterial material, float depth,
                             boolean cullFaces, boolean interiorBorder, int tintIndex, ModelConnector connector)
    implements UnbakedModelLayer {

    /**
     * This model layer's codec.
     */
    public static final MapCodec<UnbakedCTLayer> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        UnbakedSpriteSupplier.CODEC.fieldOf("exterior_corners").forGetter(UnbakedCTLayer::exteriorCorners),
        UnbakedSpriteSupplier.CODEC.fieldOf("interior_corners").forGetter(UnbakedCTLayer::interiorCorners),
        UnbakedSpriteSupplier.CODEC.fieldOf("horizontal_edges").forGetter(UnbakedCTLayer::horizontalEdges),
        UnbakedSpriteSupplier.CODEC.fieldOf("vertical_edges").forGetter(UnbakedCTLayer::verticalEdges),
        UnbakedSpriteSupplier.CODEC.optionalFieldOf("no_edges").forGetter(layer -> Optional.ofNullable(layer.noEdges)),
        JsonMaterial.CODEC.optionalFieldOf("material", JsonMaterial.DEFAULT).forGetter(UnbakedCTLayer::material),
        Codec.FLOAT.optionalFieldOf("depth", 0.0f).forGetter(UnbakedCTLayer::depth),
        Codec.BOOL.optionalFieldOf("cull_faces", true).forGetter(UnbakedCTLayer::cullFaces),
        Codec.BOOL.optionalFieldOf("interior_border", true).forGetter(UnbakedCTLayer::interiorBorder),
        Codec.INT.optionalFieldOf("tint_index", -1).forGetter(UnbakedCTLayer::tintIndex),
        ModelConnector.CODEC.optionalFieldOf("connector", ModelConnector.DEFAULT).forGetter(UnbakedCTLayer::connector)
    ).apply(instance, UnbakedCTLayer::new));

    /**
     * Constructs an unbaked connected texture model layer.
     *
     * @param exteriorCorners exterior corner sprite supplier.
     * @param interiorCorners interior corner sprite supplier.
     * @param horizontalEdges horizontal edges sprite supplier.
     * @param verticalEdges   vertical edges sprite supplier.
     * @param noEdges         no edges sprite supplier.
     * @param material        material to render with.
     * @param depth           depth to render at.
     * @param cullFaces       whether to cull faces.
     * @param interiorBorder  whether to have borders inside a corner between three blocks in an L shape.
     * @param tintIndex       tint index to render with.
     * @param connector       the connector to control which blocks to connect to.
     */
    public UnbakedCTLayer(Identifier exteriorCorners, Identifier interiorCorners, Identifier horizontalEdges,
                          Identifier verticalEdges, @Nullable Identifier noEdges, JsonMaterial material, float depth,
                          boolean cullFaces, boolean interiorBorder, int tintIndex, ModelConnector connector) {
        this(new UnbakedStaticSpriteSupplier(exteriorCorners), new UnbakedStaticSpriteSupplier(interiorCorners),
            new UnbakedStaticSpriteSupplier(horizontalEdges), new UnbakedStaticSpriteSupplier(verticalEdges),
            noEdges == null ? null : new UnbakedStaticSpriteSupplier(noEdges), material, depth, cullFaces,
            interiorBorder, tintIndex, connector);
    }

    /**
     * Constructs an unbaked connected texture model layer.
     *
     * @param exteriorCorners exterior corner sprite supplier.
     * @param interiorCorners interior corner sprite supplier.
     * @param horizontalEdges horizontal edges sprite supplier.
     * @param verticalEdges   vertical edges sprite supplier.
     * @param noEdges         no edges sprite supplier.
     * @param material        material to render with.
     * @param depth           depth to render at.
     * @param cullFaces       whether to cull faces.
     * @param interiorBorder  whether to have borders inside a corner between three blocks in an L shape.
     * @param tintIndex       tint index to render with.
     * @param connector       the connector to control which blocks to connect to.
     */
    public UnbakedCTLayer(UnbakedSpriteSupplier exteriorCorners, UnbakedSpriteSupplier interiorCorners,
                          UnbakedSpriteSupplier horizontalEdges, UnbakedSpriteSupplier verticalEdges,
                          Optional<UnbakedSpriteSupplier> noEdges, JsonMaterial material, float depth,
                          boolean cullFaces, boolean interiorBorder, int tintIndex, ModelConnector connector) {
        this(exteriorCorners, interiorCorners, horizontalEdges, verticalEdges, noEdges.orElse(null), material, depth,
            cullFaces, interiorBorder, tintIndex, connector);
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
        Function<Identifier, Sprite> sprite =
            id -> textureGetter.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, id));

        BakedSpriteSupplier[] sprites;
        if (noEdges == null) {
            sprites = new BakedSpriteSupplier[]{
                exteriorCorners.bake(textureGetter, rotationContainer, modelId),
                horizontalEdges.bake(textureGetter, rotationContainer, modelId),
                verticalEdges.bake(textureGetter, rotationContainer, modelId),
                interiorCorners.bake(textureGetter, rotationContainer, modelId)
            };
        } else {
            sprites = new BakedSpriteSupplier[]{
                exteriorCorners.bake(textureGetter, rotationContainer, modelId),
                horizontalEdges.bake(textureGetter, rotationContainer, modelId),
                verticalEdges.bake(textureGetter, rotationContainer, modelId),
                interiorCorners.bake(textureGetter, rotationContainer, modelId),
                noEdges.bake(textureGetter, rotationContainer, modelId)
            };
        }

        return new BakedCTLayer(sprites, material.toRenderMaterial(), depth, cullFaces, interiorBorder, tintIndex,
            connector);
    }
}
