package com.corvidina.corvidAgriculture.items;

import com.corvidina.corvidAgriculture.BerryLike;
import com.corvidina.corvidAgriculture.CorvidAgriculture;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ResolvableProfile;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemRarity;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

public class ItemHandler {
    public static CorvidAgriculture plugin = CorvidAgriculture.getPlugin(CorvidAgriculture.class);
    public static double sellMultiplier=plugin.getSellMultiplier();
    public static final HashMap<Crops, Double> materialBasedSellMultiplier=plugin.getMaterialBasedSellMultiplier();
    public static final BlockFace[] headFaces = {
            BlockFace.EAST,BlockFace.EAST_NORTH_EAST,BlockFace.EAST_SOUTH_EAST,
            BlockFace.WEST,BlockFace.WEST_NORTH_WEST,BlockFace.WEST_SOUTH_WEST,
            BlockFace.NORTH, BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_NORTH_WEST, BlockFace.NORTH_WEST, BlockFace.NORTH_EAST,
            BlockFace.SOUTH, BlockFace.SOUTH_SOUTH_EAST, BlockFace.SOUTH_SOUTH_WEST, BlockFace.SOUTH_WEST, BlockFace.SOUTH_EAST
    };

    //rotten fair good great exemplary divine
    public static double[] baseValues=new double[]{
            0.01, 20, 50, 100, 250, 1000
    };
    private static double[] valueChances = setValueChances(new double[]{
            5, 30, 25, 20, 15, 5
    });
    public static double[] setValueChances(double @NotNull [] rc){
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
        valueChances=rc;
        return valueChances;
    }
    public static double[] getValueChances(){
        return valueChances;
    }
    public static double getMaterialBasedSellMultiplier(Crops material){
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
    private static int getValueAsIndex(@NotNull Value value){
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
            return getMaterialBasedSellMultiplier(getCropType(itemStack))*
                    sellMultiplier*
                    itemStack.getBukkitStack().getAmount();
        return getMaterialBasedSellMultiplier(getCropType(itemStack))*
                baseValues[getValueAsIndex(getValue(itemStack))]*
                sellMultiplier*
                itemStack.getBukkitStack().getAmount();
    }
    public static boolean itemIsHoe(Material itemType){
        return (itemType==Material.NETHERITE_HOE
                || itemType==Material.DIAMOND_HOE
                || itemType==Material.IRON_HOE
                || itemType==Material.GOLDEN_HOE
                || itemType==Material.STONE_HOE
                || itemType==Material.WOODEN_HOE);
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
        customTag.putString("item_type", MobDrops.CROW_FEATHER.toString().toLowerCase());
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
        customTag.putString("item_type", MobDrops.INSECT_CARAPACE.toString().toLowerCase());
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
        customTag.putString("item_type", MobDrops.INSECT_LARVAE.toString().toLowerCase());
        //apply tag to root
        root.put("corvidagriculture",customTag);

        //assign tag to item
        nmsStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
        return nmsStack;
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
        bktStack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds((float)0.8)
                .hasConsumeParticles(false)
                .build());
        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.PLAYER_HEAD.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.PROFILE,getProfile("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWI0N2UwNjc3MzY3NzgwYjc2NTNkY2ViZjhjZjg4YmViNGRhYzk0Yzk4ZTY0NDYzNzVjYjVlYzhlOWEzOGRiNCJ9fX0="));
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
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(Crops.CRANBERRY))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type", Crops.CRANBERRY.name().toLowerCase());

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildCranberrySeeds(){
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.OAK_SAPLING);

        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.BEETROOT_SEEDS.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE,64);
        bktStack.setData(DataComponentTypes.RARITY,ItemRarity.COMMON);
        bktStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Cranberry Seeds")
                        .style(Style.style(TextDecoration.ITALIC.withState(false)))
                        .build()
        );

        ItemLore lore = ItemLore.lore().addLine(
                Component.text("")
        ).addLine(
                Component.text()
                        .content("Grows a cranberry bush.")
                        .style(Style.style(TextColor.color(0xAAAAAA),TextDecoration.ITALIC.withState(false))
                        ).build()
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("item_type", Seeds.CRANBERRY_SEEDS.name().toLowerCase());

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }

    private static ItemStack buildBlueberry(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        bktStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        bktStack.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .nutrition(3)
                .saturation((float)1.5)
                .build()
        );
        bktStack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds((float)0.8)
                .hasConsumeParticles(false)
                .build());
        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.PLAYER_HEAD.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.PROFILE,getProfile("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDI2OTdmM2VmOGY0NjI5YjY0NWZkMmU2NDQ2NDEzMjRhMWMxMTgzNTQ5OGU2MzhmNzU3ZjI3OGFmYmNlNWRiMSJ9fX0="));
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE,64);
        bktStack.setData(DataComponentTypes.RARITY,ItemRarity.COMMON);
        bktStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Blueberry")
                        .style(Style.style(TextDecoration.ITALIC.withState(false)))
                        .build()
        );

        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(
                Component.text()
                        .content("Obtained when harvesting a blueberry bush")
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
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(Crops.BLUEBERRY))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type", Crops.BLUEBERRY.name().toLowerCase());

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildBlueberrySeeds(){
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.OAK_SAPLING);

        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.BEETROOT_SEEDS.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE,64);
        bktStack.setData(DataComponentTypes.RARITY,ItemRarity.COMMON);
        bktStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Blueberry Seeds")
                        .style(Style.style(TextDecoration.ITALIC.withState(false)))
                        .build()
        );

        ItemLore lore = ItemLore.lore().addLine(
                Component.text("")
        ).addLine(
                Component.text()
                        .content("Grows a blueberry bush.")
                        .style(Style.style(TextColor.color(0xAAAAAA),TextDecoration.ITALIC.withState(false))
                        ).build()
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("item_type", Seeds.BLUEBERRY_SEEDS.name().toLowerCase());

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }

    private static ItemStack buildLemon(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        bktStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        bktStack.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .nutrition(3)
                .saturation((float)1.5)
                .build()
        );
        bktStack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds((float)1.6)
                .hasConsumeParticles(false)
                .build());
        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.PLAYER_HEAD.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.PROFILE,getProfile("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM3OGI1ODJkMTljY2M1NWIwMjNlYjgyZWRhMjcxYmFjNDc0NGZhMjAwNmNmNWUxOTAyNDZlMmI0ZDVkIn19fQ=="));
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE,64);
        bktStack.setData(DataComponentTypes.RARITY,ItemRarity.COMMON);
        bktStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Lemon")
                        .style(Style.style(TextDecoration.ITALIC.withState(false)))
                        .build()
        );

        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(
                Component.text()
                        .content("Obtained from harvesting lemons off of trees")
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
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(Crops.LEMON))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type", Crops.LEMON.name().toLowerCase());

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildLemonTreeSapling(){
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.OAK_SAPLING);

        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.OAK_SAPLING.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE,64);
        bktStack.setData(DataComponentTypes.RARITY,ItemRarity.COMMON);
        bktStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Lemon Tree Sapling")
                        .style(Style.style(TextDecoration.ITALIC.withState(false)))
                        .build()
        );

        ItemLore lore = ItemLore.lore().addLine(
                Component.text("")
        ).addLine(
                Component.text()
                        .content("Grows a lemon tree.")
                        .style(Style.style(TextColor.color(0xAAAAAA),TextDecoration.ITALIC.withState(false))
                ).build()
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("item_type", Seeds.LEMON_TREE_SAPLING.name().toLowerCase());

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }

    private static ItemStack buildPricklyPear(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        bktStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        bktStack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .hasConsumeParticles(false)
                .build());
        bktStack.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .canAlwaysEat(false)
                .nutrition(3)
                .saturation((float)3.6)
                .build()
        );
        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.PLAYER_HEAD.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.PROFILE,getProfile("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQzNWVlZGQ0MDg5NTU3NmJlMmJiNDNhOWRjNzllYjdhOWYwMGJmNDg4MjFjYzEwZjQ1N2JlMzMyOWMxZmFkZSJ9fX0="));
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
                        .content("Grows by chance off of a two tall cactus")
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
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(Crops.PRICKLY_PEAR))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);



        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type", Crops.PRICKLY_PEAR.name().toLowerCase());

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
    }

    private static String capitalizeFirst(String s){
        s=s.toLowerCase();
        return s.substring(0,1).toUpperCase()+s.substring(1);
    }

    private static ItemStack buildApple(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.APPLE);
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
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(Crops.CARROT))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type","apple");

        root.put("corvidagriculture",tag);

        itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(root));

        return itemStack;
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
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(Crops.BEETROOT))
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
                                                *getMaterialBasedSellMultiplier(Crops.CACTUS))
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
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(Crops.CARROT))
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
    private static ItemStack buildChorusFruit(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.CHORUS_FRUIT);
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
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(Crops.CARROT))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type","chorus_fruit");

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
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(Crops.COCOA_BEANS))
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
    private static ItemStack buildGlowBerries(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.GLOW_BERRIES);
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
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(Crops.CARROT))
                                        .style(Style.style(TextColor.color(0x18516),TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value",value.toString().toLowerCase());
        tag.putString("item_type","glow_berries");

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
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(Crops.MELON_SLICE))
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
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(Crops.NETHER_WART))
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
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(Crops.POTATO))
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
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(Crops.PUMPKIN))
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
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(Crops.SUGAR_CANE))
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
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(Crops.SWEET_BERRIES))
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
                                        .content("Sell Value: $"+baseValues[getValueAsIndex(value)]* sellMultiplier *getMaterialBasedSellMultiplier(Crops.WHEAT))
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

    public static ItemStack buildItem(Crops item){
        // People tell me enhanced switches are hard to read, but these are
        // way better than compounding ternary operations or loads of ifs
        return switch (item) {
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
            case APPLE -> buildApple();
            case GLOW_BERRIES -> buildGlowBerries();
            case CHORUS_FRUIT -> buildChorusFruit();
            case PRICKLY_PEAR -> buildPricklyPear();
            case CRANBERRY -> buildCranberry();
            case LEMON -> buildLemon();
            case BLUEBERRY -> buildBlueberry();
        };
    }
    public static ItemStack buildItem(Seeds item) {
        return switch (item) {
            case LEMON_TREE_SAPLING -> buildLemonTreeSapling();
            case CRANBERRY_SEEDS -> buildCranberrySeeds();
            case BLUEBERRY_SEEDS -> buildBlueberrySeeds();
        };
    }
    public static ItemStack buildItem(MobDrops item){
        return switch (item){
            case CROW_FEATHER -> buildCrowFeather();
            case INSECT_CARAPACE -> buildInsectCarapace();
            case INSECT_LARVAE -> buildInsectLarvae();
            default -> throw new IllegalArgumentException("Non valid MobDrop item");
        };
    }

    public static org.bukkit.inventory.ItemStack buildItemBkt(Crops item){
        return CraftItemStack.asBukkitCopy(buildItem(item));
    }
    public static org.bukkit.inventory.ItemStack buildItemBkt(Seeds item){
        return CraftItemStack.asBukkitCopy(buildItem(item));
    }

    public static void buildFruitBlock(Block block, Crops item) {
        ItemStack fruitBlock = ItemHandler.buildItem(item);

        plugin.getBerryLikeMap().put(block.getLocation(), new BerryLike(block.getLocation(), item));

        if (block.getType() == Material.PLAYER_HEAD){
            if (block.getBlockData() instanceof Rotatable rotatable) {
                rotatable.setRotation(headFaces[(int) (Math.random() * headFaces.length)]);
                block.setBlockData(rotatable);
            }
        }

        BlockState state = block.getState();
        if (!(state instanceof Skull skull)) return;

        ResolvableProfile profile = fruitBlock.get(DataComponents.PROFILE);

        if(profile==null)
            throw new IllegalArgumentException("Illegal Crop Type");
        try {
            Field profileField = skull.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skull, profile);
            profileField.setAccessible(false);
            skull.update(true, false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    public static void buildFruitBlock(Block block, Crops item, BlockFace face){
        ItemStack pear = ItemHandler.buildItem(item);

        plugin.getBerryLikeMap().put(block.getLocation(), new BerryLike(block.getLocation(), item));

        if (block.getType() == Material.PLAYER_WALL_HEAD){
            if (block.getBlockData() instanceof Directional direction) {
                direction.setFacing(face);
                block.setBlockData(direction);
            }
        }

        BlockState state = block.getState();
        if (!(state instanceof Skull skull)) return;

        ResolvableProfile profile = pear.get(DataComponents.PROFILE);

        if(profile==null)
            throw new IllegalArgumentException("Illegal Crop Type");

        try {
            Field profileField = skull.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skull, profile);
            profileField.setAccessible(false);
            skull.update(true, false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
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
    public static Crops getCropType(ItemStack itemStack) {
        CustomData data = itemStack.getComponents().get(DataComponents.CUSTOM_DATA);
        if(data==null)
            return null;
        CompoundTag tag = data.copyTag();

        tag = tag.getCompound("corvidagriculture");

        String str = tag.get("item_type")==null?null:tag.getString("item_type");
        try {
            return Crops.valueOf(str.toUpperCase());
        } catch (Exception ignored){
            return null;
        }

    }
    public static Crops getCropType(org.bukkit.inventory.ItemStack itemStack){
        return getCropType(CraftItemStack.asNMSCopy(itemStack));
    }

    public static Seeds getSeedType(ItemStack itemStack) {
        CustomData data = itemStack.getComponents().get(DataComponents.CUSTOM_DATA);
        if(data==null)
            return null;
        CompoundTag tag = data.copyTag();

        tag = tag.getCompound("corvidagriculture");

        String str = tag.get("item_type")==null?null:tag.getString("item_type");
        try {
            return Seeds.valueOf(str.toUpperCase());
        } catch (Exception ignored){
            return null;
        }
    }
    public static Seeds getSeedType(org.bukkit.inventory.ItemStack itemStack) {
        return getSeedType(CraftItemStack.asNMSCopy(itemStack));
    }
    public static io.papermc.paper.datacomponent.item.ResolvableProfile getProfile(String base64){
        UUID name = UUID.nameUUIDFromBytes(base64.getBytes(StandardCharsets.UTF_8));
        GameProfile profile = new GameProfile(name,name.toString());
        profile.getProperties().put("textures",new Property("textures",base64));

        return io.papermc.paper.datacomponent.item.ResolvableProfile.resolvableProfile()
                .uuid(name)
                .name(null)
                .addProperty(new ProfileProperty("textures",base64))
                .build();
    }
    public static org.bukkit.inventory.ItemStack playerHead(PlayerProfile profile){
        org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD);
        itemStack.setData(DataComponentTypes.PROFILE, io.papermc.paper.datacomponent.item.ResolvableProfile.resolvableProfile(profile));
        return  itemStack;
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

