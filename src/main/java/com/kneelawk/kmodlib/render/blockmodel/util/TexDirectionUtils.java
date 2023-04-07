package com.kneelawk.kmodlib.render.blockmodel.util;

import java.util.Arrays;

import net.minecraft.util.math.Direction;

public class TexDirectionUtils {
    private static final Direction[] UPS = {
        Direction.SOUTH,
        Direction.NORTH,
        Direction.UP,
        Direction.UP,
        Direction.UP,
        Direction.UP
    };

    private static final Direction[] DOWNS = Arrays.stream(UPS).map(Direction::getOpposite).toArray(Direction[]::new);

    private static final Direction[] RIGHTS = {
        Direction.EAST,
        Direction.EAST,
        Direction.WEST,
        Direction.EAST,
        Direction.SOUTH,
        Direction.NORTH
    };

    private static final Direction[] LEFTS =
        Arrays.stream(RIGHTS).map(Direction::getOpposite).toArray(Direction[]::new);

    public static Direction texUp(Direction normal) {
        return UPS[normal.getId()];
    }

    public static Direction texDown(Direction normal) {
        return DOWNS[normal.getId()];
    }

    public static Direction texRight(Direction normal) {
        return RIGHTS[normal.getId()];
    }

    public static Direction texLeft(Direction normal) {
        return LEFTS[normal.getId()];
    }
}
