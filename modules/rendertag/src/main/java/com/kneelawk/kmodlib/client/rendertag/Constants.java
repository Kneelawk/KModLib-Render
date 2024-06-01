package com.kneelawk.kmodlib.client.rendertag;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.Identifier;

@ApiStatus.Internal
public class Constants {
    public static final String MOD_ID = "kmodlib-rendertag";

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
