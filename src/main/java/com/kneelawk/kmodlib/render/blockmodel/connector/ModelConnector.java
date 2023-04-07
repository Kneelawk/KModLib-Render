package com.kneelawk.kmodlib.render.blockmodel.connector;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import com.kneelawk.kmodlib.render.blockmodel.KBlockModels;

public interface ModelConnector {
    ModelConnector DEFAULT = BlockModelConnector.INSTANCE;

    Codec<ModelConnector> CODEC = Codec.either(Identifier.CODEC,
        KBlockModels.BLOCK_MODEL_CONNECTOR_REGISTRY.getCodec().dispatch(ModelConnector::getType, type -> {
            // this should only ever happen if an object was decoded with a type corresponding to a singleton type
            if (type instanceof Singleton s) return Codec.unit(s.instance());

                // return the decodable's codec, so we can decode an object
            else if (type instanceof Decodable d) return d.codec();

                // this should be impossible
            else throw new IncompatibleClassChangeError();
        })
    ).flatXmap(
        either -> either.map(id -> {
                // this side of the either is where we just decoded an Identifier instead of a whole object, so we get the
                // associated type from the registry
                Type type = KBlockModels.BLOCK_MODEL_CONNECTOR_REGISTRY.get(id);

                // if the registry doesn't have the id we were given, we return an error
                if (type == null) return DataResult.error(() -> "No block model connector type with id '" + id + "'");

                    // if the registered type describes a singleton, we just return that singleton
                else if (type instanceof Singleton s) return DataResult.success(s.instance());

                    // not enough information is available with just an id, so we return an error
                else return DataResult.error(() -> "Unable to decode block model connector with id '" + id +
                        "' from a string because it requires extra information. It can only be decoded from an object.");
            },
            // this side of the either is where we already decoded a whole object
            DataResult::success),
        // here we're converting ModelConnectors back into, either an identifier or just telling it to encode a full object
        conn -> {
            // get the ModelConnector's type, so we can tell whether it's a singleton or no
            Type type = conn.getType();

            // if it's a singleton, we only want to encode the id
            if (type instanceof Singleton) {
                Identifier id = KBlockModels.BLOCK_MODEL_CONNECTOR_REGISTRY.getId(type);

                // if the type doesn't correspond to an id in the registry, return an error
                if (id == null) return DataResult.error(
                    () -> "Attempting to encode a block model connector with a type that has not been registered.");

                // return the id associated with the ModelConnector's type
                return DataResult.success(Either.left(id));

                // otherwise, just tell it to try and encode a full object
            } else if (type instanceof Decodable) return DataResult.success(Either.right(conn));

            // this should be impossible
            else throw new IncompatibleClassChangeError();
        });

    Type getType();

    boolean canConnect(BlockRenderView view, BlockPos pos, BlockPos otherPos, Direction normal, BlockState state,
                       BlockState otherState);

    sealed interface Type {
    }

    record Singleton(ModelConnector instance) implements Type {
    }

    record Decodable(Codec<? extends ModelConnector> codec) implements Type {
    }
}
