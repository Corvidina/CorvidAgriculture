package com.corvidina.corvidAgriculture;

import com.corvidina.corvidAgriculture.items.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.TreeMap;

public class TickHandler extends BukkitRunnable {
    private final CorvidAgriculture plugin;

    public TickHandler() {
        this.plugin = CorvidAgriculture.getPlugin(CorvidAgriculture.class);
    }

    @Override
    public void run() {

        for (Location loc : plugin.getBushMap().keySet()) {
            if (loc.isChunkLoaded()) {
                Location loca = loc.clone();
                Bush bush = plugin.getBushMap().get(loc);
                loca.add(0, 1, 0);
                if (bush.isTall()) {
                    loca.add(0, 1, 0);
                    if (Math.random() < 0.67)
                        bush.incrementAge(1);
                    if (bush.getAge()>=5)
                        Bush.growBerry(bush);
                } else if (loca.getBlock().getType() == Material.AIR) {
                    if (Math.random() < 0.67)
                        bush.incrementAge(1);
                    if (bush.getAge() >= 5)
                        Bush.growBerry(bush);
                }
            }
        }

        for (Location loc : plugin.getGrowingLeavesMap().keySet()) {
            //Bukkit.getServer().sendPlainMessage("iterated fruit leaf at: "+loc.toString());

            if (loc.isChunkLoaded()) {
                FruitGrowingLeaves growingLeaves = plugin.getGrowingLeavesMap().get(loc);
                if(loc.getBlock().getType()!=Material.OAK_LEAVES){
                    plugin.getGrowingLeavesMap().remove(loc);
                    continue;
                }
                if (growingLeaves.getAge()<7 && Math.random() < 0.3) {
                    growingLeaves.incrementAge(1);
                }
                if (growingLeaves.getAge()>=7) {
                    switch ((int)(Math.random()*6)) {
                        case 0 : {
                            // Case NORTH
                            Location loca = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()-1);
                            if (loca.getBlock().getType() == Material.AIR) {
                                loca.getBlock().setType(Material.PLAYER_WALL_HEAD);
                                ItemHandler.buildFruitBlock(loca.getBlock(),growingLeaves.getType(),BlockFace.NORTH);
                                growingLeaves.resetAge();
                            }
                            break;
                        }

                        case 1 : {
                            // Case SOUTH

                            Location loca = new Location(loc.getWorld(),loc.getBlockX(),loc.getBlockY(),loc.getBlockZ()+1);
                            if (loca.getBlock().getType() == Material.AIR) {
                                loca.getBlock().setType(Material.PLAYER_WALL_HEAD);
                                ItemHandler.buildFruitBlock(loca.getBlock(),growingLeaves.getType(),BlockFace.SOUTH);
                                growingLeaves.resetAge();
                            }
                            break;
                        }

                        case 2 : {
                            // Case EAST

                            Location loca = new Location(loc.getWorld(),loc.getBlockX()+1,loc.getBlockY(),loc.getBlockZ());
                            if (loca.getBlock().getType() == Material.AIR) {
                                loca.getBlock().setType(Material.PLAYER_WALL_HEAD);
                                ItemHandler.buildFruitBlock(loca.getBlock(),growingLeaves.getType(),BlockFace.EAST);
                                growingLeaves.resetAge();
                            }
                            break;
                        }

                        case 3 : {
                            // Case WEST

                            Location loca = new Location(loc.getWorld(),loc.getBlockX()-1,loc.getBlockY(),loc.getBlockZ());
                            if (loca.getBlock().getType() == Material.AIR){
                                loca.getBlock().setType(Material.PLAYER_WALL_HEAD);
                                ItemHandler.buildFruitBlock(loca.getBlock(),growingLeaves.getType(),BlockFace.WEST);
                                growingLeaves.resetAge();
                            }
                            break;
                        }

                        case 4 : {
                            // Case UP

                            Location loca = new Location(loc.getWorld(),loc.getBlockX(),loc.getBlockY()+1,loc.getBlockZ());
                            if (loca.getBlock().getType() == Material.AIR){
                                loca.getBlock().setType(Material.PLAYER_HEAD);
                                ItemHandler.buildFruitBlock(loca.getBlock(),growingLeaves.getType());
                                growingLeaves.resetAge();
                            }
                            break;
                        }

                        case 5 : {
                            // Case DOWN

                            Location loca = new Location(loc.getWorld(),loc.getBlockX(),loc.getBlockY()-1,loc.getBlockZ());
                            if (loca.getBlock().getType() == Material.AIR) {
                                loca.getBlock().setType(Material.PLAYER_WALL_HEAD);
                                ItemHandler.buildFruitBlock(loca.getBlock(), growingLeaves.getType(),
                                        switch ((int)(Math.random()*4)) {
                                            case 0 -> BlockFace.NORTH;
                                            case 1 -> BlockFace.WEST;
                                            case 2 -> BlockFace.EAST;
                                            case 3 -> BlockFace.SOUTH;
                                            default -> null;
                                        });
                                growingLeaves.resetAge();
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
