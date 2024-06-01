package com.kneelawk.kmodlib.client.overlay;

import java.util.SequencedMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import com.kneelawk.commonevents.api.BusEvent;
import com.kneelawk.commonevents.api.Event;
import com.kneelawk.commonevents.api.Scan;
import com.kneelawk.commonevents.events.api.CommonEventsEvents;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;

/**
 * Event for rendering to the KModLib overlay.
 */
@Scan(side = Scan.Side.CLIENT)
@FunctionalInterface
public interface RenderToOverlay {
    /**
     * The allocator used for the {@link #CONSUMERS} buffers.
     */
    ByteBufferBuilder ALLOCATOR = new ByteBufferBuilder(256);

    /**
     * Mutable map of buffers used in the {@link MultiBufferSource} provided in the {@link OverlayRenderContext} in
     * {@link #renderToOverlay(OverlayRenderContext)}.
     * <p>
     * This has been made public for mods to add their own {@link RenderType}s.
     */
    SequencedMap<RenderType, ByteBufferBuilder> LAYER_MAP = new Object2ObjectLinkedOpenHashMap<>();

    /**
     * The {@link MultiBufferSource} provided in the {@link OverlayRenderContext} in
     * {@link #renderToOverlay(OverlayRenderContext)}.
     */
    MultiBufferSource CONSUMERS = MultiBufferSource.immediateWithBuffers(LAYER_MAP, ALLOCATOR);

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
