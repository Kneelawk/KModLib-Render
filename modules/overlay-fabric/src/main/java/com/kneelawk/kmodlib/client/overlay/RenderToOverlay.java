package com.kneelawk.kmodlib.client.overlay;

import java.util.SequencedMap;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;

/**
 * Event for rendering to the KModLib overlay.
 */
@FunctionalInterface
public interface RenderToOverlay {
    /**
     * The allocator used for the {@link #CONSUMERS} buffers.
     */
    BufferAllocator ALLOCATOR = new BufferAllocator(256);

    /**
     * Mutable map of buffers used in the {@link VertexConsumerProvider} provided in the {@link WorldRenderContext} in
     * {@link #renderToOverlay(WorldRenderContext)}.
     * <p>
     * This has been made public for mods to add their own {@link RenderLayer}s.
     */
    SequencedMap<RenderLayer, BufferAllocator> LAYER_MAP = new Object2ObjectLinkedOpenHashMap<>();

    /**
     * The {@link VertexConsumerProvider} provided in the {@link WorldRenderContext} in
     * {@link #renderToOverlay(WorldRenderContext)}.
     */
    VertexConsumerProvider CONSUMERS = VertexConsumerProvider.immediate(LAYER_MAP, ALLOCATOR);

    /**
     * Event fired when rendering to the KModLib overlay.
     */
    Event<RenderToOverlay> EVENT =
        EventFactory.createArrayBacked(RenderToOverlay.class, ctx -> {}, listeners -> ctx -> {
            for (RenderToOverlay listener : listeners) {
                listener.renderToOverlay(ctx);
            }
        });

    /**
     * Called to render something to the KModLib overlay.
     *
     * @param ctx the world render context.
     */
    void renderToOverlay(WorldRenderContext ctx);
}
