package com.corvidina.corvidAgriculture.listeners;

import com.corvidina.corvidAgriculture.items.CorvidAgricultureItems;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerBreaksBlock implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        switch (event.getBlock().getType()){
            case BEETROOTS,WHEAT,CARROTS,POTATOES,MELON,PUMPKIN,NETHER_WART,SWEET_BERRY_BUSH,CACTUS:{
                handleBlockBreakEvent(event);
            }
            default: {
                break;
            }
        }
    }

    public void handleBlockBreakEvent(BlockBreakEvent event){
        Block block = event.getBlock();
        if(ItemHandler.itemIsHoe(event.getPlayer().getInventory().getItem(EquipmentSlot.HAND).getType())||
                ItemHandler.itemIsHoe(event.getPlayer().getInventory().getItem(EquipmentSlot.OFF_HAND).getType())) {

            switch (block.getType()){
                case Material.CACTUS -> {
                    handleCactus(event);
                    break;
                } case Material.CARROTS -> {
                    handleCarrots(event);
                    break;
                } case Material.POTATOES -> {
                    handlePotatoes(event);
                    break;
                }

            }
        }
    }

    public void handleCactus(BlockBreakEvent event){
        Location blockLoc = event.getBlock().getLocation().toBlockLocation();
        blockLoc.add(0.5,0.5,0.5);
        BlockData data = event.getBlock().getBlockData();
        if(data instanceof Ageable) {
            Ageable ageable = (Ageable)data;
            if(ageable.getAge()>2)
                event.getBlock().getWorld().dropItemNaturally(blockLoc, ItemHandler.buildItemBkt(CorvidAgricultureItems.PRICKLY_PEAR));
        }
    }

    public void handleCarrots(BlockBreakEvent event){

    }

    public void handlePotatoes(BlockBreakEvent event){
        
    }
}
