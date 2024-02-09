package com.kneelawk.kmodlib.client.overlay;

import com.mojang.blaze3d.vertex.BufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

/**
 * Holds the overlay vertex consumers.
 */
public class RenderToOverlay {
    private RenderToOverlay() {}

    /**
     * Mutable map of buffers used in the {@link MultiBufferSource}.
     * <p>
     * This has been made public for mods to add their own {@link RenderType}s.
     */
    public static final Object2ObjectMap<RenderType, BufferBuilder> LAYER_MAP = new Object2ObjectLinkedOpenHashMap<>();

    /**
     * The overlay {@link MultiBufferSource}.
     */
    public static final MultiBufferSource CONSUMERS =
        MultiBufferSource.immediateWithBuffers(LAYER_MAP, new BufferBuilder(256));
}
