package com.kneelawk.kmodlib.client.blockmodel.sprite;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

public record BakedStaticSpriteSupplier(Sprite sprite) implements BakedSpriteSupplier {
    @Override
    public Sprite getBlockSprite(BlockRenderView blockView, BlockState state, BlockPos pos,
                                 Supplier<Random> randomSupplier, @Nullable Direction normal) {
        return sprite;
    }

    @Override
    public Sprite getItemSprite(ItemStack stack, Supplier<Random> randomSupplier) {
        return sprite;
    }
}
