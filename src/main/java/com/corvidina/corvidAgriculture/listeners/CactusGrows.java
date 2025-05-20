package com.corvidina.corvidAgriculture.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

public class CactusGrows implements Listener {

    @EventHandler
    public void onBlockGrow(BlockGrowEvent event){

        if(event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().add(0,-1,0)).getType()==Material.CACTUS&&event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().add(0,-2,0)).getType()==Material.CACTUS){
            if(Math.random()<0.5){
                event.setCancelled(true);
                Bukkit.getServer().sendPlainMessage("Prickly pear should've been created");
                //Place a prickly pear at this block
            }
        }

    }
}
