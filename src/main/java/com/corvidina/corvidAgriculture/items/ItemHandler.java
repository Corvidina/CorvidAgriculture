package com.corvidina.corvidAgriculture.items;

import com.corvidina.corvidAgriculture.CorvidAgriculture;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemRarity;
import org.jetbrains.annotations.NotNull;

import javax.xml.crypto.Data;
import java.util.HashMap;

public class ItemHandler {
    //Lore line length -> 24 chars
    public static double sellMultiplier=1;
    public static final HashMap<CorvidAgricultureItems, Double> materialBasedSellMultiplier=new HashMap<CorvidAgricultureItems,Double>();

    //rotten fair good great exemplary divine
    public static double[] baseValues=new double[]{
            0.01, 20, 50, 100, 250, 1000
    };
    private static double[] valueChances = setValueChances(new double[]{
            5, 30, 25, 20, 15, 5
    });
    public static double[] setValueChances(double[] rc){
        if(rc.length!=6){
            throw new RuntimeException("rarity chances array failed to have 6 elements");
        }
        double tota = 0;
        for(double d : rc){
            tota+=d;
        }
        for (int i = 0; i < rc.length; i++) {
            rc[i]/=tota;
        }
        valueChances =rc;
        return valueChances;
    }
    public static double[] getValueChances(){
        return valueChances;
    }
    public static double getMaterialBasedSellMultiplier(CorvidAgricultureItems material){
        if(materialBasedSellMultiplier.containsKey(material))
            return materialBasedSellMultiplier.get(material);
        return 1;
    }
    private static Value randomValue(){
        double random = Math.random();
        random-=valueChances[0];
        if(random<0)
            return Value.ROTTEN;
        random-=valueChances[1];
        if(random<0)
            return Value.FAIR;
        random-=valueChances[2];
        if(random<0)
            return Value.GOOD;
        random-=valueChances[3];
        if(random<0)
            return Value.GREAT;
        random-=valueChances[4];
        if(random<0)
            return Value.EXEMPLARY;
        return Value.DIVINE;
    }
    private static int getValueAsIndex(Value value){
        return switch (value){
            case ROTTEN -> 0;
            case FAIR -> 1;
            case GOOD -> 2;
            case GREAT -> 3;
            case EXEMPLARY -> 4;
            case DIVINE -> 5;
        };
    }
    public static double sellValueOfStack(ItemStack itemStack){
        if(getValue(itemStack)==null)
            return getMaterialBasedSellMultiplier(getItemType(itemStack))*
                    sellMultiplier*
                    itemStack.getBukkitStack().getAmount();
        return getMaterialBasedSellMultiplier(getItemType(itemStack))*
                baseValues[getValueAsIndex(getValue(itemStack))]*
                sellMultiplier*
                itemStack.getBukkitStack().getAmount();
    }

