package com.kneelawk.kmodlib.client.blockmodel;

import java.util.function.Supplier;

import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

/**
 * A layer of a baked model.
 */
public interface BakedModelLayer {
    /**
     * Emits block quads.
     *
     * @param blockView      the view of the world (typically a chunk during client-side chunk-building).
     * @param state          the state of this block.
     * @param pos            the position of the block.
     * @param randomSupplier the random supplier for this block.
     * @param context        the context to emit block quads to.
     */
    void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos,
                        Supplier<Random> randomSupplier, RenderContext context);

    /**
     * Emits item quads.
     *
     * @param stack          the item stack that quads are being emitted for.
     * @param randomSupplier this stack's random supplier.
     * @param context        the context to emit item quads to.
     */
    void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context);
}
