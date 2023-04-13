package com.kneelawk.kmodlib.render.blockmodel.sprite;

import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

public interface BakedSpriteSupplier {
    Sprite getSprite(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier);
}
