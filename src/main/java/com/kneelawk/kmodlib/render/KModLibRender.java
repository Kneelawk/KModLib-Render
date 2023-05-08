package com.kneelawk.kmodlib.render;

import net.fabricmc.api.ClientModInitializer;

import com.kneelawk.kmodlib.render.blockmodel.KBlockModels;
import com.kneelawk.kmodlib.render.resource.KResources;

public class KModLibRender implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KResources.init();
        KBlockModels.init();
    }
}
