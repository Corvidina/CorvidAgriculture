package com.corvidina.corvidAgriculture.listeners;

import com.corvidina.corvidAgriculture.CorvidAgriculture;
import com.corvidina.corvidAgriculture.FruitTreeSapling;
import com.corvidina.corvidAgriculture.items.Crops;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class Grow implements Listener {
    private CorvidAgriculture plugin;
    public Grow(){
        this.plugin = CorvidAgriculture.getPlugin(CorvidAgriculture.class);
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent event){

        if(event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().add(0,-1,0)).getType()==Material.CACTUS
                && event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().add(0,-2,0)).getType()==Material.CACTUS){
            if(Math.random()<0.5) {
                event.setCancelled(true);

                //Bukkit.getServer().sendPlainMessage("Prickly pear should've been created");

                Block block = event.getBlock();
                block.setType(Material.PLAYER_HEAD);
                ItemHandler.buildFruitBlock(block, Crops.PRICKLY_PEAR);
            }
        }
    }

    @EventHandler
    public void onStructureGrow(StructureGrowEvent event){

        // WARNING -> unaware if this is sapling loc or root location for structure
        Location loc = event.getLocation();
        if(plugin.getFruitTreeSaplingMap().containsKey(loc)){
            event.setCancelled(true);
            FruitTreeSapling.growSapling(plugin.getFruitTreeSaplingMap().get(loc));
        }
    }
}
