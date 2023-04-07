package com.kneelawk.kmodlib.render.blockmodel.connector;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

public class BlockModelConnector implements ModelConnector {
    public static final BlockModelConnector INSTANCE = new BlockModelConnector();
    public static final Type TYPE = new Singleton(INSTANCE);

    private BlockModelConnector() {
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public boolean canConnect(BlockRenderView view, BlockPos pos, BlockPos otherPos, Direction normal, BlockState state,
                              BlockState otherState) {
        return otherState.isOf(state.getBlock());
    }
}
