package com.kneelawk.kmodlib.client.rendertag;

import net.fabricmc.api.ClientModInitializer;

public class KMLRenderTagMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KResources.init();
    }
}
