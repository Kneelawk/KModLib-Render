package com.kneelawk.kmodlib.render.blockmodel.cube;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import com.kneelawk.kmodlib.render.blockmodel.BakedModelLayer;
import com.kneelawk.kmodlib.render.blockmodel.sprite.BakedSpriteSupplier;

public record BakedSpriteCubeModelLayer(@Nullable ModelBakeSettings rotation, boolean cullFaces, boolean quarterFaces,
                                        float sideDepth, float faceDepth, RenderMaterial material,
                                        @Nullable BakedSpriteSupplier[] spriteSuppliers, int[] tintIndices)
    implements BakedModelLayer {
    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos,
                               Supplier<Random> randomSupplier, RenderContext context) {
        Sprite[] sprites = new Sprite[6];
        for (int i = 0; i < 6; i++) {
            BakedSpriteSupplier supplier = spriteSuppliers[i];
            if (supplier == null) continue;

            sprites[i] = supplier.getBlockSprite(blockView, state, pos, randomSupplier);
        }

        CubeModelUtils.emitCube(context.getEmitter(), rotation, cullFaces, quarterFaces, sideDepth, faceDepth,
            material, sprites, tintIndices);
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        Sprite[] sprites = new Sprite[6];
        for (int i = 0; i < 6; i++) {
            BakedSpriteSupplier supplier = spriteSuppliers[i];
            if (supplier == null) continue;

            sprites[i] = supplier.getItemSprite(stack, randomSupplier);
        }

        CubeModelUtils.emitCube(context.getEmitter(), rotation, cullFaces, quarterFaces, sideDepth, faceDepth,
            material, sprites, tintIndices);
    }
}
