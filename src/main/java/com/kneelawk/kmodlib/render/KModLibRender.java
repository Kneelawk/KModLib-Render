package com.kneelawk.kmodlib.render;

import net.fabricmc.api.ClientModInitializer;

import com.kneelawk.kmodlib.render.blockmodel.KBlockModels;

public class KModLibRender implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KBlockModels.init();
    }
}
