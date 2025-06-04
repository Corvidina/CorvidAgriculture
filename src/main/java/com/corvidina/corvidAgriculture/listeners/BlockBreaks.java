package com.corvidina.corvidAgriculture.listeners;

import com.corvidina.corvidAgriculture.*;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.EquipmentSlot;

public class BlockBreaks implements Listener {
    private final CorvidAgriculture plugin;
    public BlockBreaks(){
        plugin=CorvidAgriculture.getPlugin(CorvidAgriculture.class);
    }
    public void handleBlockBreak(Block block){

        if(plugin.getBushMap().containsKey(block.getLocation())){
            Location loca = block.getLocation();
            if(plugin.getBushMap().get(block.getLocation()).isTall()){
                loca.add(0,1,0);
                loca.getBlock().setType(Material.AIR);
            }
            loca.add(0,1,0);
            if(plugin.getBerryLikeMap().containsKey(loca)){
                BerryLike.breakBerry(plugin.getBerryLikeMap().get(loca));
            }

            Bush.dropSeeds(plugin.getBushMap().get(block.getLocation()));
            plugin.getBushMap().remove(block.getLocation());
            block.setType(Material.AIR);

        }

        if(plugin.getBushMap().containsKey(block.getLocation().add(0,-1,0))
                &&plugin.getBushMap().get(block.getLocation().add(0,-1,0)).isTall()
        ){
            block.setType(Material.AIR);
            Location loc = block.getLocation().add(0,-1,0);
            Bush.dropSeeds(plugin.getBushMap().get(loc));
            loc.getBlock().setType(Material.AIR);
            loc.add(0,2,0);

            if(plugin.getBerryLikeMap().containsKey(loc)){
                BerryLike.breakBerry(plugin.getBerryLikeMap().get(loc));
            }
        }

        if(plugin.getGrowingLeavesMap().containsKey(block.getLocation())){
            FruitGrowingLeaves leaves = plugin.getGrowingLeavesMap().remove(block.getLocation());
            FruitGrowingLeaves.breakLeaves(leaves);
        }

        if(plugin.getFruitTreeSaplingMap().containsKey(block.getLocation())){
            FruitTreeSapling sapling = plugin.getFruitTreeSaplingMap().remove(block.getLocation());
            FruitTreeSapling.breakSapling(sapling);
        }


    }

    public void handlePlayerBlockBreak(BlockBreakEvent event) {
        if(!concernedVanillaBlock(event.getBlock())){
            handleBlockBreak(event.getBlock());
            return;
        }
        if(ItemHandler.itemIsHoe(event.getPlayer().getInventory().getItem(EquipmentSlot.HAND).getType())
            || ItemHandler.itemIsHoe(event.getPlayer().getInventory().getItem(EquipmentSlot.OFF_HAND).getType())) {

            // will need to go back and check how to write this to drop with fortune in mind

        } else {
            handleBlockBreak(event.getBlock());
        }

    }

    private boolean concernedVanillaBlock(Block block){
        return switch (block.getType()) {
            case CARROTS,POTATOES,BEETROOT,WHEAT,MELON,PUMPKIN,NETHER_WART,SWEET_BERRIES,COCOA_BEANS,SUGAR_CANE,CACTUS,GLOW_BERRIES,CHORUS_FRUIT -> true;
            default -> false;
        };
    }

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event) {
        handlePlayerBlockBreak(event);
    }
    @EventHandler
    public void onExplosion(EntityExplodeEvent event){
        for (Block block : event.blockList())
            handleBlockBreak(block);
    }
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event){
        handleBlockBreak(event.getBlock());
    }
    @EventHandler
    public void onFluidFlow(BlockFromToEvent event) {
        handleBlockBreak(event.getBlock());
    }
    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event){
        handleBlockBreak(event.getBlock());
    }
    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event){
        handleBlockBreak(event.getBlock());
    }
    @EventHandler
    public void onBurn(BlockBurnEvent event){
        handleBlockBreak(event.getBlock());
    }

    @EventHandler
    public void onFade(BlockFadeEvent event){
        handleBlockBreak(event.getBlock());
    }




}
