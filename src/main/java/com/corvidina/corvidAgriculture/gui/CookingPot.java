package com.corvidina.corvidAgriculture.gui;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.context.RenderContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CookingPot extends View {

    @Override
    public void onInit(ViewConfigBuilder config){
        config.cancelOnClick();
        config.type(ViewType.CHEST);
        config.layout(
                "ABABABABA",
                "ABABABABA",
                "A A AB BA",
                "A ABABABA",
                "ABABABABA"
        );
        config.title(Component.text()
                .content("Cooking Pot & Meals")
                .style(Style.style(TextColor.color(0x18516),TextDecoration.BOLD,TextDecoration.ITALIC.withState(false)))
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
        render.layoutSlot('A',green);
        render.layoutSlot('B',brown);

        ItemStack temp = new ItemStack(Material.CAULDRON);
        render.slot(3,2,temp);

        temp = new ItemStack(Material.CAMPFIRE);
        render.slot(4,2,temp);

        temp = new ItemStack(Material.KNOWLEDGE_BOOK);
        temp.setData(DataComponentTypes.ITEM_NAME, Component.text()
                .content("<- Cooking Pot Structure")
                .style(Style.style(TextColor.color(0xBE0018), TextDecoration.BOLD,TextDecoration.ITALIC.withState(false)))
                .build()
        );
        render.slot(3,4,temp);

        temp = new ItemStack(Material.BOOK);
        render.slot(3,7,temp);
    }
}
