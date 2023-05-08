package com.kneelawk.kmodlib.render.blockmodel;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.JsonObject;

import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import com.kneelawk.kmodlib.render.KMLRLog;
import com.kneelawk.kmodlib.render.blockmodel.connector.BlockModelConnector;
import com.kneelawk.kmodlib.render.blockmodel.connector.ModelConnector;
import com.kneelawk.kmodlib.render.blockmodel.connector.RenderTagModelConnector;
import com.kneelawk.kmodlib.render.blockmodel.ct.UnbakedCTLayer;
import com.kneelawk.kmodlib.render.blockmodel.cube.UnbakedBottomTopModelLayer;
import com.kneelawk.kmodlib.render.blockmodel.cube.UnbakedColumnModelLayer;
import com.kneelawk.kmodlib.render.blockmodel.cube.UnbakedCubeAllModelLayer;
import com.kneelawk.kmodlib.render.blockmodel.cube.UnbakedCubeModelLayer;
import com.kneelawk.kmodlib.render.blockmodel.sprite.UnbakedSpriteSupplier;

import static com.kneelawk.kmodlib.render.Constants.id;

/**
 * Class containing registries for block models.
 */
public class KBlockModels {
    /**
     * The filename extension of KModLib-Render block models.
     */
    public static final String MODEL_EXTENSION = ".kr.json";

    private static final Identifier BLOCK_MODEL_REGISTRY_ID = id("block_model");
    private static final Identifier BLOCK_MODEL_LAYER_REGISTRY_ID = id("block_model_layer");
    private static final Identifier BLOCK_MODEL_CONNECTOR_REGISTRY_ID = id("block_model_connector");
    private static final Identifier SPRITE_SUPPLIER_REGISTRY_ID = id("sprite_supplier");
    private static final RegistryKey<Registry<Codec<? extends KUnbakedModel>>> BLOCK_MODEL_REGISTRY_KEY =
        RegistryKey.ofRegistry(BLOCK_MODEL_REGISTRY_ID);
    private static final RegistryKey<Registry<Codec<? extends UnbakedModelLayer>>> BLOCK_MODEL_LAYER_REGISTRY_KEY =
        RegistryKey.ofRegistry(BLOCK_MODEL_LAYER_REGISTRY_ID);
    private static final RegistryKey<Registry<ModelConnector.Type>> BLOCK_MODEL_CONNECTOR_REGISTRY_KEY =
        RegistryKey.ofRegistry(BLOCK_MODEL_CONNECTOR_REGISTRY_ID);
    private static final RegistryKey<Registry<Codec<? extends UnbakedSpriteSupplier>>> SPRITE_SUPPLIER_REGISTRY_KEY =
        RegistryKey.ofRegistry(SPRITE_SUPPLIER_REGISTRY_ID);

    /**
     * Registry for registering codecs of {@link KUnbakedModel}s in.
     */
    public static final Registry<Codec<? extends KUnbakedModel>> BLOCK_MODEL_REGISTRY =
        new SimpleRegistry<>(BLOCK_MODEL_REGISTRY_KEY, Lifecycle.stable());

    /**
     * Registry for registering codecs of {@link UnbakedModelLayer}s in.
     */
    public static final Registry<Codec<? extends UnbakedModelLayer>> BLOCK_MODEL_LAYER_REGISTRY =
        new SimpleRegistry<>(BLOCK_MODEL_LAYER_REGISTRY_KEY, Lifecycle.stable());

    /**
     * Registry for registering types of {@link ModelConnector}s in.
     */
    public static final Registry<ModelConnector.Type> BLOCK_MODEL_CONNECTOR_REGISTRY =
        new SimpleRegistry<>(BLOCK_MODEL_CONNECTOR_REGISTRY_KEY, Lifecycle.stable());

    /**
     * Registry for registering codecs of {@link UnbakedSpriteSupplier}s in.
     */
    public static final Registry<Codec<? extends UnbakedSpriteSupplier>> SPRITE_SUPPLIER_REGISTRY =
        new SimpleRegistry<>(SPRITE_SUPPLIER_REGISTRY_KEY, Lifecycle.stable());

    private static final AtomicBoolean GAVE_FORMAT_WARNING = new AtomicBoolean(false);

    @SuppressWarnings("unchecked")
    @ApiStatus.Internal
    public static void init() {
        Registry.register((Registry<? super Registry<Codec<? extends KUnbakedModel>>>) Registries.REGISTRIES,
            BLOCK_MODEL_REGISTRY_ID, BLOCK_MODEL_REGISTRY);
        Registry.register((Registry<? super Registry<Codec<? extends UnbakedModelLayer>>>) Registries.REGISTRIES,
            BLOCK_MODEL_LAYER_REGISTRY_ID, BLOCK_MODEL_LAYER_REGISTRY);

        Registry.register(BLOCK_MODEL_REGISTRY, id("layered"), UnbakedLayeredModel.CODEC);

        Registry.register(BLOCK_MODEL_LAYER_REGISTRY, id("quarter_connected_texture"), UnbakedCTLayer.CODEC);
        Registry.register(BLOCK_MODEL_LAYER_REGISTRY, id("cube_all"), UnbakedCubeAllModelLayer.CODEC);
        Registry.register(BLOCK_MODEL_LAYER_REGISTRY, id("cube_bottom_top"), UnbakedBottomTopModelLayer.CODEC);
        Registry.register(BLOCK_MODEL_LAYER_REGISTRY, id("cube_column"), UnbakedColumnModelLayer.CODEC);
        Registry.register(BLOCK_MODEL_LAYER_REGISTRY, id("cube"), UnbakedCubeModelLayer.CODEC);

        Registry.register(BLOCK_MODEL_CONNECTOR_REGISTRY, id("block"), BlockModelConnector.TYPE);
        Registry.register(BLOCK_MODEL_CONNECTOR_REGISTRY, id("render_tag"), RenderTagModelConnector.TYPE);

        ModelLoadingRegistry.INSTANCE.registerResourceProvider(KBlockModels::getResourceProvider);
    }

    private static ModelResourceProvider getResourceProvider(ResourceManager manager) {
        return (id, ctx) -> {
            Identifier modelId = new Identifier(id.getNamespace(), "models/" + id.getPath() + MODEL_EXTENSION);
            Optional<Resource> res = manager.getResource(modelId);
            if (res.isPresent()) {
                try {
                    JsonObject object = JsonHelper.deserialize(new InputStreamReader(res.get().getInputStream()));
                    return KUnbakedModel.CODEC.parse(JsonOps.INSTANCE, object).resultOrPartial(msg -> {
                        KMLRLog.LOG.error("Error parsing k-render JSON model '{}': {}", modelId, msg);
                        if (!GAVE_FORMAT_WARNING.getAndSet(true)) {
                            KMLRLog.LOG.error(
                                "If you are generating custom JSON models, be aware that models ending in {} are picked up by KModLib-Render and interpreted as its custom model JSON format instead of Minecraft's default model JSON format.",
                                MODEL_EXTENSION);
                        }
                    }).orElse(null);
                } catch (IOException e) {
                    KMLRLog.LOG.warn("Error loading k-render model: {}", modelId, e);
                    return null;
                }
            } else {
                return null;
            }
        };
    }
}
