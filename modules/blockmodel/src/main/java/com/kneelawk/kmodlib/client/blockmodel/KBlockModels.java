package com.kneelawk.kmodlib.client.blockmodel;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import com.kneelawk.codextra.api.attach.AttachmentKey;
import com.kneelawk.kmodlib.client.blockmodel.connector.BlockModelConnector;
import com.kneelawk.kmodlib.client.blockmodel.connector.ModelConnector;
import com.kneelawk.kmodlib.client.blockmodel.connector.RenderTagModelConnector;
import com.kneelawk.kmodlib.client.blockmodel.ct.UnbakedCTLayer;
import com.kneelawk.kmodlib.client.blockmodel.cube.UnbakedBottomTopModelLayer;
import com.kneelawk.kmodlib.client.blockmodel.cube.UnbakedColumnModelLayer;
import com.kneelawk.kmodlib.client.blockmodel.cube.UnbakedCubeAllModelLayer;
import com.kneelawk.kmodlib.client.blockmodel.cube.UnbakedCubeModelLayer;
import com.kneelawk.kmodlib.client.blockmodel.modelref.UnbakedModelRefModelLayer;
import com.kneelawk.kmodlib.client.blockmodel.sprite.UnbakedSpriteSupplier;

import static com.kneelawk.kmodlib.client.blockmodel.Constants.id;

/**
 * Class containing registries for block models.
 */
public class KBlockModels {
    /**
     * The filename extension of KModLib-Render block models.
     */
    public static final String MODEL_EXTENSION_1 = ".kml.json";
    public static final String MODEL_EXTENSION_2 = ".json.kml";

    public static final ResourceFinder FINDER_1 = new ResourceFinder("models", MODEL_EXTENSION_1);
    public static final ResourceFinder FINDER_2 = new ResourceFinder("models", MODEL_EXTENSION_2);

    public static AttachmentKey<Identifier> MODEL_ID_KEY = AttachmentKey.ofStaticFieldName();

    private static final Identifier BLOCK_MODEL_REGISTRY_ID = id("block_model");
    private static final Identifier BLOCK_MODEL_LAYER_REGISTRY_ID = id("block_model_layer");
    private static final Identifier BLOCK_MODEL_CONNECTOR_REGISTRY_ID = id("block_model_connector");
    private static final Identifier SPRITE_SUPPLIER_REGISTRY_ID = id("sprite_supplier");
    private static final RegistryKey<Registry<MapCodec<? extends KUnbakedModel>>> BLOCK_MODEL_REGISTRY_KEY =
        RegistryKey.ofRegistry(BLOCK_MODEL_REGISTRY_ID);
    private static final RegistryKey<Registry<MapCodec<? extends UnbakedModelLayer>>> BLOCK_MODEL_LAYER_REGISTRY_KEY =
        RegistryKey.ofRegistry(BLOCK_MODEL_LAYER_REGISTRY_ID);
    private static final RegistryKey<Registry<ModelConnector.Type>> BLOCK_MODEL_CONNECTOR_REGISTRY_KEY =
        RegistryKey.ofRegistry(BLOCK_MODEL_CONNECTOR_REGISTRY_ID);
    private static final RegistryKey<Registry<MapCodec<? extends UnbakedSpriteSupplier>>> SPRITE_SUPPLIER_REGISTRY_KEY =
        RegistryKey.ofRegistry(SPRITE_SUPPLIER_REGISTRY_ID);

    /**
     * Registry for registering codecs of {@link KUnbakedModel}s in.
     */
    public static final Registry<MapCodec<? extends KUnbakedModel>> BLOCK_MODEL_REGISTRY =
        new SimpleRegistry<>(BLOCK_MODEL_REGISTRY_KEY, Lifecycle.stable());

    /**
     * Registry for registering codecs of {@link UnbakedModelLayer}s in.
     */
    public static final Registry<MapCodec<? extends UnbakedModelLayer>> BLOCK_MODEL_LAYER_REGISTRY =
        new SimpleRegistry<>(BLOCK_MODEL_LAYER_REGISTRY_KEY, Lifecycle.stable());

    /**
     * Registry for registering types of {@link ModelConnector}s in.
     */
    public static final Registry<ModelConnector.Type> BLOCK_MODEL_CONNECTOR_REGISTRY =
        new SimpleRegistry<>(BLOCK_MODEL_CONNECTOR_REGISTRY_KEY, Lifecycle.stable());

