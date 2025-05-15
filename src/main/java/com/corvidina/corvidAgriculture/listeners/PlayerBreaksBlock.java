package com.corvidina.corvidAgriculture.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
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
        if(PlayerCrafts.itemIsHoe(event.getPlayer().getInventory().getItem(EquipmentSlot.HAND).getType())||
                PlayerCrafts.itemIsHoe(event.getPlayer().getInventory().getItem(EquipmentSlot.OFF_HAND).getType())) {

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

    }

    public void handleCarrots(BlockBreakEvent event){

    }

    public void handlePotatoes(BlockBreakEvent event){
        
    }
}
