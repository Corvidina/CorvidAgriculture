package com.corvidina.corvidAgriculture.gui;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.context.CloseContext;
import me.devnatan.inventoryframework.context.RenderContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CropSell extends View {

    @Override
    public void onInit(ViewConfigBuilder config){
        config.type(ViewType.CHEST);
        config.layout(
                "ABABABABA",
                "A       A",
                "A       A",
                "A       A",
                " BA A ABA"
        );
        config.title(Component.text()
                .content("Crop Compendium")
                .style(Style.style(TextColor.color(0x18516), TextDecoration.BOLD))
                .build()
        );
        config.build();
    }

    @Override
    public void onFirstRender(RenderContext render){
        final ItemStack green = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        green.setData(DataComponentTypes.ITEM_NAME, Component.text().content(" ").build());
        green.setData(DataComponentTypes.HIDE_TOOLTIP);
        final ItemStack brown = new ItemStack(Material.BROWN_STAINED_GLASS_PANE);
        brown.setData(DataComponentTypes.ITEM_NAME, Component.text().content(" ").build());
        brown.setData(DataComponentTypes.HIDE_TOOLTIP);
        render.layoutSlot('A',green).onClick(click->click.setCancelled(true));
        render.layoutSlot('B',brown).onClick(click->click.setCancelled(true));


    }

    @Override
    public void onClose(CloseContext close){

    }
}
