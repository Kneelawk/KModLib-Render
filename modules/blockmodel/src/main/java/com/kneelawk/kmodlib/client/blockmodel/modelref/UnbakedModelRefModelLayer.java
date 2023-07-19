package com.kneelawk.kmodlib.client.blockmodel.modelref;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import com.kneelawk.kmodlib.client.blockmodel.BakedMeshModelLayer;
import com.kneelawk.kmodlib.client.blockmodel.BakedModelLayer;
import com.kneelawk.kmodlib.client.blockmodel.JsonMaterial;
import com.kneelawk.kmodlib.client.blockmodel.UnbakedModelLayer;
import com.kneelawk.kmodlib.client.blockmodel.util.RenderUtils;

public record UnbakedModelRefModelLayer(Identifier ref, JsonMaterial material, boolean rotate)
    implements UnbakedModelLayer {
    public static final Codec<UnbakedModelRefModelLayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Identifier.CODEC.fieldOf("ref").forGetter(UnbakedModelRefModelLayer::ref),
        JsonMaterial.CODEC.optionalFieldOf("material", JsonMaterial.DEFAULT)
            .forGetter(UnbakedModelRefModelLayer::material),
        Codec.BOOL.optionalFieldOf("rotate", true).forGetter(UnbakedModelRefModelLayer::rotate)
    ).apply(instance, UnbakedModelRefModelLayer::new));

    @Override
    public Codec<? extends UnbakedModelLayer> getCodec() {
        return CODEC;
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return List.of(ref);
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> modelLoader) {
    }

    @Override
    public @NotNull BakedModelLayer bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter,
                                         ModelBakeSettings rotationContainer, Identifier modelId) {
        ModelBakeSettings settings;
        if (rotate) {
            settings = rotationContainer;
        } else {
            settings = ModelRotation.X0_Y0;
        }

        BakedModel model = baker.bake(ref, settings);

        MeshBuilder builder =
            Objects.requireNonNull(RendererAccess.INSTANCE.getRenderer(), "No Renderer access!").meshBuilder();
        QuadEmitter emitter = builder.getEmitter();

        RenderUtils.fromVanilla(model, emitter, material.toRenderMaterial());

        return new BakedMeshModelLayer(builder.build());
    }
}
