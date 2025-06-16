package com.corvidina.corvidAgriculture.listeners;

import com.corvidina.corvidAgriculture.items.HelperIngredient;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import com.corvidina.corvidAgriculture.items.Meal;
import com.corvidina.corvidAgriculture.items.Value;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerCrafts implements Listener {

    @EventHandler
    public void onBlockHit(PlayerInteractEvent event){
        if(event.getItem()==null||!ItemHandler.itemIsHoe(event.getItem().getType())||(event.getAction()!=Action.LEFT_CLICK_BLOCK&&event.getAction()!=Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Material itemType = event.getItem().getType();
        if (ItemHandler.itemIsHoe(itemType)) {
            switch (getCraftingStationType(event.getClickedBlock())) {
                case null : {
                    return;
                }
                case COMPOSTER : {
                    //run composter based crafting code

                }
                case CAULDRON_POT : {
                    //run cooking crafting code
                    Meal[] meals = Meal.values();

                    for(Meal meal : meals) {
                        Location loc = event.getClickedBlock().getLocation().add(0.5,0.5,0.5);
                        Value value = meal.getCraftingData().hasIngredients(loc);
                        if (value!=null){
                            //build and drop this meal
                            double[] doubles = ItemHandler.getValueChances();
                            ItemHandler.setWeighingValue(value);
                            loc.getWorld().dropItemNaturally(loc, ItemHandler.buildItemBkt(meal));
                            ItemHandler.setValueChances(doubles);
                            return;
                        }
                    }

                    HelperIngredient[] ingredients = HelperIngredient.values();

                    for(HelperIngredient ingredient : ingredients) {
                        Location loc = event.getClickedBlock().getLocation().add(0.5,0.5,0.5);
                        Value value = ingredient.getCraftingData().hasIngredients(loc);
                        if (value!=null) {
                            loc.getWorld().dropItemNaturally(loc, ItemHandler.buildItemBkt(ingredient));
                            return;
                        }
                    }

                }
            }
        }
    }

    private CraftingStation getCraftingStationType(Block block){
        //check that the block is a composter
        if(block.getType()==Material.COMPOSTER) {
            return CraftingStation.COMPOSTER;
        //check if it is a cooking cauldron
        } else if (block.getType()==Material.CAULDRON ||
            block.getType()==Material.WATER_CAULDRON) {

            block=block.getRelative(0,-1,0);
            //Bukkit.getLogger().info(block.getType().toString());

            //check that the block below is a fire type
            if(block.getType()==Material.CAMPFIRE ||
                block.getType()==Material.SOUL_CAMPFIRE ||
                block.getType()==Material.FIRE ||
                block.getType()==Material.SOUL_FIRE) {

                return CraftingStation.CAULDRON_POT;
            }
        }
        return null;
    }
}
enum CraftingStation {
    CAULDRON_POT,
    COMPOSTER
}
