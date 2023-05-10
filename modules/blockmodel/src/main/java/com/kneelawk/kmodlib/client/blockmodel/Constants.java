package com.kneelawk.kmodlib.client.blockmodel;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.Identifier;

@ApiStatus.Internal
public class Constants {
    public static final String MOD_ID = "kmodlib-block";

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
