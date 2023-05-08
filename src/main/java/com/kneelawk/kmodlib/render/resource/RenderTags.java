package com.kneelawk.kmodlib.render.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;

import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;

import com.mojang.serialization.JsonOps;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import com.kneelawk.kmodlib.render.KMLRLog;

import static com.kneelawk.kmodlib.render.Constants.id;

/**
 * Manages render-tags.
 */
public final class RenderTags
    implements SimpleResourceReloadListener<Table<RegistryKey<Registry<?>>, Identifier, Set<RegistryKey<?>>>> {
    static final RenderTags INSTANCE = new RenderTags();

    private static final Identifier ID = id("render_tags");

    private static Table<RegistryKey<Registry<?>>, Identifier, Set<RegistryKey<?>>> table = null;

    private RenderTags() {
    }

    /**
     * Checks if a block is in a render-tag.
     *
     * @param tag   the render-tag.
     * @param block the block.
     * @return whether the block is in the render-tag.
     */
    public static boolean isInTag(Identifier tag, Block block) {
        Optional<RegistryKey<Block>> key = Registries.BLOCK.getKey(block);
        return key.filter(blockRegistryKey -> isInTag(tag, RegistryKeys.BLOCK, blockRegistryKey)).isPresent();
    }

    /**
     * Checks if a registry-key is in a render-tag.
     *
     * @param tag the render-tag.
     * @param key the registry-key.
     * @param <T> the type of the registry-key.
     * @return whether the registry-key is in the render-tag.
     */
    public static <T> boolean isInTag(Identifier tag, RegistryKey<T> key) {
        return isInTag(tag, RegistryKey.ofRegistry(key.getRegistry()), key);
    }

    private static <T> boolean isInTag(Identifier tag, RegistryKey<Registry<T>> registryKey, RegistryKey<T> key) {
        checkTable();

        Set<RegistryKey<?>> set = table.get(registryKey, tag);
        if (set == null) return false;
        return set.contains(key);
    }

    private static void checkTable() {
        if (table == null) throw new IllegalStateException(
            "Render-tag table not available because resources have not been loaded yet");
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<Table<RegistryKey<Registry<?>>, Identifier, Set<RegistryKey<?>>>> load(
        ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Table<RegistryKey<Registry<?>>, Identifier, Set<RegistryKey<?>>> table = HashBasedTable.create();

            KMLRLog.LOG.info("Loading render-tags...");
            profiler.push("kmodlib-render:render_tags.load");

            for (RegistryKey<? extends Registry<?>> registryKey : Registries.REGISTRIES.getKeys()) {
                Identifier registryId = registryKey.getValue();
                String registryPath = ID.getNamespace() + "/" + ID.getPath() + "/" + registryId.getNamespace() + "/" +
                    registryId.getPath();

                profiler.push(() -> "kmodlib-render:render_tags.load." + registryId);

                Map<Identifier, List<Resource>> resources =
                    manager.findAllResources(registryPath, id -> id.getPath().endsWith(".json"));
                for (Map.Entry<Identifier, List<Resource>> entry : resources.entrySet()) {
                    Identifier fileId = entry.getKey();
                    String filePath = fileId.getPath();
                    Identifier tag = new Identifier(fileId.getNamespace(),
                        filePath.substring(registryPath.length() + 1, filePath.length() - ".json".length()));

                    ImmutableSet.Builder<RegistryKey<?>> keysBuilder = ImmutableSet.builder();

                    for (Resource res : entry.getValue()) {
                        try (BufferedReader reader = res.getReader()) {
                            JsonRenderTag json = JsonRenderTag.CODEC.decode(JsonOps.INSTANCE, JsonHelper.deserialize(reader)).getOrThrow(false, KMLRLog.LOG::error).getFirst();

                            if (json.replace()) {
                                keysBuilder = ImmutableSet.builder();
                            }

                            keysBuilder.addAll(json.values().stream().map(id -> RegistryKey.of((RegistryKey<Registry<Object>>) registryKey, id)).toList());

                            if (json.replace()) {
                                break;
                            }
                        } catch (Exception e) {
                            KMLRLog.LOG.error("Error loading render-key: {}", fileId, e);
                        }
                    }

                    Set<RegistryKey<?>> keys = keysBuilder.build();
                    if (!keys.isEmpty()) {
                        table.put((RegistryKey<Registry<?>>) registryKey, tag, keys);
                    }
                }

                profiler.pop();
            }

            profiler.pop();
            KMLRLog.LOG.info("Render-tags loaded.");

            return table;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(Table<RegistryKey<Registry<?>>, Identifier, Set<RegistryKey<?>>> data,
                                         ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> table = data, executor);
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }
}
