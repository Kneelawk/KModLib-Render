package com.kneelawk.kmodlib.client.blockmodel.connector;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import com.kneelawk.kmodlib.client.rendertag.RenderTags;

public record RenderTagModelConnector(Identifier tag) implements ModelConnector {
    private static final MapCodec<RenderTagModelConnector> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(Identifier.CODEC.fieldOf("tag").forGetter(RenderTagModelConnector::tag))
            .apply(instance, RenderTagModelConnector::new));
    public static final Type TYPE = new Decodable(CODEC);

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public boolean canConnect(BlockRenderView view, BlockPos pos, BlockPos otherPos, Direction normal, BlockState state,
                              BlockState otherState) {
        return RenderTags.isInTag(tag, otherState.getBlock());
    }
}
