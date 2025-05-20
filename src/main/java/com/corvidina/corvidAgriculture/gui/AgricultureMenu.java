package com.corvidina.corvidAgriculture.gui;

import com.corvidina.corvidAgriculture.CorvidAgriculture;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.devnatan.inventoryframework.*;
import me.devnatan.inventoryframework.context.OpenContext;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class AgricultureMenu extends View {
    @Override
    public void onInit(ViewConfigBuilder config){
        config.type(ViewType.CHEST);
        config.title(
                Component.text()
                        .content("CorvidAgriculture Menu")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.BOLD))
                        .build()
        );
        config.layout(
                "ABABABABA",
                "ABABABABA",
                "ABABABABA",
                "ABABABABA",
                "ABABABABA",
                "ABABABABA"
        );
        config.cancelOnClick();
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


        ItemStack temp = ItemHandler.itemIsHoe(render.getPlayer().getInventory().getItem(EquipmentSlot.HAND).getType())
                ?render.getPlayer().getInventory().getItem(EquipmentSlot.HAND)
                :ItemHandler.itemIsHoe(render.getPlayer().getInventory().getItem(EquipmentSlot.OFF_HAND).getType())
                ?render.getPlayer().getInventory().getItem(EquipmentSlot.OFF_HAND)
                :ItemStack.empty();
        render.slot(1,3, temp);

        temp = ItemHandler.playerHead(render.getPlayer().getPlayerProfile());
        temp.setData(DataComponentTypes.CUSTOM_NAME, Component.text()
                .content("Your Stats")
                .style(Style.style(TextColor.color(0x18516),TextDecoration.BOLD,TextDecoration.ITALIC.withState(false)))
                .build()
        );
        render.slot(1,7,temp);

        temp = new ItemStack(Material.CARROT);
        temp.setData(DataComponentTypes.ITEM_NAME,Component.text()
                .content("Crop Sell Shop")
                .style(Style.style(TextColor.color(0x2BA00), TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                .build()
        );
        render.slot(3,3,temp);

        temp = new ItemStack(Material.CHEST_MINECART);
        temp.setData(DataComponentTypes.ITEM_NAME,Component.text()
                .content("Catering")
                .style(Style.style(TextColor.color(0x7DD5), TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                .build()
        );
        render.slot(3,4,temp).onClick(this::openServerCateringMenu);

        temp = new ItemStack(Material.DIAMOND_HOE);
        temp.setData(DataComponentTypes.ITEM_NAME, Component.text()
                .content("Crop Compendium")
                .style(Style.style(TextColor.color(0xDCD200),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                .build()
        );
        render.slot(3,5,temp).onClick(context -> context.openForPlayer(CropCompendium.class));

        temp = new ItemStack(Material.POISONOUS_POTATO);
        temp.setData(DataComponentTypes.ITEM_NAME, Component.text()
                .content("Compost")
                .style(Style.style(TextColor.color(0x5A3B26),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                .build()
        );
        render.slot(3,6,temp);

        temp = new ItemStack(Material.VAULT);
        temp.setData(DataComponentTypes.ITEM_NAME, Component.text()
                .content("Crop Sack")
                .style(Style.style(TextColor.color(0xD6C528),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                .build()
        );
        render.slot(3,7,temp);

        temp = new ItemStack(Material.COPPER_GRATE);
        temp.setData(DataComponentTypes.ITEM_NAME, Component.text()
                .content("Juicer & Liquids")
                .style(Style.style(TextColor.color(0xDC0046),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                .build()
        );
        render.slot(4,3,temp);

        temp = new ItemStack(Material.CAULDRON);
        temp.setData(DataComponentTypes.ITEM_NAME, Component.text()
                .content("Cooking Pot & Meals")
                .style(Style.style(TextColor.color(0xBC823C), TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                .build()
        );
        render.slot(4,4,temp);

        temp = new ItemStack(Material.COMPOSTER);
        temp.setData(DataComponentTypes.ITEM_NAME, Component.text()
                .content("Seed & Fertilizer Shop")
                .style(Style.style(TextColor.color(0xA222),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                .build()
        );
        render.slot(4,5,temp);

        temp = new ItemStack(Material.FIREWORK_STAR);
        temp.setData(DataComponentTypes.FIREWORK_EXPLOSION, FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL)
                .withColor(Color.PURPLE)
                .build()
        );
        temp.setData(DataComponentTypes.ITEM_NAME, Component.text()
                .content("Zeals")
                .style(Style.style(TextColor.color(0x8000CA),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                .build()
        );
        temp.setData(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP);
        render.slot(4,6,temp);

        temp = new ItemStack(Material.PURPLE_GLAZED_TERRACOTTA);
        temp.setData(DataComponentTypes.ITEM_NAME, Component.text()
                .content("Hexes")
                .style(Style.style(TextColor.color(0x9B0018),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                .build()
        );
        render.slot(4,7,temp);

        temp = new ItemStack(Material.KNOWLEDGE_BOOK);
        temp.setData(DataComponentTypes.ITEM_NAME, Component.text()
                .content("Farmer's Guide")
                .style(Style.style(TextColor.color(0x78A00),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                .build()
        );
        render.slot(6,5, temp);
    }

    private void openServerCateringMenu(SlotClickContext slot){
        slot.closeForPlayer();
        CorvidAgriculture.getPlugin(CorvidAgriculture.class).getCateringMenu().open(ServerCatering.class,slot.getPlayer());
    }

    @Override
    public void onOpen(OpenContext open){

    }

    @Override
    public void onClick(SlotClickContext context){
        context.getPlayer();

    }

    public static ViewFrame initAgricultureMenu(){
        CorvidAgriculture plugin = CorvidAgriculture.getPlugin(CorvidAgriculture.class);
        ViewFrame viewFrame = ViewFrame.create(plugin).
                install(AnvilInputFeature.AnvilInput);
        viewFrame.with(new AgricultureMenu());
        viewFrame.with(new CropCompendium());
        viewFrame.with(new CropSell());
        viewFrame.register();
        return viewFrame;
    }
}
