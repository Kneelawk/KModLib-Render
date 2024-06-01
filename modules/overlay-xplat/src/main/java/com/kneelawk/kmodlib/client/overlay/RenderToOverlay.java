package com.kneelawk.kmodlib.client.overlay;

import java.util.SequencedMap;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;

import com.kneelawk.commonevents.api.BusEvent;
import com.kneelawk.commonevents.api.Event;
import com.kneelawk.commonevents.api.Scan;
import com.kneelawk.commonevents.events.api.CommonEventsEvents;

/**
 * Event for rendering to the KModLib overlay.
 */
@Scan(side = Scan.Side.CLIENT)
@FunctionalInterface
public interface RenderToOverlay {
    /**
     * The allocator used for the {@link #CONSUMERS} buffers.
     */
    BufferAllocator ALLOCATOR = new BufferAllocator(256);

    /**
     * Mutable map of buffers used in the {@link VertexConsumerProvider} provided in the {@link OverlayRenderContext} in
     * {@link #renderToOverlay(OverlayRenderContext)}.
     * <p>
     * This has been made public for mods to add their own {@link RenderLayer}s.
     */
    SequencedMap<RenderLayer, BufferAllocator> LAYER_MAP = new Object2ObjectLinkedOpenHashMap<>();

    /**
     * The {@link VertexConsumerProvider} provided in the {@link OverlayRenderContext} in
     * {@link #renderToOverlay(OverlayRenderContext)}.
     */
    VertexConsumerProvider CONSUMERS = VertexConsumerProvider.immediate(LAYER_MAP, ALLOCATOR);

    /**
     * Event fired when rendering to the KModLib overlay.
     */
    @BusEvent(CommonEventsEvents.MAIN_EVENT_BUS_NAME)
    Event<RenderToOverlay> EVENT = Event.create(RenderToOverlay.class, ctx -> {}, listeners -> ctx -> {
        for (RenderToOverlay listener : listeners) {
            listener.renderToOverlay(ctx);
        }
    });

    /**
     * Called to render something to the KModLib overlay.
     *
     * @param ctx the world render context.
     */
    void renderToOverlay(OverlayRenderContext ctx);
}
