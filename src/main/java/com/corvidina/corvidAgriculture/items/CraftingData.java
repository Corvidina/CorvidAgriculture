package com.corvidina.corvidAgriculture.items;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.C;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class CraftingData {
    private final HashMap<Material, Integer> vanillaMap;
    private final HashMap<AgricultureItem, Integer> ingredientMap;
    private boolean requiresWaterCauldron;

    public CraftingData(){
        vanillaMap=new HashMap<>();
        ingredientMap=new HashMap<>();
    }

    public static class Builder {
        private final CraftingData craftingData;
        private Builder() {
            craftingData = new CraftingData();
        }
        public static Builder init(){
            return new Builder();
        }
        public CraftingData build(){
            return craftingData;
        }
        public Builder addIngredient(Material material, int amount) {
            craftingData.addIngredient(material,amount);
            return this;
        }
        public Builder addIngredient(AgricultureItem ingredient, int amount) {
            craftingData.addIngredient(ingredient,amount);
            return this;
        }
        public Builder setRequiresWaterCauldron(boolean requiresWaterCauldron) {
            craftingData.setRequiresWaterCauldron(requiresWaterCauldron);
            return this;
        }
    }

    public void addIngredient(Material material, int amount) {
        if (material.isItem()) {
            vanillaMap.put(material, amount);
        } else {
            throw new IllegalArgumentException("Not an item");
        }
    }

    public void addIngredient(AgricultureItem ingredient, int amount) {
        ingredientMap.put(ingredient,amount);
    }

    public void setRequiresWaterCauldron(boolean requiresWaterCauldron) {
        this.requiresWaterCauldron = requiresWaterCauldron;
    }

    public HashMap<Object, Integer> getMap(){
        HashMap<Object,Integer> map = new HashMap<>();
        for (AgricultureItem item : ingredientMap.keySet()) {
            map.put(item, ingredientMap.get(item));
        }
        for (Material item : vanillaMap.keySet()){
            map.put(item, vanillaMap.get(item));
        }
        return map;
    }

    // returns null if ingredients aren't present. Returns the lowest value ingredient in the recipe otherwise
    public Value hasIngredients(Location loc){
        Collection<Entity> entities = loc.getWorld().getNearbyEntities(loc,0.45,0.45,0.45, entity -> entity.getType()==EntityType.ITEM);

        if(requiresWaterCauldron && loc.getBlock().getType()!=Material.WATER_CAULDRON)
            return null;

        HashMap<Object, Integer> map = getMap();
        HashMap<Object, Integer> needsToBeRemoved = new HashMap<>();

        // Checks that all items required are available before crafting
        for (Iterator<Entity> it = entities.iterator(); it.hasNext(); ) {
            Entity entity = it.next();
            if(entity instanceof Item item) {
                ItemStack itemStack = item.getItemStack();
                int count = itemStack.getAmount();
                AgricultureItem agricultureItem = ItemHandler.getItemType(itemStack);
                if (agricultureItem!=null) {
                    if (map.containsKey(agricultureItem)) {
                        if (map.get(agricultureItem)<=count) {
                            needsToBeRemoved.put(agricultureItem,map.remove(agricultureItem));
                        } else {
                            return null;
                        }
                    }
                } else {
                    Material itemType = itemStack.getType();
                    if (map.containsKey(itemType)) {
                        if (map.get(itemType)>=count) {
                            needsToBeRemoved.put(itemType,map.remove(itemType));
                        } else {
                            return null;
                        }
                    }
                }
                if (map.isEmpty()) {
                    break;
                }
            }
        }
        // Crafts the necessary item
        // Needs to be removed should contain only items that are a part of the recipe
        if(map.isEmpty()){
            Value lowestValue = Value.DIVINE;
            boolean hasChanged = false;
            for (Entity entity : entities) {
                if (entity instanceof Item item) {
                    ItemStack itemStack = item.getItemStack();
                    int count = itemStack.getAmount();
                    AgricultureItem agricultureItem = ItemHandler.getItemType(itemStack);
                    if(agricultureItem!=null) {
                        if (ItemHandler.getValue(itemStack) != null){
                            hasChanged=true;
                            if (lowestValue.compare(ItemHandler.getValue(itemStack)) > 0) {
                                lowestValue = ItemHandler.getValue(itemStack);
                            }
                        }
                        if (needsToBeRemoved.containsKey(agricultureItem)) {
                            itemStack.setAmount(count - needsToBeRemoved.remove(agricultureItem));
                            item.setItemStack(itemStack);
                        }
                    } else {
                        Material itemType = itemStack.getType();
                        if (needsToBeRemoved.containsKey(itemType)) {
                            if(itemType==Material.MILK_BUCKET) {
                                itemStack = new ItemStack(Material.BUCKET);
                            } else {
                                itemStack.setAmount(count - needsToBeRemoved.remove(itemType));
                            }
                            item.setItemStack(itemStack);
                        }
                    }
                }
            }
            if(!hasChanged)
                return Value.FAIR;
            return lowestValue; //drop item code outside this method
        }

        return null;
    }



}
