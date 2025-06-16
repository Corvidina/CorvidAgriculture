package com.corvidina.corvidAgriculture.gui;

import com.corvidina.corvidAgriculture.items.Crop;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.context.OpenContext;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CropCompendium extends View {
    @Override
    public void onInit(ViewConfigBuilder config){
        config.type(ViewType.CHEST);
        config.layout(
                "ABABABABA",
                "A       A",
                "A       A",
                " BA A ABA"
        );
        config.title(Component.text()
                .content("Sell Crops")
                .style(Style.style(TextColor.color(0x18516), TextDecoration.BOLD))
                .build()
        );
        config.cancelOnClick();
        config.build();
    }

    @Override
    public void onFirstRender(RenderContext render){
        final double[] weighed = new double[]{
                0,1,0,0,0,0
        };
        double[] save = ItemHandler.getValueChances();
        final ItemStack green = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        green.setData(DataComponentTypes.ITEM_NAME, Component.text().content(" ").build());
        green.setData(DataComponentTypes.HIDE_TOOLTIP);
        final ItemStack brown = new ItemStack(Material.BROWN_STAINED_GLASS_PANE);
        brown.setData(DataComponentTypes.ITEM_NAME, Component.text().content(" ").build());
        brown.setData(DataComponentTypes.HIDE_TOOLTIP);
        render.layoutSlot('A',green);
        render.layoutSlot('B',brown);


        ItemStack temp = new ItemStack(Material.ARROW);
        temp.setData(DataComponentTypes.ITEM_NAME,Component.text()
                .content("Back")
                .style(Style.style(TextDecoration.BOLD,TextDecoration.ITALIC.withState(false)))
                .build()
        );
        render.slot(4,1,temp)
                .onClick(context -> context.openForPlayer(AgricultureMenu.class));

        ItemHandler.setValueChances(weighed);
        render.slot(2,2,ItemHandler.buildItemBkt(Crop.PRICKLY_PEAR))
                .onClick(this::clickedSlot);
        render.slot(2,3,ItemHandler.buildItemBkt(Crop.CRANBERRY))
                .onClick(this::clickedSlot);
        ItemHandler.setValueChances(save);


    }

    private void clickedSlot(SlotClickContext click){
        final Player player = click.getPlayer();
        player.getInventory().addItem(click.getItem());
    }

    @Override
    public void onOpen(OpenContext open){

    }

    @Override
    public void onClick(SlotClickContext context){

    }

}
