package com.corvidina.corvidAgriculture.listeners;

import com.corvidina.corvidAgriculture.*;
import com.corvidina.corvidAgriculture.items.Crop;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import com.corvidina.corvidAgriculture.items.Seeds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteracts implements Listener {
    private final CorvidAgriculture plugin;
    public PlayerInteracts(){
        this.plugin=CorvidAgriculture.getPlugin(CorvidAgriculture.class);

    }
    @EventHandler
    public void onHarvest(PlayerHarvestBlockEvent event){
        // This includes sweet berries and hanging vines
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        Block block = event.getClickedBlock();
        if(block==null){
            return;
        }
        if (block.getType() == Material.PLAYER_HEAD || block.getType() == Material.PLAYER_WALL_HEAD || block.getType()==Material.FERN) {
            Location loc = block.getLocation();
            if(plugin.getBerryLikeMap().containsKey(loc)){
                BerryLike berry = plugin.getBerryLikeMap().get(loc);
                event.getClickedBlock().setType(Material.AIR);
                event.getPlayer().getInventory().addItem(
                        ItemHandler.buildItemBkt(berry.getType())
                );
                plugin.getBerryLikeMap().remove(loc);
                event.setCancelled(true);
            }

        }
    }
    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event){
        ItemStack stack = event.getItemInHand();
        // the stack is always the placed block

        Seeds seedType = ItemHandler.getSeedType(stack);
        switch (seedType){
            case null : break;
            case LETTUCE_SEEDS: {
                break;
            }
            case BANANA_TREE_SAPLING: {
                FruitTreeSapling.buildFruitTreeSapling(event.getBlock().getLocation(), Crop.BANANA);
                break;
            }
            case BLUEBERRY_SEEDS: {
                Bush.buildBush(event.getBlock().getLocation(), Crop.BLUEBERRY, true);
                break;
            }
            case CRANBERRY_SEEDS: {
                Bush.buildBush(event.getBlock().getLocation(), Crop.CRANBERRY, false);
                break;
            }
            case LEMON_TREE_SAPLING: {
                FruitTreeSapling.buildFruitTreeSapling(event.getBlock().getLocation(), Crop.LEMON);
                break;
            }
            case RICE: {
                Rice.buildRice(event.getBlock().getLocation());
                break;
            }
            case TOMATO_SEEDS: {
                Bush.buildBush(event.getBlock().getLocation(), Crop.TOMATO, true);
                break;
            }
        }

    }

}
