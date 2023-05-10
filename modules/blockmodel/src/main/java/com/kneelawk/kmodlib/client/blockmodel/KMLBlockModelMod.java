package com.kneelawk.kmodlib.client.blockmodel;

import net.fabricmc.api.ClientModInitializer;

public class KMLBlockModelMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KBlockModels.init();
    }
}
