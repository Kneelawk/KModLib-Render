package com.kneelawk.kmodlib.client.blockmodel.connector;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import com.kneelawk.kmodlib.client.blockmodel.KBlockModels;

/**
 * Controls which connected models connect.
 */
public interface ModelConnector {
    /**
     * The default model connector that is used if none are supplied.
     */
    ModelConnector DEFAULT = BlockModelConnector.INSTANCE;

    /**
     * The model connector codec.
     * <p>
     * This is either a registry-lookup codec or an identifier codec. If an identifier is supplied, a singleton-type
     * model connector is expected. If an object is supplied, the <code>type</code> parameter is used to perform the
     * lookup but the result can be either a decodable-type or singleton-type model connector.
     */
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

    /**
     * @return the type of this model connector, registered with {@link KBlockModels#BLOCK_MODEL_CONNECTOR_REGISTRY}.
     */
    Type getType();

    /**
     * Tests if the model governed by this connector should connect.
     *
     * @param view       the view of the world (typically a chunk during client-side chunk-building).
     * @param pos        the position of the block being connected.
     * @param otherPos   the position of the block being connected to.
     * @param normal     the direction of the block being connected to from the block being connected.
     * @param state      the block state of the block being connected.
     * @param otherState the block state of the block being connected to.
     * @return whether this block should connect to the other block.
     */
    boolean canConnect(BlockRenderView view, BlockPos pos, BlockPos otherPos, Direction normal, BlockState state,
                       BlockState otherState);

    /**
     * A type of a model connector, for encoding and decoding purposes.
     */
    sealed interface Type {
    }

    /**
     * A singleton-type model connector type.
     * @param instance the single instance of the model connector.
     */
    record Singleton(ModelConnector instance) implements Type {
    }

    /**
     * A decodable-type model connector type.
     * @param codec the codec used for encoding and decoding instances of the model connector.
     */
    record Decodable(Codec<? extends ModelConnector> codec) implements Type {
    }
}
