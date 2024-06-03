package com.kneelawk.kmodlib.client.overlay;

import java.util.SequencedMap;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import com.kneelawk.commonevents.api.BusEvent;
import com.kneelawk.commonevents.api.Event;
import com.kneelawk.commonevents.api.Scan;
import com.kneelawk.commonevents.mainbus.api.CommonEventsMainBus;

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
    @BusEvent(CommonEventsMainBus.NAME)
    Event<RenderToOverlay> EVENT = Event.createSimple(RenderToOverlay.class);

    /**
     * Called to render something to the KModLib overlay.
     *
     * @param ctx the world render context.
     */
    void renderToOverlay(OverlayRenderContext ctx);
}
