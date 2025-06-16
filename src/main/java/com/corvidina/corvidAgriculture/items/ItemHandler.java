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
import kotlin.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ResolvableProfile;
import org.bukkit.Bukkit;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ItemHandler {
    public static CorvidAgriculture plugin = CorvidAgriculture.getPlugin(CorvidAgriculture.class);
    public static double sellMultiplier = plugin.getSellMultiplier();
    public static final HashMap<Crop, Double> materialBasedSellMultiplier = plugin.getMaterialBasedSellMultiplier();
    public static final BlockFace[] headFaces = {
            BlockFace.EAST, BlockFace.EAST_NORTH_EAST, BlockFace.EAST_SOUTH_EAST,
            BlockFace.WEST, BlockFace.WEST_NORTH_WEST, BlockFace.WEST_SOUTH_WEST,
            BlockFace.NORTH, BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_NORTH_WEST, BlockFace.NORTH_WEST, BlockFace.NORTH_EAST,
            BlockFace.SOUTH, BlockFace.SOUTH_SOUTH_EAST, BlockFace.SOUTH_SOUTH_WEST, BlockFace.SOUTH_WEST, BlockFace.SOUTH_EAST
    };

    //rotten fair good great exemplary divine
    public static double[] baseValues = new double[]{
            0.01, 20, 50, 100, 250, 1000
    };
    private static double[] valueChances = setValueChances(new double[]{
            5, 30, 25, 20, 15, 5
    });

    public static double[] setValueChances(double @NotNull [] rc) {
        if (rc.length != 6) {
            throw new RuntimeException("rarity chances array failed to have 6 elements");
        }
        double tota = 0;
        for (double d : rc) {
            tota += d;
        }
        for (int i = 0; i < rc.length; i++) {
            rc[i] /= tota;
        }
        valueChances = rc;
        return valueChances;
    }

    public static double[] getValueChances() {
        return valueChances;
    }

    public static void setWeighingValue(Value value) {
        switch(value) {
            case Value.ROTTEN -> setValueChances(new double[] {1,0,0,0,0,0});
            case Value.FAIR -> setValueChances(new double[] {0,1,0,0,0,0});
            case Value.GOOD -> setValueChances(new double[] {0,0,1,0,0,0});
            case Value.GREAT -> setValueChances(new double[] {0,0,0,1,0,0});
            case Value.EXEMPLARY -> setValueChances(new double[] {0,0,0,0,1,0});
            case Value.DIVINE -> setValueChances(new double[] {0,0,0,0,0,1});
        }
    }

    public static double getMaterialBasedSellMultiplier(Crop material) {
        if (materialBasedSellMultiplier.containsKey(material))
            return materialBasedSellMultiplier.get(material);
        return 1;
    }

    public static Value randomValue() {
        double random = Math.random();
        random -= valueChances[0];
        if (random < 0)
            return Value.ROTTEN;
        random -= valueChances[1];
        if (random < 0)
            return Value.FAIR;
        random -= valueChances[2];
        if (random < 0)
            return Value.GOOD;
        random -= valueChances[3];
        if (random < 0)
            return Value.GREAT;
        random -= valueChances[4];
        if (random < 0)
            return Value.EXEMPLARY;
        return Value.DIVINE;
    }

    private static int getValueAsIndex(@NotNull Value value) {
        return switch (value) {
            case ROTTEN -> 0;
            case FAIR -> 1;
            case GOOD -> 2;
            case GREAT -> 3;
            case EXEMPLARY -> 4;
            case DIVINE -> 5;
        };
    }

    public static double sellValueOfStack(ItemStack itemStack) {
        if (getValue(itemStack) == null)
            return getMaterialBasedSellMultiplier(getCropType(itemStack)) *
                    sellMultiplier *
                    itemStack.getBukkitStack().getAmount();
        return getMaterialBasedSellMultiplier(getCropType(itemStack)) *
                baseValues[getValueAsIndex(getValue(itemStack))] *
                sellMultiplier *
                itemStack.getBukkitStack().getAmount();
    }

    public static boolean itemIsHoe(Material itemType) {
        return (itemType == Material.NETHERITE_HOE
                || itemType == Material.DIAMOND_HOE
                || itemType == Material.IRON_HOE
                || itemType == Material.GOLDEN_HOE
                || itemType == Material.STONE_HOE
                || itemType == Material.WOODEN_HOE);
    }

    private static ItemStack buildCrowFeather() {
        //Create item
        org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        itemStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        itemStack.setData(DataComponentTypes.ITEM_MODEL, Material.FEATHER.getDefaultData(DataComponentTypes.ITEM_MODEL));
        itemStack.setData(DataComponentTypes.RARITY, ItemRarity.UNCOMMON);
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
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
        root.put("corvidagriculture", customTag);

        //assign tag to item
        nmsItem.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return nmsItem;
    }
    private static ItemStack buildInsectCarapace() {
        //Create item
        org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        itemStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        itemStack.setData(DataComponentTypes.RARITY, ItemRarity.UNCOMMON);
        itemStack.setData(DataComponentTypes.ITEM_MODEL, Material.TURTLE_SCUTE.getDefaultData(DataComponentTypes.ITEM_MODEL));
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
        root.put("corvidagriculture", customTag);

        //assign tag to item
        nmsStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
        return nmsStack;
    }
    private static ItemStack buildInsectLarvae() {
        //Create item
        org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        itemStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        itemStack.setData(DataComponentTypes.RARITY, ItemRarity.UNCOMMON);
        itemStack.setData(DataComponentTypes.ITEM_MODEL, Material.PRISMARINE_CRYSTALS.getDefaultData(DataComponentTypes.ITEM_MODEL));
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
        root.put("corvidagriculture", customTag);

        //assign tag to item
        nmsStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
        return nmsStack;
    }

    private static ItemStack buildDough(){
        //Create item
        org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        itemStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        itemStack.setData(DataComponentTypes.RARITY, ItemRarity.COMMON);
        itemStack.setData(DataComponentTypes.ITEM_MODEL, Material.PLAYER_HEAD.getDefaultData(DataComponentTypes.ITEM_MODEL));
        itemStack.setData(DataComponentTypes.PROFILE, getProfile("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjA2YmUyZGYyMTIyMzQ0YmRhNDc5ZmVlY2UzNjVlZTBlOWQ1ZGEyNzZhZmEwZThjZThkODQ4ZjM3M2RkMTMxIn19fQ=="));
        itemStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Dough")
                        .style(Style.style(TextDecoration.ITALIC.withState(false)))
                        .build()
        );
        ItemLore lore = ItemLore.lore()
                .addLine(
                        Component.text("")
                ).addLine(
                        Component.text()
                                .content("Used in various crafting recipes")
                                .style(Style.style(TextColor.color(0xAAAAAA),TextDecoration.ITALIC.withState(false)))
                                .build()
                ).build();

        //Add tag for referencing information & certifying item
        ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        CompoundTag root = new CompoundTag();
        CompoundTag customTag = new CompoundTag();
        //tag data
        customTag.putString("item_type", HelperIngredient.DOUGH.toString().toLowerCase());
        //apply tag to root
        root.put("corvidagriculture", customTag);

        //assign tag to item
        nmsStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
        return nmsStack;
    }

    private static ItemStack buildBlueberryPie(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        bktStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        bktStack.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .nutrition(6)
                .saturation((float) 14.5)
                .build()
        );
        bktStack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds((float) 1.6)
                .hasConsumeParticles(false)
                .build());
        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.PLAYER_HEAD.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.PROFILE, getProfile("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTQ2ZjhlMTAwNjVmNmJmNjM2OWRhMTYwNzIyN2ZhM2EwZWNiNTllYmUzNDYzMjU5ZDNmN2Q4YjY3MjBlMTIyMSJ9fX0="));
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        bktStack.setData(DataComponentTypes.RARITY, ItemRarity.COMMON);
        bktStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Blueberry Pie")
                        .style(Style.style(TextDecoration.ITALIC.withState(false)))
                        .build()
        );

        ItemLore lore = ItemLore.lore().addLine(
                Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();

        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", Meal.BLUEBERRY_PIE.name().toLowerCase());

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildLemonCheesecake(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        bktStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        bktStack.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .nutrition(6)
                .saturation((float) 14.5)
                .build()
        );
        bktStack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds((float) 1.6)
                .hasConsumeParticles(false)
                .build());
        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.PLAYER_HEAD.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.PROFILE, getProfile("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTEwMmNiZmNmZWRiMmNlYjYyMDg2YTZhMGZhNjk3MzM1MDc3NmZmOWU5NWU0NDJmZjU2NzM5N2ZiNmFhNTg5YiJ9fX0="));
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        bktStack.setData(DataComponentTypes.RARITY, ItemRarity.COMMON);
        bktStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Lemon Cheesecake")
                        .style(Style.style(TextDecoration.ITALIC.withState(false)))
                        .build()
        );

        ItemLore lore = ItemLore.lore().addLine(
                Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();

        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", Meal.LEMON_CHEESECAKE.name().toLowerCase());

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildHamburger(){
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        bktStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        bktStack.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .nutrition(6)
                .saturation((float) 14.5)
                .build()
        );
        bktStack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds((float) 1.6)
                .hasConsumeParticles(false)
                .build());
        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.PLAYER_HEAD.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.PROFILE, getProfile("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTQxMWNiNTRhYWExZTNkYjU1NWM3ODVmNmI3NWJlMWJmOGU2OGIyOGU1Y2ZjNTljOWE4NzY4OTRmNjFjZGMxNyJ9fX0="));
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        bktStack.setData(DataComponentTypes.RARITY, ItemRarity.COMMON);
        bktStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Hamburger")
                        .style(Style.style(TextDecoration.ITALIC.withState(false)))
                        .build()
        );

        ItemLore lore = ItemLore.lore().addLine(
                Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();

        bktStack.setData(DataComponentTypes.LORE,lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", Meal.HAMBURGER.name().toLowerCase());

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }

    private static ItemStack buildLettuce() {
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        bktStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        bktStack.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .nutrition(3)
                .saturation((float) 1.5)
                .build()
        );
        bktStack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds((float) 1.6)
                .hasConsumeParticles(false)
                .build());
        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.PLAYER_HEAD.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.PROFILE, getProfile("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTk3OWZlN2IzMmZiN2ZjOTc0NmUxNTc0NzFmOTc1ZjMxYWZjYjU4ZmQ4YjVhY2I4YmY1M2VjMzUwZjRkNWMyIn19fQ=="));
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        bktStack.setData(DataComponentTypes.RARITY, ItemRarity.COMMON);
        bktStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Lettuce")
                        .style(Style.style(TextDecoration.ITALIC.withState(false)))
                        .build()
        );

        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(
                Component.text()
                        .content("Obtained when harvesting a lettuce plant")
                        .style(Style.style(TextColor.color(0xAAAAAA), TextDecoration.ITALIC.withState(false)))
        ).addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $" + baseValues[getValueAsIndex(value)] * sellMultiplier * getMaterialBasedSellMultiplier(Crop.LETTUCE))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", Crop.LETTUCE.name().toLowerCase());

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildLettuceSeeds() {
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.WHEAT_SEEDS);

        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.BEETROOT_SEEDS.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        bktStack.setData(DataComponentTypes.RARITY, ItemRarity.COMMON);
        bktStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Lettuce Seeds")
                        .style(Style.style(TextDecoration.ITALIC.withState(false)))
                        .build()
        );

        ItemLore lore = ItemLore.lore().addLine(
                Component.text("")
        ).addLine(
                Component.text()
                        .content("Grows a lettuce plant.")
                        .style(Style.style(TextColor.color(0xAAAAAA), TextDecoration.ITALIC.withState(false))
                        ).build()
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("item_type", Seeds.LETTUCE_SEEDS.name().toLowerCase());

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }

    private static ItemStack buildCranberry() {
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        bktStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        bktStack.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .nutrition(3)
                .saturation((float) 1.5)
                .build()
        );
        bktStack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds((float) 0.8)
                .hasConsumeParticles(false)
                .build());
        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.PLAYER_HEAD.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.PROFILE, getProfile("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWI0N2UwNjc3MzY3NzgwYjc2NTNkY2ViZjhjZjg4YmViNGRhYzk0Yzk4ZTY0NDYzNzVjYjVlYzhlOWEzOGRiNCJ9fX0="));
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        bktStack.setData(DataComponentTypes.RARITY, ItemRarity.COMMON);
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
                        .style(Style.style(TextColor.color(0xAAAAAA), TextDecoration.ITALIC.withState(false)))
        ).addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $" + baseValues[getValueAsIndex(value)] * sellMultiplier * getMaterialBasedSellMultiplier(Crop.CRANBERRY))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", Crop.CRANBERRY.name().toLowerCase());

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildCranberrySeeds() {
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.OAK_SAPLING);

        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.BEETROOT_SEEDS.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        bktStack.setData(DataComponentTypes.RARITY, ItemRarity.COMMON);
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
                        .style(Style.style(TextColor.color(0xAAAAAA), TextDecoration.ITALIC.withState(false))
                        ).build()
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("item_type", Seeds.CRANBERRY_SEEDS.name().toLowerCase());

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }

    private static ItemStack buildTomato() {
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        bktStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        bktStack.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .nutrition(3)
                .saturation((float) 1.5)
                .build()
        );
        bktStack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds((float) 0.8)
                .hasConsumeParticles(false)
                .build());
        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.PLAYER_HEAD.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.PROFILE, getProfile("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzJkZjRlNjc0OTUxYzEzOGU3MzExMTI3NTYxZmRiZDI3ZTIxNTA3MTZiMDJiYjU2ODc0N2Y4NTQ1ZmIyMDE0NSJ9fX0="));
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        bktStack.setData(DataComponentTypes.RARITY, ItemRarity.COMMON);
        bktStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Tomato")
                        .style(Style.style(TextDecoration.ITALIC.withState(false)))
                        .build()
        );

        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(
                Component.text()
                        .content("Obtained when harvesting a tomato bush")
                        .style(Style.style(TextColor.color(0xAAAAAA), TextDecoration.ITALIC.withState(false)))
        ).addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $" + baseValues[getValueAsIndex(value)] * sellMultiplier * getMaterialBasedSellMultiplier(Crop.TOMATO))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", Crop.TOMATO.name().toLowerCase());

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildTomatoSeeds() {
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.OAK_SAPLING);

        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.BEETROOT_SEEDS.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        bktStack.setData(DataComponentTypes.RARITY, ItemRarity.COMMON);
        bktStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Tomato Seeds")
                        .style(Style.style(TextDecoration.ITALIC.withState(false)))
                        .build()
        );

        ItemLore lore = ItemLore.lore().addLine(
                Component.text("")
        ).addLine(
                Component.text()
                        .content("Grows a tomato bush.")
                        .style(Style.style(TextColor.color(0xAAAAAA), TextDecoration.ITALIC.withState(false))
                        ).build()
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("item_type", Seeds.TOMATO_SEEDS.name().toLowerCase());

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }

    private static ItemStack buildBlueberry() {
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        bktStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        bktStack.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .nutrition(3)
                .saturation((float) 1.5)
                .build()
        );
        bktStack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds((float) 0.8)
                .hasConsumeParticles(false)
                .build());
        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.PLAYER_HEAD.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.PROFILE, getProfile("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDI2OTdmM2VmOGY0NjI5YjY0NWZkMmU2NDQ2NDEzMjRhMWMxMTgzNTQ5OGU2MzhmNzU3ZjI3OGFmYmNlNWRiMSJ9fX0="));
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        bktStack.setData(DataComponentTypes.RARITY, ItemRarity.COMMON);
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
                        .style(Style.style(TextColor.color(0xAAAAAA), TextDecoration.ITALIC.withState(false)))
        ).addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $" + baseValues[getValueAsIndex(value)] * sellMultiplier * getMaterialBasedSellMultiplier(Crop.BLUEBERRY))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", Crop.BLUEBERRY.name().toLowerCase());

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildBlueberrySeeds() {
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.OAK_SAPLING);

        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.MELON_SEEDS.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        bktStack.setData(DataComponentTypes.RARITY, ItemRarity.COMMON);
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
                        .style(Style.style(TextColor.color(0xAAAAAA), TextDecoration.ITALIC.withState(false))
                        ).build()
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("item_type", Seeds.BLUEBERRY_SEEDS.name().toLowerCase());

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }

    private static ItemStack buildLemon() {
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        bktStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        bktStack.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .nutrition(3)
                .saturation((float) 1.5)
                .build()
        );
        bktStack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds((float) 1.6)
                .hasConsumeParticles(false)
                .build());
        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.PLAYER_HEAD.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.PROFILE, getProfile("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM3OGI1ODJkMTljY2M1NWIwMjNlYjgyZWRhMjcxYmFjNDc0NGZhMjAwNmNmNWUxOTAyNDZlMmI0ZDVkIn19fQ=="));
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        bktStack.setData(DataComponentTypes.RARITY, ItemRarity.COMMON);
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
                        .style(Style.style(TextColor.color(0xAAAAAA), TextDecoration.ITALIC.withState(false)))
        ).addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $" + baseValues[getValueAsIndex(value)] * sellMultiplier * getMaterialBasedSellMultiplier(Crop.LEMON))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", Crop.LEMON.name().toLowerCase());

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildLemonTreeSapling() {
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.OAK_SAPLING);

        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.OAK_SAPLING.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        bktStack.setData(DataComponentTypes.RARITY, ItemRarity.COMMON);
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
                        .style(Style.style(TextColor.color(0xAAAAAA), TextDecoration.ITALIC.withState(false))
                        ).build()
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("item_type", Seeds.LEMON_TREE_SAPLING.name().toLowerCase());

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }

    private static ItemStack buildBanana() {
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        bktStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        bktStack.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .nutrition(3)
                .saturation((float) 1.5)
                .build()
        );
        bktStack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds((float) 1.6)
                .hasConsumeParticles(false)
                .build());
        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.PLAYER_HEAD.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.PROFILE, getProfile("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmYwZWE5YWNjOTdjOTg3YWI0NWQxNjZhYTdkZmU0ZGRiMjNhYjY1M2EwNjllYmJjYzQxZjYzY2Y0YTZlZjQ0MSJ9fX0="));
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        bktStack.setData(DataComponentTypes.RARITY, ItemRarity.COMMON);
        bktStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Banana")
                        .style(Style.style(TextDecoration.ITALIC.withState(false)))
                        .build()
        );

        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(
                Component.text()
                        .content("Obtained from harvesting bananas off of trees")
                        .style(Style.style(TextColor.color(0xAAAAAA), TextDecoration.ITALIC.withState(false)))
        ).addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $" + baseValues[getValueAsIndex(value)] * sellMultiplier * getMaterialBasedSellMultiplier(Crop.BANANA))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", Crop.BANANA.name().toLowerCase());

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildBananaTreeSapling() {
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.OAK_SAPLING);

        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.OAK_SAPLING.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        bktStack.setData(DataComponentTypes.RARITY, ItemRarity.COMMON);
        bktStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Banana Tree Sapling")
                        .style(Style.style(TextDecoration.ITALIC.withState(false)))
                        .build()
        );

        ItemLore lore = ItemLore.lore().addLine(
                Component.text("")
        ).addLine(
                Component.text()
                        .content("Grows a banana tree.")
                        .style(Style.style(TextColor.color(0xAAAAAA), TextDecoration.ITALIC.withState(false))
                        ).build()
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("item_type", Seeds.BANANA_TREE_SAPLING.name().toLowerCase());

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }

    private static ItemStack buildRice() {
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.KELP);
        bktStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        bktStack.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .nutrition(3)
                .saturation((float) 1.5)
                .build()
        );
        bktStack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds((float) 0.8)
                .hasConsumeParticles(false)
                .build());
        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.PLAYER_HEAD.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.PROFILE, getProfile("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWVmNjYxNDk5NmIxMjhlMjU2MzNmOGNmYjUxNWI5OTc3MzNlYmI0MmUzMmZiZmFkM2JjY2ZkMzk1YThiMjg5YSJ9fX0="));
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        bktStack.setData(DataComponentTypes.RARITY, ItemRarity.COMMON);
        bktStack.setData(DataComponentTypes.ITEM_NAME,
                Component.text()
                        .content("Rice")
                        .style(Style.style(TextDecoration.ITALIC.withState(false)))
                        .build()
        );

        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(
                Component.text()
                        .content("Obtained when harvesting a rice panicle")
                        .style(Style.style(TextColor.color(0xAAAAAA), TextDecoration.ITALIC.withState(false)))
        ).addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $" + baseValues[getValueAsIndex(value)] * sellMultiplier * getMaterialBasedSellMultiplier(Crop.RICE))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", Crop.RICE.name().toLowerCase());

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }

    private static ItemStack buildPricklyPear() {
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.MUSIC_DISC_5);
        bktStack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
        bktStack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .hasConsumeParticles(false)
                .build());
        bktStack.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .canAlwaysEat(false)
                .nutrition(3)
                .saturation((float) 3.6)
                .build()
        );
        bktStack.setData(DataComponentTypes.ITEM_MODEL,
                Material.PLAYER_HEAD.getDefaultData(DataComponentTypes.ITEM_MODEL)
        );
        bktStack.setData(DataComponentTypes.PROFILE, getProfile("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQzNWVlZGQ0MDg5NTU3NmJlMmJiNDNhOWRjNzllYjdhOWYwMGJmNDg4MjFjYzEwZjQ1N2JlMzMyOWMxZmFkZSJ9fX0="));
        bktStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
        bktStack.setData(DataComponentTypes.RARITY, ItemRarity.COMMON);
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
                        .style(Style.style(TextColor.color(0xAAAAAA), TextDecoration.ITALIC.withState(false)))
        ).addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $" + baseValues[getValueAsIndex(value)] * sellMultiplier * getMaterialBasedSellMultiplier(Crop.PRICKLY_PEAR))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);


        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", Crop.PRICKLY_PEAR.name().toLowerCase());

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }

    public static String capitalizeFirst(String s) {
        s = s.toLowerCase();
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private static ItemStack buildApple() {
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.APPLE);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $" + baseValues[getValueAsIndex(value)] * sellMultiplier * getMaterialBasedSellMultiplier(Crop.CARROT))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", "apple");

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildBeetroot() {
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.BEETROOT);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $" + baseValues[getValueAsIndex(value)] * sellMultiplier * getMaterialBasedSellMultiplier(Crop.BEETROOT))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", "beetroot");

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

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
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $" + baseValues[getValueAsIndex(value)] * sellMultiplier * getMaterialBasedSellMultiplier(Crop.CARROT))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", "carrot");

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildChorusFruit() {
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.CHORUS_FRUIT);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $" + baseValues[getValueAsIndex(value)] * sellMultiplier * getMaterialBasedSellMultiplier(Crop.CARROT))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", "chorus_fruit");

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildCocoaBeans() {
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.COCOA_BEANS);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $" + baseValues[getValueAsIndex(value)] * sellMultiplier * getMaterialBasedSellMultiplier(Crop.COCOA_BEANS))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", "cocoa_beans");

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildGlowBerries() {
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.GLOW_BERRIES);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $" + baseValues[getValueAsIndex(value)] * sellMultiplier * getMaterialBasedSellMultiplier(Crop.CARROT))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", "glow_berries");

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildMelonSlice() {
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.MELON_SLICE);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $" + baseValues[getValueAsIndex(value)] * sellMultiplier * getMaterialBasedSellMultiplier(Crop.MELON_SLICE))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", "melon_slice");

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildNetherWart() {
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.NETHER_WART);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $" + baseValues[getValueAsIndex(value)] * sellMultiplier * getMaterialBasedSellMultiplier(Crop.NETHER_WART))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", "nether_wart");

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildPotato() {
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.POTATO);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $" + baseValues[getValueAsIndex(value)] * sellMultiplier * getMaterialBasedSellMultiplier(Crop.POTATO))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", "potato");

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildSweetBerries() {
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.SWEET_BERRIES);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $" + baseValues[getValueAsIndex(value)] * sellMultiplier * getMaterialBasedSellMultiplier(Crop.SWEET_BERRIES))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", "sweet_berries");

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }
    private static ItemStack buildWheat() {
        Value value = randomValue();
        org.bukkit.inventory.ItemStack bktStack = new org.bukkit.inventory.ItemStack(Material.WHEAT);
        //Set item "lore" -- Sell value, show rating value etc
        ItemLore lore = ItemLore.lore().addLine(Component.text("")
        ).addLine(
                Component.text()
                        .content("Information:")
                        .style(Style.style(TextColor.color(0xCBB9), TextDecoration.ITALIC.withState(false), TextDecoration.BOLD))
                        .build()
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Evaluation: " + capitalizeFirst(value.toString()))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).addLine(
                Component.text()
                        .content(" ● ")
                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                        .build()
                        .append(
                                Component.text()
                                        .content("Sell Value: $" + baseValues[getValueAsIndex(value)] * sellMultiplier * getMaterialBasedSellMultiplier(Crop.WHEAT))
                                        .style(Style.style(TextColor.color(0x18516), TextDecoration.ITALIC.withState(false)))
                                        .build()
                        )
        ).build();
        bktStack.setData(DataComponentTypes.LORE, lore);

        ItemStack itemStack = CraftItemStack.asNMSCopy(bktStack);
        CompoundTag root = new CompoundTag();

        CompoundTag tag = new CompoundTag();
        tag.putString("value", value.toString().toLowerCase());
        tag.putString("item_type", "wheat");

        root.put("corvidagriculture", tag);

        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));

        return itemStack;
    }

    public static ItemStack buildItem(AgricultureItem item) {
        // People tell me enhanced switches are hard to read, but these are
        // way better than compounding ternary operations or loads of ifs
        return switch (item) {
            case Crop.WHEAT -> buildWheat();
            case Crop.CARROT -> buildCarrot();
            case Crop.POTATO -> buildPotato();
            case Crop.BEETROOT -> buildBeetroot();
            case Crop.MELON_SLICE -> buildMelonSlice();
            case Crop.NETHER_WART -> buildNetherWart();
            case Crop.SWEET_BERRIES -> buildSweetBerries();
            case Crop.COCOA_BEANS -> buildCocoaBeans();
            case Crop.APPLE -> buildApple();
            case Crop.GLOW_BERRIES -> buildGlowBerries();
            case Crop.CHORUS_FRUIT -> buildChorusFruit();
            case Crop.PRICKLY_PEAR -> buildPricklyPear();
            case Crop.CRANBERRY -> buildCranberry();
            case Crop.LEMON -> buildLemon();
            case Crop.BLUEBERRY -> buildBlueberry();
            case Crop.RICE, Seeds.RICE -> buildRice();
            case Crop.TOMATO -> buildTomato();
            case Seeds.LEMON_TREE_SAPLING -> buildLemonTreeSapling();
            case Seeds.CRANBERRY_SEEDS -> buildCranberrySeeds();
            case Seeds.BLUEBERRY_SEEDS -> buildBlueberrySeeds();
            case Seeds.TOMATO_SEEDS -> buildTomatoSeeds();
            case MobDrops.CROW_FEATHER -> buildCrowFeather();
            case MobDrops.INSECT_CARAPACE -> buildInsectCarapace();
            case MobDrops.INSECT_LARVAE -> buildInsectLarvae();
            case HelperIngredient.DOUGH -> buildDough();
            case Meal.BLUEBERRY_PIE -> buildBlueberryPie();
            case Meal.HAMBURGER -> buildHamburger();
            case Meal.LEMON_CHEESECAKE -> buildLemonCheesecake();
            case Seeds.BANANA_TREE_SAPLING -> buildBananaTreeSapling();
            case Crop.BANANA -> buildBanana();
            case Crop.LETTUCE -> buildLettuce();
            case Seeds.LETTUCE_SEEDS -> buildLettuceSeeds();
            default -> throw new IllegalArgumentException("Either needs implemented or cannot be built");
        };
    }
    public static org.bukkit.inventory.ItemStack buildItemBkt(AgricultureItem item) {
        return CraftItemStack.asBukkitCopy(buildItem(item));
    }

    public static void buildFruitBlock(Block block, Crop item) {
        ItemStack fruitBlock = ItemHandler.buildItem(item);

        plugin.getBerryLikeMap().put(block.getLocation(), new BerryLike(block.getLocation(), item));

        if (block.getType() == Material.PLAYER_HEAD) {
            if (block.getBlockData() instanceof Rotatable rotatable) {
                rotatable.setRotation(headFaces[(int) (Math.random() * headFaces.length)]);
                block.setBlockData(rotatable);
            }
        }

        BlockState state = block.getState();
        if (!(state instanceof Skull skull)) return;

        ResolvableProfile profile = fruitBlock.get(DataComponents.PROFILE);

        if (profile == null)
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

    public static void buildFruitBlock(Block block, Crop item, BlockFace face) {
        ItemStack pear = ItemHandler.buildItem(item);

        plugin.getBerryLikeMap().put(block.getLocation(), new BerryLike(block.getLocation(), item));

        if (block.getType() == Material.PLAYER_WALL_HEAD) {
            if (block.getBlockData() instanceof Directional direction) {
                direction.setFacing(face);
                block.setBlockData(direction);
            }
        }

        BlockState state = block.getState();
        if (!(state instanceof Skull skull)) return;

        ResolvableProfile profile = pear.get(DataComponents.PROFILE);

        if (profile == null)
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

    public static ItemStack addZealToHoe(ItemStack stack, Zeal zeal) {
        CompoundTag tag;
        try {
            tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
        } catch (Exception e) {
            tag = new CompoundTag();
        }
        CompoundTag agriculture = tag.getCompound("corvidagriculture");
        CompoundTag zeals = agriculture.getCompound("zeals");

        int i = zeals.getInt(zeal.toString().toLowerCase());
        zeals.putInt(zeal.toString().toLowerCase(), i+1);
        Bukkit.getLogger().info(""+i);

        agriculture.put("zeals",zeals);
        tag.put("corvidagriculture",agriculture);
        stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));
        return stack;
    }

    public static org.bukkit.inventory.ItemStack addZealToHoe(org.bukkit.inventory.ItemStack stack, Zeal zeal) {
        return CraftItemStack.asBukkitCopy(addZealToHoe(CraftItemStack.asNMSCopy(stack),zeal));
    }

    public static HashMap<Zeal, Integer> getZeals(ItemStack stack) {
        CompoundTag tag;
        try {
            tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
        } catch (Exception e) {
            throw new IllegalArgumentException("Stack failed to have custom data");
        }
        CompoundTag agriculture = tag.getCompound("corvidagriculture");
        CompoundTag zeals = agriculture.getCompound("zeals");

        HashMap<Zeal, Integer> data = new HashMap<>();
        for (String key : zeals.getAllKeys()) {
            data.put(Zeal.valueOf(key.toUpperCase()),zeals.getInt(key));
        }
        return data;
    }
    public static HashMap<Zeal, Integer> getZeals(org.bukkit.inventory.ItemStack stack) {
        return getZeals(CraftItemStack.asNMSCopy(stack));
    }

    public static int getZeal(ItemStack stack, Zeal zeal){
        CompoundTag tag;
        try {
            tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
        } catch (Exception e) {
            throw new IllegalArgumentException("Stack failed to have custom data");
        }

        CompoundTag agriculture = tag.getCompound("corvidagriculture");
        CompoundTag zeals = agriculture.getCompound("zeals");
        return zeals.getInt(zeal.toString().toLowerCase());
    }
    public static int getZeal(org.bukkit.inventory.ItemStack stack, Zeal zeal) {
        return getZeal(CraftItemStack.asNMSCopy(stack), zeal);
    }

    public static Value getValue(ItemStack itemStack) {
        CustomData data = itemStack.getComponents().get(DataComponents.CUSTOM_DATA);
        if (data == null)
            return null;
        CompoundTag tag = data.copyTag();

        tag = tag.getCompound("corvidagriculture");

        String str = tag.get("value") == null ? null : tag.getString("value");
        try {
            return Value.valueOf(str.toUpperCase());
        } catch (Exception ignored) {
            return null;
        }
    }
    public static Value getValue(org.bukkit.inventory.ItemStack itemStack) {
        return getValue(CraftItemStack.asNMSCopy(itemStack));
    }

    public static Crop getCropType(ItemStack itemStack) {
        CustomData data = itemStack.getComponents().get(DataComponents.CUSTOM_DATA);
        if (data == null)
            return null;
        CompoundTag tag = data.copyTag();

        tag = tag.getCompound("corvidagriculture");

        String str = tag.get("item_type") == null ? null : tag.getString("item_type");
        try {
            return Crop.valueOf(str.toUpperCase());
        } catch (Exception ignored) {
            return null;
        }

    }
    public static Crop getCropType(org.bukkit.inventory.ItemStack itemStack) {
        return getCropType(CraftItemStack.asNMSCopy(itemStack));
    }

    public static Seeds getSeedType(ItemStack itemStack) {
        CustomData data = itemStack.getComponents().get(DataComponents.CUSTOM_DATA);
        if (data == null)
            return null;
        CompoundTag tag = data.copyTag();

        tag = tag.getCompound("corvidagriculture");

        String str = tag.get("item_type") == null ? null : tag.getString("item_type");
        try {
            return Seeds.valueOf(str.toUpperCase());
        } catch (Exception ignored) {
            return null;
        }
    }
    public static Seeds getSeedType(org.bukkit.inventory.ItemStack itemStack) {
        return getSeedType(CraftItemStack.asNMSCopy(itemStack));
    }

    public static Meal getMealType(ItemStack itemStack) {
        CustomData data = itemStack.getComponents().get(DataComponents.CUSTOM_DATA);
        if (data == null)
            return null;
        CompoundTag tag = data.copyTag();

        tag = tag.getCompound("corvidagriculture");

        String str = tag.get("item_type") == null ? null : tag.getString("item_type");
        try {
            return Meal.valueOf(str.toUpperCase());
        } catch (Exception ignored) {
            return null;
        }
    }
    public static Meal getMealType(org.bukkit.inventory.ItemStack itemStack) {
        return getMealType(CraftItemStack.asNMSCopy(itemStack));
    }

    public static HelperIngredient getIngredientType(ItemStack itemStack) {
        CustomData data = itemStack.getComponents().get(DataComponents.CUSTOM_DATA);
        if (data == null)
            return null;
        CompoundTag tag = data.copyTag();

        tag = tag.getCompound("corvidagriculture");

        String str = tag.get("item_type") == null ? null : tag.getString("item_type");
        try {
            return HelperIngredient.valueOf(str.toUpperCase());
        } catch (Exception ignored) {
            return null;
        }
    }
    public static HelperIngredient getIngredientType(org.bukkit.inventory.ItemStack itemStack) {
        return getIngredientType(CraftItemStack.asNMSCopy(itemStack));
    }

    public static AgricultureItem getItemType(ItemStack itemStack) {
        if (getCropType(itemStack) != null)
            return getCropType(itemStack);
        if (getSeedType(itemStack) != null)
            return getSeedType(itemStack);
        if (getMealType(itemStack) != null)
            return getMealType(itemStack);
        if (getIngredientType(itemStack) != null)
            return getIngredientType(itemStack);
        return null;
    }
    public static AgricultureItem getItemType(org.bukkit.inventory.ItemStack itemStack) {
        return getItemType(CraftItemStack.asNMSCopy(itemStack));
    }

    public static io.papermc.paper.datacomponent.item.ResolvableProfile getProfile(String base64) {
        UUID name = UUID.nameUUIDFromBytes(base64.getBytes(StandardCharsets.UTF_8));
        GameProfile profile = new GameProfile(name, name.toString());
        profile.getProperties().put("textures", new Property("textures", base64));

        return io.papermc.paper.datacomponent.item.ResolvableProfile.resolvableProfile()
                .uuid(name)
                .name(null)
                .addProperty(new ProfileProperty("textures", base64))
                .build();
    }

    public static org.bukkit.inventory.ItemStack playerHead(PlayerProfile profile) {
        org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD);
        itemStack.setData(DataComponentTypes.PROFILE, io.papermc.paper.datacomponent.item.ResolvableProfile.resolvableProfile(profile));
        return itemStack;
    }

    public static org.bukkit.inventory.ItemStack playerHead(io.papermc.paper.datacomponent.item.ResolvableProfile profile) {
        org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD);
        itemStack.setData(DataComponentTypes.PROFILE, profile);
        return itemStack;
    }
    public static org.bukkit.inventory.ItemStack playerHead(String playerName) {
        return switch(playerName) {
            case "Nico" -> playerHead(getProfile("ewogICJ0aW1lc3RhbXAiIDogMTc0OTA0NTQzNDc4OCwKICAicHJvZmlsZUlkIiA6ICI0YmQ5MjA0NDVkOGI0YjE0OWFhZGY4ZWNhYTllOGNmOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJ4Tmljb2NvX3gzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2UxYjlhMTYxYzU4N2JiMWE4YzAxZTMyMTQzYjdmODAzNDYyMGNkYzFmODljNTdkNWZkOTE3NGEzNGZhZjM3YWQiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="));
            default -> throw new IllegalArgumentException("No implementation for name");
        };
    }
}