    /**
     * Registry for registering codecs of {@link UnbakedSpriteSupplier}s in.
     */
    public static final Registry<MapCodec<? extends UnbakedSpriteSupplier>> SPRITE_SUPPLIER_REGISTRY =
        new SimpleRegistry<>(SPRITE_SUPPLIER_REGISTRY_KEY, Lifecycle.stable());

    private static final AtomicBoolean GAVE_FORMAT_WARNING_1 = new AtomicBoolean(false);
    private static final AtomicBoolean GAVE_FORMAT_WARNING_2 = new AtomicBoolean(false);

    @SuppressWarnings("unchecked")
    @ApiStatus.Internal
    public static void init() {
        Registry.register((Registry<? super Registry<?>>) Registries.REGISTRIES,
            BLOCK_MODEL_REGISTRY_ID, BLOCK_MODEL_REGISTRY);
        Registry.register((Registry<? super Registry<?>>) Registries.REGISTRIES,
            BLOCK_MODEL_LAYER_REGISTRY_ID, BLOCK_MODEL_LAYER_REGISTRY);

        Registry.register(BLOCK_MODEL_REGISTRY, id("layered"), UnbakedLayeredModel.CODEC);

        Registry.register(BLOCK_MODEL_LAYER_REGISTRY, id("quarter_connected_texture"), UnbakedCTLayer.CODEC);
        Registry.register(BLOCK_MODEL_LAYER_REGISTRY, id("cube_all"), UnbakedCubeAllModelLayer.CODEC);
        Registry.register(BLOCK_MODEL_LAYER_REGISTRY, id("cube_bottom_top"), UnbakedBottomTopModelLayer.CODEC);
        Registry.register(BLOCK_MODEL_LAYER_REGISTRY, id("cube_column"), UnbakedColumnModelLayer.CODEC);
        Registry.register(BLOCK_MODEL_LAYER_REGISTRY, id("cube"), UnbakedCubeModelLayer.CODEC);
        Registry.register(BLOCK_MODEL_LAYER_REGISTRY, id("model_ref"), UnbakedModelRefModelLayer.CODEC);

        Registry.register(BLOCK_MODEL_CONNECTOR_REGISTRY, id("block"), BlockModelConnector.TYPE);
        Registry.register(BLOCK_MODEL_CONNECTOR_REGISTRY, id("render_tag"), RenderTagModelConnector.TYPE);

        PreparableModelLoadingPlugin.register(
            (resourceManager, executor) -> CompletableFuture.completedFuture(resourceManager),
            KBlockModels::onInitializeModeLoader);
    }

    private static void onInitializeModeLoader(ResourceManager manager, ModelLoadingPlugin.Context context) {
        ImmutableMap.Builder<Identifier, Resource> modelsBuilder = ImmutableMap.builder();
        modelsBuilder.putAll(FINDER_1.findResources(manager));
        modelsBuilder.putAll(FINDER_2.findResources(manager));
        Map<Identifier, Resource> models = modelsBuilder.build();

        context.resolveModel().register(ctx -> {
            Identifier path1 = FINDER_1.toResourcePath(ctx.id());
            if (models.containsKey(path1))
                return tryLoadModel(path1, ctx.id(), Objects.requireNonNull(models.get(path1)), GAVE_FORMAT_WARNING_1,
                    MODEL_EXTENSION_1);

            Identifier path2 = FINDER_2.toResourcePath(ctx.id());
            if (models.containsKey(path2))
                return tryLoadModel(path2, ctx.id(), Objects.requireNonNull(models.get(path2)), GAVE_FORMAT_WARNING_2,
                    MODEL_EXTENSION_2);

            return null;
        });
    }

    @Nullable
    private static KUnbakedModel tryLoadModel(Identifier path, Identifier id, Resource res,
                                              AtomicBoolean gaveFormatWarning, String extension) {
        try {
            JsonObject object = JsonHelper.deserialize(new InputStreamReader(res.getInputStream()));

            DynamicOps<JsonElement> ops = JsonOps.INSTANCE;
            ops = MODEL_ID_KEY.push(ops, id);

            return KUnbakedModel.CODEC.parse(ops, object).resultOrPartial(msg -> {
                KLog.LOG.error("Error parsing k-render JSON model '{}': {}", path, msg);
                if (!gaveFormatWarning.getAndSet(true)) {
                    KLog.LOG.error(
                        "If you are generating custom JSON models, be aware that models ending in {} are picked up by KModLib-Render and interpreted as its custom model JSON format instead of Minecraft's default model JSON format.",
                        extension);
                }
            }).orElse(null);
        } catch (IOException e) {
            KLog.LOG.warn("Error loading k-render model: {}", path, e);
            return null;
        }
    }
}
