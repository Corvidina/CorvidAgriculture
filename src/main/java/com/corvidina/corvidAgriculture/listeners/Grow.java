package com.corvidina.corvidAgriculture.listeners;

import com.corvidina.corvidAgriculture.CorvidAgriculture;
import com.corvidina.corvidAgriculture.FruitTreeSapling;
import com.corvidina.corvidAgriculture.LettucePlant;
import com.corvidina.corvidAgriculture.Rice;
import com.corvidina.corvidAgriculture.items.Crop;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFertilizeEvent;
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
                ItemHandler.buildFruitBlock(block, Crop.PRICKLY_PEAR);
            }
        }

        Location loc = event.getBlock().getLocation();
        if(plugin.getLettucePlantMap().containsKey(loc)) {
            Block block = loc.getBlock();
            if(block.getBlockData() instanceof Ageable age) {
                if(age.getAge()==2) {
                    LettucePlant.growLettucePlant(plugin.getLettucePlantMap().get(loc));
                }
            }
        }
    }

    @EventHandler
    public void onStructureGrow(StructureGrowEvent event){

        Location loc = event.getLocation();
        if(plugin.getFruitTreeSaplingMap().containsKey(loc)){
            event.setCancelled(true);
            FruitTreeSapling.growSapling(plugin.getFruitTreeSaplingMap().get(loc));
        }
        if(plugin.getBushMap().containsKey(loc)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBonemeal(BlockFertilizeEvent event) {
        if(plugin.getBushMap().containsKey(event.getBlock().getLocation())){
            plugin.getBushMap().get(event.getBlock().getLocation()).incrementAge(2);
            event.setCancelled(true);
        }
    }
}
