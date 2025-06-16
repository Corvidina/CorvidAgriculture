package com.corvidina.corvidAgriculture.listeners;

import com.corvidina.corvidAgriculture.*;
import com.corvidina.corvidAgriculture.items.Crop;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import com.corvidina.corvidAgriculture.items.Zeal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Iterator;

public class BlockBreaks implements Listener {
    private final CorvidAgriculture plugin;
    public BlockBreaks(){
        plugin=CorvidAgriculture.getPlugin(CorvidAgriculture.class);
    }
    public void handleGeneralBlockBreak(Block block, Cancellable event){

        if(plugin.getBushMap().containsKey(block.getLocation())){
            Location loca = block.getLocation();
            if(plugin.getBushMap().get(block.getLocation()).isTall()){
                loca.add(0,1,0);
                loca.getBlock().setType(Material.AIR);
                breakBerriesAdj(plugin.getBushMap().get(block.getLocation()));
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

        if(plugin.getRiceMap().containsKey(block.getLocation())){
            Rice rice = plugin.getRiceMap().remove(block.getLocation());
            Rice.breakRice(rice);
        }

        if(concernedVanillaBlock(block)) {
            dropBlockCropParallel(block, getRespectiveItemFromBlockMaterial(block.getType()), getRespectiveCropFromBlockMaterial(block.getType()));
        }

    }

    public void handlePlayerBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if(!concernedVanillaBlock(event.getBlock())){
            handleGeneralBlockBreak(event.getBlock(),event);
            return;
        }
        if(ItemHandler.itemIsHoe(event.getPlayer().getInventory().getItem(EquipmentSlot.HAND).getType())
            || ItemHandler.itemIsHoe(event.getPlayer().getInventory().getItem(EquipmentSlot.OFF_HAND).getType())) {
            Material type = event.getBlock().getType();
            if(concernedVanillaBlock(event.getBlock())){
                dropBlockCropParallel(event.getBlock(), player, getRespectiveItemFromBlockMaterial(type), getRespectiveCropFromBlockMaterial(type), event);
            }


        } else {
            handleGeneralBlockBreak(event.getBlock(), event);
        }

    }

    public Material getRespectiveItemFromBlockMaterial(Material material){
        return switch (material) {
            case CARROTS -> Material.CARROT;
            case POTATOES -> Material.POTATO;
            case BEETROOTS -> Material.BEETROOT;
            case WHEAT->Material.WHEAT;
            case MELON -> Material.MELON_SLICE;
            case NETHER_WART -> Material.NETHER_WART;
            case SWEET_BERRY_BUSH -> Material.SWEET_BERRIES;
            case COCOA -> Material.COCOA_BEANS;
            case GLOW_BERRIES -> Material.GLOW_BERRIES;
            case CHORUS_FRUIT -> Material.CHORUS_FRUIT;
            default -> throw new IllegalArgumentException("Material is not considered concerned");
        };
    }

    public Crop getRespectiveCropFromBlockMaterial(Material material) {
        return switch (material) {
            case CARROTS -> Crop.CARROT;
            case POTATOES -> Crop.POTATO;
            case BEETROOT -> Crop.BEETROOT;
            case WHEAT -> Crop.WHEAT;
            case MELON -> Crop.MELON_SLICE;
            case NETHER_WART -> Crop.NETHER_WART;
            case SWEET_BERRY_BUSH -> Crop.SWEET_BERRIES;
            case COCOA -> Crop.COCOA_BEANS;
            case GLOW_BERRIES -> Crop.GLOW_BERRIES;
            case CHORUS_FRUIT -> Crop.CHORUS_FRUIT;
            default -> throw new IllegalArgumentException("Has no respective crop type");
        };
    }

    public void dropBlockCropParallel(Block block, Player player, Material material, Crop crop, Cancellable event){
        ItemStack tool = player.getInventory().getItemInMainHand();
        int i = tool.getEnchantmentLevel(Enchantment.SILK_TOUCH);
        if(i!=0 && block.getType()==Material.MELON) {
            return;
        }

        Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());

        // Total number of a specific material
        int count = drops.stream()
                .filter(item -> item.getType() == material)
                .mapToInt(ItemStack::getAmount)
                .sum();

        drops.removeIf(itemStack -> itemStack.getType()==material);

        Material replantableMaterial = null;

        double[] doubles = ItemHandler.getValueChances();
        if(block.getBlockData() instanceof Ageable age) {
            replantableMaterial=block.getType();
            if(age.getAge()==age.getMaximumAge()) {
                // Augment code here
            } else {
                ItemHandler.setValueChances(new double[]{1,0,0,0,0,0});
            }
        }

        block.setType(Material.AIR);

        int replanterLevel = ItemHandler.getZeal(player.getInventory().getItem(EquipmentSlot.HAND), Zeal.REPLANTER);

        if(replanterLevel>0) {
            replanterLevel=replanterLevel/Zeal.REPLANTER.getMaxLevel();
            if(Math.random()<replanterLevel) {
                if(replantableMaterial!=null) {
                    block.setType(replantableMaterial);
                    Ageable age = (Ageable)block.getBlockData();
                    age.setAge(0);
                    block.setBlockData(age);

                    // Event cancel needs replaced with queueing a player place event and executing that queue tickly
                    event.setCancelled(true);
                }
            }
        }


        Location loc = block.getLocation().add(0.5,0.5,0.5);
        ItemStack item = ItemHandler.buildItemBkt(crop);
        item.setAmount(count);
        loc.getWorld().dropItemNaturally(loc, item);
        for(Iterator<ItemStack> it = drops.stream().iterator(); it.hasNext();) {
            ItemStack stack = it.next();
            loc.getWorld().dropItemNaturally(loc,stack);
        }
        ItemHandler.setValueChances(doubles);
    }