    private static ItemStack buildCrowFeather(){
        //Create item
        org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        itemStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        itemStack.setData(DataComponentTypes.ITEM_MODEL, Material.FEATHER.getDefaultData(DataComponentTypes.ITEM_MODEL));
        itemStack.setData(DataComponentTypes.RARITY, ItemRarity.UNCOMMON);
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE,64);
        itemStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Crow Feather")
                        .style(Style.style(TextColor.color(0xB100FF), TextDecoration.BOLD))
                        .build()
        );

        //Add tag for referencing information & certifying type
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        CompoundTag root = new CompoundTag();
        CompoundTag customTag = new CompoundTag();
        //tag data
        customTag.putString("item_type", CorvidAgricultureItems.CROW_FEATHER.toString());
        //assign tags to root
        root.put("corvidagriculture",customTag);

        //assign tag to item
        nmsItem.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return nmsItem;
    }
    private static ItemStack buildInsectCarapace(){
        //Create item
        org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        itemStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE,64);
        itemStack.setData(DataComponentTypes.RARITY, ItemRarity.UNCOMMON);
        itemStack.setData(DataComponentTypes.ITEM_MODEL,Material.TURTLE_SCUTE.getDefaultData(DataComponentTypes.ITEM_MODEL));
        itemStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Insect Carapace")
                        .style(Style.style(TextColor.color(0xD5FF), TextDecoration.BOLD))
                        .build()
        );

        //Add tag for referencing information & certifying item
        ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        CompoundTag root = new CompoundTag();
        CompoundTag customTag = new CompoundTag();
        //tag data
        customTag.putString("item_type", CorvidAgricultureItems.INSECT_CARAPACE.toString());
        //apply tag to root
        root.put("corvidagriculture",customTag);

        //assign tag to item
        nmsStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
        return nmsStack;
    }
    private static ItemStack buildInsectLarvae(){
        //Create item
        org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        itemStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE,64);
        itemStack.setData(DataComponentTypes.RARITY, ItemRarity.UNCOMMON);
        itemStack.setData(DataComponentTypes.ITEM_MODEL,Material.PRISMARINE_CRYSTALS.getDefaultData(DataComponentTypes.ITEM_MODEL));
        itemStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Insect Larvae")
                        .style(Style.style(TextColor.color(0xFF005B), TextDecoration.BOLD))
                        .build()
        );

        //Add tag for referencing information & certifying item
        ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        CompoundTag root = new CompoundTag();
        CompoundTag customTag = new CompoundTag();
        //tag data
        customTag.putString("item_type",CorvidAgricultureItems.INSECT_LARVAE.toString());
        //apply tag to root
        root.put("corvidagriculture",customTag);

        //assign tag to item
        nmsStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
        return nmsStack;
    }

    private static ItemStack buildPricklyPear(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        bktStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        bktStack.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .nutrition(3)
                .saturation((float)1.5)
                .build()
        );
        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.GREEN_DYE.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE,64);
        bktStack.setData(DataComponentTypes.RARITY,ItemRarity.COMMON);
        bktStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Prickly Pear")
                        .style(Style.style(TextDecoration.ITALIC.withState(false)))
                        .build()
        );

        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(
                Component.text()
                        .content("Obtained by breaking a Cactus with a hoe.")
                        .style(Style.style(TextColor.color(0xAAAAAA),TextDecoration.ITALIC.withState(false)))
        ).addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(CorvidAgricultureItems.PRICKLY_PEAR))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type",CorvidAgricultureItems.PRICKLY_PEAR.name().toLowerCase());

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildCranberry(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        bktStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        bktStack.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .nutrition(3)
                .saturation((float)1.5)
                .build()
        );
        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.GREEN_DYE.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE,64);
        bktStack.setData(DataComponentTypes.RARITY,ItemRarity.COMMON);
        bktStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Cranberry")
                        .style(Style.style(TextDecoration.ITALIC.withState(false)))
                        .build()
        );

        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(
                Component.text()
                        .content("Obtained when harvesting a cranberry bush")
                        .style(Style.style(TextColor.color(0xAAAAAA),TextDecoration.ITALIC.withState(false)))
        ).addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(CorvidAgricultureItems.CRANBERRY))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type",CorvidAgricultureItems.CRANBERRY.name().toLowerCase());

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }

    private static String capitalizeFirst(String s){
        s=s.toLowerCase();
        return s.substring(0,1).toUpperCase()+s.substring(1);
    }

    private static ItemStack buildBeetroot(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.BEETROOT);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(CorvidAgricultureItems.BEETROOT))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type","beetroot");

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildCactus(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.CACTUS);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]
                                                *sellMultiplier
                                                *getMaterialBasedSellMultiplier(CorvidAgricultureItems.CACTUS))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type","cactus");

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildCarrot() {
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.CARROT);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(CorvidAgricultureItems.CARROT))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type","carrot");

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildCocoaBeans(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.COCOA_BEANS);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(CorvidAgricultureItems.COCOA_BEANS))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type","cocoa_beans");

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildMelonSlice(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.MELON_SLICE);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(CorvidAgricultureItems.MELON_SLICE))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type","melon_slice");

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildNetherWart(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.NETHER_WART);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(CorvidAgricultureItems.NETHER_WART))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type","nether_wart");

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildPotato(){
        Value value= randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.POTATO);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(CorvidAgricultureItems.POTATO))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type","potato");

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildPumpkin(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.PUMPKIN);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(CorvidAgricultureItems.PUMPKIN))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type","pumpkin");

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildSugarCane(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.SUGAR_CANE);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(CorvidAgricultureItems.SUGAR_CANE))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type","sugar_cane");

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildSweetBerries(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.SWEET_BERRIES);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(CorvidAgricultureItems.SWEET_BERRIES))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type","sweet_berries");

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildWheat(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.WHEAT);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9),TextDecoration.ITALIC.withState(false),TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(CorvidAgricultureItems.WHEAT))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type","wheat");

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }

    public static ItemStack buildItem(CorvidAgricultureItems item){
        // People tell me enhanced switches are hard to read, but these are
        // way better than compounding ternary operations or loads of ifs
        return switch (item) {
            case CROW_FEATHER -> buildCrowFeather();
            case INSECT_CARAPACE -> buildInsectCarapace();
            case INSECT_LARVAE -> buildInsectLarvae();
            case WHEAT -> buildWheat();
            case CARROT -> buildCarrot();
            case POTATO -> buildPotato();
            case PUMPKIN -> buildPumpkin();
            case BEETROOT -> buildBeetroot();
            case MELON_SLICE -> buildMelonSlice();
            case NETHER_WART -> buildNetherWart();
            case SWEET_BERRIES -> buildSweetBerries();
            case COCOA_BEANS -> buildCocoaBeans();
            case SUGAR_CANE -> buildSugarCane();
            case CACTUS -> buildCactus();
            case PRICKLY_PEAR -> buildPricklyPear();
            case CRANBERRY -> buildCranberry();
            case null, default -> null;
        };
    }

    private static Value getValue(ItemStack itemStack){
        CustomData data = itemStack.getComponents().get(DataComponents.CUSTOM_DATA);
        if(data==null)
            return null;
        CompoundTag tag = data.copyTag();

        tag = tag.getCompound("corvidagriculture");

        String str = tag.get("value")==null?null:tag.getString("value");
        try {
            return Value.valueOf(str.toUpperCase());
        } catch (Exception ignored){
            return null;
        }
    }
    public static CorvidAgricultureItems getItemType(ItemStack itemStack) {
        CustomData data = itemStack.getComponents().get(DataComponents.CUSTOM_DATA);
        if(data==null)
            return null;
        CompoundTag tag = data.copyTag();

        tag = tag.getCompound("corvidagriculture");

        String str = tag.get("item_type")==null?null:tag.getString("item_type");
        try {
            return CorvidAgricultureItems.valueOf(str);
        } catch (Exception ignored){
            return null;
        }

    }
    public static CorvidAgricultureItems getItemType(Material material){
        return switch(material){
            case BEETROOT -> CorvidAgricultureItems.BEETROOT;
            case CARROT -> CorvidAgricultureItems.CARROT;
            case POTATO -> CorvidAgricultureItems.POTATO;
            case WHEAT -> CorvidAgricultureItems.WHEAT;
            case MELON_SLICE -> CorvidAgricultureItems.MELON_SLICE;
            case PUMPKIN -> CorvidAgricultureItems.PUMPKIN;
            case NETHER_WART -> CorvidAgricultureItems.NETHER_WART;
            case SWEET_BERRIES -> CorvidAgricultureItems.SWEET_BERRIES;
            case COCOA_BEANS -> CorvidAgricultureItems.COCOA_BEANS;
            case SUGAR_CANE -> CorvidAgricultureItems.SUGAR_CANE;
            case CACTUS -> CorvidAgricultureItems.CACTUS;
            default -> null;
        };
    }
    public static void spawnItemStack(ItemStack itemStack, int x, int y, int z, Level level){
        ItemEntity item = new ItemEntity(level, x,y,z, itemStack);
        level.getWorld().addEntity(item, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }
}
enum Value {
    ROTTEN,
    FAIR,
    GOOD,
    GREAT,
    EXEMPLARY,
    DIVINE
}
