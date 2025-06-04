package com.corvidina.corvidAgriculture.gui;

import com.corvidina.corvidAgriculture.CorvidAgriculture;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ServerCatering extends View {
    private ItemStack itemStack;
    @Override
    public void onInit(ViewConfigBuilder config){
        int rand = (int)(Math.random()*Material.values().length);
        itemStack = new ItemStack(Material.values()[rand]);


        config.type(ViewType.CHEST);
        config.cancelOnClick();
        config.layout(
                "ABAB BABA",
                "ABCCCCCBA", //Easy level deliveries
                "ABCCCCCBA", //Medium difficulty deliveries
                "ABCCCCCBA", //Difficult deliveries
                "ABABCBABA"
        );
        config.title(Component.text()
                .content("Catering Meals")
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
        render.layoutSlot('A',green);
        render.layoutSlot('B',brown);

        ItemStack temp = new ItemStack(Material.KNOWLEDGE_BOOK);
        temp.setData(DataComponentTypes.ITEM_NAME,Component.text()
                .content("Deliveries")
                .style(Style.style(TextColor.color(0xD5FF),TextDecoration.BOLD,TextDecoration.ITALIC.withState(false)))
                .build()
        );
        ItemLore lore = ItemLore.lore().addLine(
                Component.text()
                        .content("Time until next set of deliveries: "+timeTilNextDelivery())
                        .style(Style.style(TextColor.color(0xAAAAAA),TextDecoration.ITALIC.withState(false)))
                        .build()
        ).build();
        temp.setData(DataComponentTypes.LORE,lore);
        render.slot(1,5,temp);

        temp = new ItemStack(Material.CHEST_MINECART);
        temp.setData(DataComponentTypes.ITEM_NAME, Component.text()
                .content("Your Deliveries")
                .style(Style.style(TextColor.color(0xA222),TextDecoration.BOLD,TextDecoration.ITALIC.withState(false)))
                .build()
        );
        render.slot(5,5,temp);

        temp = new ItemStack(Material.ARROW);
        temp.setData(DataComponentTypes.ITEM_NAME, Component.text()
                .content("Back")
                .style(Style.style(TextDecoration.BOLD,TextDecoration.ITALIC.withState(false)))
                .build()
        );
        render.slot(5,1,temp).onClick(this::openPlayerAgricultureMenu);

        render.slot(2,3,itemStack);

    }

    private void openPlayerAgricultureMenu(SlotClickContext slot){
        slot.closeForPlayer();
        AgricultureMenu.initAgricultureMenu().open(AgricultureMenu.class,slot.getPlayer());
    }

    public void reloadCaterings(){
        // Reloads the available catering options



    }

    private String timeTilNextDelivery(){
        CorvidAgriculture plugin = CorvidAgriculture.getPlugin(CorvidAgriculture.class);
        return secsToStandard(plugin.getCateringAdditionRate()-((Bukkit.getCurrentTick()/20)%plugin.getCateringAdditionRate()));
    }

    private static String secsToStandard(double time){
        int temp = (int)time;
        String s = "";
        s+=temp/3600;
        temp=temp%3600;
        s+=":"+temp/60;
        temp=temp%60;
        s+=":"+temp;
        return s;
    }
}
