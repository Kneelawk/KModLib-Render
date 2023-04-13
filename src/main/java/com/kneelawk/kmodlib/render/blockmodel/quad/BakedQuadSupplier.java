package com.kneelawk.kmodlib.render.blockmodel.quad;

import org.jetbrains.annotations.NotNull;

/**
 * Describes something capable of drawing a requested quad.
 * <p>
 * A quad supplier may draw multiple quads, as long as they functionally appear to be the requested quad.
 */
public interface BakedQuadSupplier {
    /**
     * Draws the requested quad.
     *
     * @param request the parameters for the requested quad.
     */
    void draw(@NotNull QuadRequest request);
}
