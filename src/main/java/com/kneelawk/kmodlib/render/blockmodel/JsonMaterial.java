package com.kneelawk.kmodlib.render.blockmodel;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record JsonMaterial(BlendMode blendMode, boolean ambientOcclusion, boolean hasColorIndex, boolean diffuseShading,
                           boolean emissive) {
    public static final JsonMaterial DEFAULT = new JsonMaterial(BlendMode.DEFAULT, true, true, true, false);

    public static final Codec<JsonMaterial> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.comapFlatMap(str -> {
                try {
                    return DataResult.success(BlendMode.valueOf(str.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    String validBlendModes =
                        Arrays.stream(BlendMode.values()).map(mode -> mode.name().toLowerCase()).collect(
                            Collectors.joining(", ", "[", "]"));
                    return DataResult.error(
                        () -> "Unknown blend mode: '" + str + "'. Valid blend modes are: " + validBlendModes);
                }
            }, mode -> mode.name().toLowerCase()).optionalFieldOf("blend_mode", BlendMode.DEFAULT)
            .forGetter(JsonMaterial::blendMode),
        Codec.BOOL.optionalFieldOf("ambient_occlusion", true).forGetter(JsonMaterial::ambientOcclusion),
        Codec.BOOL.optionalFieldOf("has_color_index", true).forGetter(JsonMaterial::hasColorIndex),
        Codec.BOOL.optionalFieldOf("diffuse_shading", true).forGetter(JsonMaterial::diffuseShading),
        Codec.BOOL.optionalFieldOf("emissive", false).forGetter(JsonMaterial::emissive)
    ).apply(instance, JsonMaterial::new));

    public RenderMaterial toRenderMaterial() {
        return Objects.requireNonNull(RendererAccess.INSTANCE.getRenderer(), "No FRAPI renderer available")
            .materialFinder()
            .blendMode(0, blendMode)
            .disableAo(0, !ambientOcclusion)
            .disableColorIndex(0, !hasColorIndex)
            .disableDiffuse(0, !diffuseShading)
            .emissive(0, emissive)
            .find();
    }
}
