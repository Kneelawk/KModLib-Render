package com.kneelawk.kmodlib.render.blockmodel.sprite;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

/**
 * Supplies sprites to individual blocks at chunk-bake time.
 */
public interface BakedSpriteSupplier {
    /**
     * Gets the block sprite for this model.
     *
     * @param blockView      the view of the world (typically a chunk during client-side chunk-building).
     * @param state          the state of the block being baked to terrain.
     * @param pos            the position of the block.
     * @param randomSupplier the random supplier for this block.
     * @return the sprite for this block.
     */
    @Nullable Sprite getBlockSprite(BlockRenderView blockView, BlockState state, BlockPos pos,
                                    Supplier<Random> randomSupplier);

    /**
     * Gets the item sprite for this model.
     *
     * @param stack          the item stack.
     * @param randomSupplier the random supplier for this item.
     * @return the sprite for this item stack.
     */
    @Nullable Sprite getItemSprite(ItemStack stack, Supplier<Random> randomSupplier);
}