    public void dropBlockCropParallel(Block block,  Material material, Crop crop) {
        Collection<ItemStack> drops = block.getDrops();

        // Total number of a specific material
        int count = drops.stream()
                .filter(item -> item.getType() == material)
                .mapToInt(ItemStack::getAmount)
                .sum();

        drops.removeIf(itemStack -> itemStack.getType()==material);

        double[] doubles = ItemHandler.getValueChances();
        if(block.getBlockData() instanceof Ageable age) {
            if(age.getAge()==age.getMaximumAge()) {
                // Augment code here
            } else {
                ItemHandler.setValueChances(new double[]{1,0,0,0,0,0});
            }
        }

        block.setType(Material.AIR);
        Location loc = block.getLocation().add(0.5,0.5,0.5);
        ItemStack item = ItemHandler.buildItemBkt(crop);
        item.setAmount(count);
        loc.getWorld().dropItemNaturally(loc, item);
        for(Iterator<ItemStack> it = drops.stream().iterator(); it.hasNext();){
            ItemStack stack = it.next();
            loc.getWorld().dropItemNaturally(loc,stack);
        }
        ItemHandler.setValueChances(doubles);
    }

    public void breakBerriesAdj(Bush bush){
        Location loc = bush.toLocation().add(0,1,0);
        Location loca = new Location(loc.getWorld(),loc.getBlockX(),loc.getBlockY(),loc.getBlockZ()+1);
        if(loca.getBlock().getType()==Material.PLAYER_WALL_HEAD){
            Block block = loca.getBlock();
            BlockData data = block.getBlockData();
            if(data instanceof Directional direction) {
                if(direction.getFacing()== BlockFace.SOUTH)
                    if(plugin.getBerryLikeMap().containsKey(loca))
                        BerryLike.breakBerry(plugin.getBerryLikeMap().get(loca));
            }
        }
        loca = new Location(loc.getWorld(),loc.getBlockX()+1,loc.getBlockY(),loc.getBlockZ());
        if(loca.getBlock().getType()==Material.PLAYER_WALL_HEAD){
            Block block = loca.getBlock();
            BlockData data = block.getBlockData();
            if(data instanceof Directional direction) {
                if(direction.getFacing()==BlockFace.EAST)
                    if(plugin.getBerryLikeMap().containsKey(loca))
                        BerryLike.breakBerry(plugin.getBerryLikeMap().get(loca));
            }
        }
        loca = new Location(loc.getWorld(),loc.getBlockX()-1,loc.getBlockY(),loc.getBlockZ());
        if(loca.getBlock().getType()==Material.PLAYER_WALL_HEAD){
            Block block = loca.getBlock();
            BlockData data = block.getBlockData();
            if(data instanceof Directional direction) {
                if(direction.getFacing()==BlockFace.WEST)
                    if(plugin.getBerryLikeMap().containsKey(loca))
                        BerryLike.breakBerry(plugin.getBerryLikeMap().get(loca));
            }
        }
        loca = new Location(loc.getWorld(),loc.getBlockX(),loc.getBlockY(),loc.getBlockZ()-1);
        if(loca.getBlock().getType()==Material.PLAYER_WALL_HEAD){
            Block block = loca.getBlock();
            BlockData data = block.getBlockData();
            if(data instanceof Directional direction) {
                if(direction.getFacing()==BlockFace.NORTH)
                    if(plugin.getBerryLikeMap().containsKey(loca))
                        BerryLike.breakBerry(plugin.getBerryLikeMap().get(loca));
            }
        }
    }

    private boolean concernedVanillaBlock(Block block){
        return switch (block.getType()) {
            case CARROTS,POTATOES,BEETROOTS,WHEAT,MELON,NETHER_WART,SWEET_BERRY_BUSH,COCOA,SUGAR_CANE,GLOW_BERRIES,CHORUS_FRUIT -> true;
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
            handleGeneralBlockBreak(block, null);
    }
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event){
        handleGeneralBlockBreak(event.getBlock(), event);
    }
    @EventHandler
    public void onFluidFlow(BlockFromToEvent event) {
        handleGeneralBlockBreak(event.getBlock(), event);
    }
    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event){
        handleGeneralBlockBreak(event.getBlock(), event);
    }
    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event){
        handleGeneralBlockBreak(event.getBlock(), event);
    }
    @EventHandler
    public void onBurn(BlockBurnEvent event){
        handleGeneralBlockBreak(event.getBlock(), event);
    }

    @EventHandler
    public void onFade(BlockFadeEvent event){
        handleGeneralBlockBreak(event.getBlock(), event);
    }




}
