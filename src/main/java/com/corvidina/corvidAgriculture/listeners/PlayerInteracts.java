package com.corvidina.corvidAgriculture.listeners;

import com.corvidina.corvidAgriculture.BerryLike;
import com.corvidina.corvidAgriculture.Bush;
import com.corvidina.corvidAgriculture.CorvidAgriculture;
import com.corvidina.corvidAgriculture.FruitTreeSapling;
import com.corvidina.corvidAgriculture.items.Crops;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import com.corvidina.corvidAgriculture.items.Seeds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
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
        if (block.getType() == Material.PLAYER_HEAD || block.getType() == Material.PLAYER_WALL_HEAD) {
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
            case BLUEBERRY_SEEDS: {
                Bush.buildBush(event.getBlock().getLocation(), Crops.BLUEBERRY, true);
                break;
            }
            case CRANBERRY_SEEDS: {
                Bush.buildBush(event.getBlock().getLocation(), Crops.CRANBERRY, false);
                break;
            }
            case LEMON_TREE_SAPLING: {
                FruitTreeSapling.buildFruitTreeSapling(event.getBlock().getLocation(), Crops.LEMON);
                break;
            }
        }

    }

}
