package com.kneelawk.kmodlib.render.blockmodel;

import java.util.function.Supplier;

import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

public interface BakedModelLayer {
    void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos,
                        Supplier<Random> randomSupplier, RenderContext context);

    void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context);
}
