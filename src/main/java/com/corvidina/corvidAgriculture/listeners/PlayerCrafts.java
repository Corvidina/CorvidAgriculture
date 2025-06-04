package com.corvidina.corvidAgriculture.listeners;

import com.corvidina.corvidAgriculture.items.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerCrafts implements Listener {

    @EventHandler
    public void onBlockHit(BlockDamageEvent event){
        Material itemType = event.getItemInHand().getType();
        if (ItemHandler.itemIsHoe(itemType)) {
            switch (getCraftingStationType(event)) {
                case null : {
                    return;
                }
                case COMPOSTER : {
                    //run composter based crafting code

                }
                case CAULDRON_POT : {
                    //run cooking crafting code

                }
            }
        }
    }

    private CraftingStation getCraftingStationType(BlockDamageEvent event){
        Block block = event.getBlock();
        //check that the block is a composter
        if(block.getType()==Material.COMPOSTER) {
            return CraftingStation.COMPOSTER;
        //check if it is a cooking cauldron
        } else if (block.getType()==Material.CAULDRON ||
            block.getType()==Material.WATER_CAULDRON) {

            block=block.getRelative(0,-1,0);
            Bukkit.getLogger().info(block.getType().toString());

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
