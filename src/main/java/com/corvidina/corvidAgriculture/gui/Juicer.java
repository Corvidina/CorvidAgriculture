package com.corvidina.corvidAgriculture.gui;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.context.RenderContext;
import net.kyori.adventure.text.Component;

public class Juicer extends View {

    @Override
    public void onInit(ViewConfigBuilder config){
        config.layout(
                "ABABABABA",
                "ABABABABA",
                "ABABABABA",
                "ABABABABA",
                "ABABABABA",
                "ABABABABA"
        );
        config.title(Component.text()
                .content("Juicer & Liquids")
        );
        //config.cancelOnClick();
    }

    @Override
    public void onFirstRender(RenderContext context){

    }
}
