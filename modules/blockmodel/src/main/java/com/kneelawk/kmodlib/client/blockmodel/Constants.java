package com.kneelawk.kmodlib.client.blockmodel;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.Identifier;

@ApiStatus.Internal
public class Constants {
    public static final String MOD_ID = "kmodlib-blockmodel";

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
