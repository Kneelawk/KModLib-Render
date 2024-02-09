package com.kneelawk.kmodlib.client.overlay;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;

/**
 * Holds the overlay vertex consumers.
 */
public class RenderToOverlay {
    private RenderToOverlay() {}

    /**
     * Mutable map of buffers used in the {@link VertexConsumerProvider}.
     * <p>
     * This has been made public for mods to add their own {@link RenderLayer}s.
     */
    public static final Object2ObjectMap<RenderLayer, BufferBuilder> LAYER_MAP = new Object2ObjectLinkedOpenHashMap<>();

    /**
     * The overlay {@link VertexConsumerProvider}.
     */
    public static final VertexConsumerProvider CONSUMERS =
        VertexConsumerProvider.immediate(LAYER_MAP, new BufferBuilder(256));
}
