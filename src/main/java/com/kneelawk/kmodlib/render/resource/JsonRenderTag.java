package com.kneelawk.kmodlib.render.resource;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;

public record JsonRenderTag(boolean replace, List<Identifier> values) {
    public static Codec<JsonRenderTag> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.fieldOf("replace").forGetter(JsonRenderTag::replace),
        Identifier.CODEC.listOf().fieldOf("values").forGetter(JsonRenderTag::values)
    ).apply(instance, JsonRenderTag::new));
}
