package com.kneelawk.kmodlib.render.resource;

import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.resource.ResourceType;

public class KResources {
    @ApiStatus.Internal
    public static void init() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(RenderTags.INSTANCE);
    }
}
