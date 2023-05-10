package com.kneelawk.kmodlib.client.blockmodel;

import java.util.function.Supplier;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

/**
 * A baked model layer that contains a sole static mesh to be rendered both as a block and an item.
 *
 * @param mesh the mesh.
 */
public record BakedMeshModelLayer(Mesh mesh) implements BakedModelLayer {
    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos,
                               Supplier<Random> randomSupplier, RenderContext context) {
        context.meshConsumer().accept(mesh);
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        context.meshConsumer().accept(mesh);
    }
}
