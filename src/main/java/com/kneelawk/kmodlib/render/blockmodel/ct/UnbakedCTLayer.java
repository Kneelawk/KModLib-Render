package com.kneelawk.kmodlib.render.blockmodel.ct;

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
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import com.kneelawk.kmodlib.render.blockmodel.BakedModelLayer;
import com.kneelawk.kmodlib.render.blockmodel.JsonMaterial;
import com.kneelawk.kmodlib.render.blockmodel.UnbakedModelLayer;
import com.kneelawk.kmodlib.render.blockmodel.connector.ModelConnector;

public class UnbakedCTLayer implements UnbakedModelLayer {
    public static final Codec<UnbakedCTLayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Identifier.CODEC.fieldOf("exterior_corners").forGetter(layer -> layer.exteriorCorners),
        Identifier.CODEC.fieldOf("interior_corners").forGetter(layer -> layer.interiorCorners),
        Identifier.CODEC.fieldOf("horizontal_edges").forGetter(layer -> layer.horizontalEdges),
        Identifier.CODEC.fieldOf("vertical_edges").forGetter(layer -> layer.verticalEdges),
        Identifier.CODEC.optionalFieldOf("no_edges").forGetter(layer -> Optional.ofNullable(layer.noEdges)),
        JsonMaterial.CODEC.optionalFieldOf("material", JsonMaterial.DEFAULT).forGetter(layer -> layer.material),
        Codec.FLOAT.optionalFieldOf("depth", 0.0f).forGetter(layer -> layer.depth),
        Codec.BOOL.optionalFieldOf("cull_faces", true).forGetter(layer -> layer.cullFaces),
        Codec.BOOL.optionalFieldOf("interior_border", true).forGetter(layer -> layer.interiorBorder),
        Codec.INT.optionalFieldOf("tint_index", -1).forGetter(layer -> layer.tintIndex),
        ModelConnector.CODEC.optionalFieldOf("connector", ModelConnector.DEFAULT).forGetter(layer -> layer.connector)
    ).apply(instance, UnbakedCTLayer::new));

    private final Identifier exteriorCorners;
    private final Identifier interiorCorners;
    private final Identifier horizontalEdges;
    private final Identifier verticalEdges;
    private final @Nullable Identifier noEdges;
    private final JsonMaterial material;
    private final float depth;
    private final boolean cullFaces;
    private final boolean interiorBorder;
    private final int tintIndex;
    private final ModelConnector connector;

    public UnbakedCTLayer(Identifier exteriorCorners, Identifier interiorCorners, Identifier horizontalEdges,
                          Identifier verticalEdges, @Nullable Identifier noEdges, JsonMaterial material, float depth,
                          boolean cullFaces, boolean interiorBorder, int tintIndex, ModelConnector connector) {
        this.exteriorCorners = exteriorCorners;
        this.interiorCorners = interiorCorners;
        this.horizontalEdges = horizontalEdges;
        this.verticalEdges = verticalEdges;
        this.noEdges = noEdges;
        this.material = material;
        this.depth = depth;
        this.cullFaces = cullFaces;
        this.interiorBorder = interiorBorder;
        this.tintIndex = tintIndex;
        this.connector = connector;
    }

    private UnbakedCTLayer(Identifier exteriorCorners, Identifier interiorCorners, Identifier horizontalEdges,
                           Identifier verticalEdges, Optional<Identifier> noEdges, JsonMaterial material, float depth,
                           boolean cullFaces, boolean interiorBorder, int tintIndex, ModelConnector connector) {
        this(exteriorCorners, interiorCorners, horizontalEdges, verticalEdges, noEdges.orElse(null), material, depth, cullFaces, interiorBorder, tintIndex, connector);
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
        Function<Identifier, Sprite> sprite = id -> textureGetter.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, id));

        Sprite[] sprites;
        if (noEdges == null) {
            sprites = new Sprite[] {
                sprite.apply(exteriorCorners), sprite.apply(horizontalEdges), sprite.apply(verticalEdges), sprite.apply(interiorCorners)
            };
        } else {
            sprites = new Sprite[] {
                sprite.apply(exteriorCorners), sprite.apply(horizontalEdges), sprite.apply(verticalEdges), sprite.apply(interiorCorners), sprite.apply(noEdges)
            };
        }

        return new BakedCTLayer(sprites, material.toRenderMaterial(), depth, cullFaces, interiorBorder, tintIndex, connector);
    }
}
