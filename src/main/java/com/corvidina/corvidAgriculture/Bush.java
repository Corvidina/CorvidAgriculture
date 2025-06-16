package com.corvidina.corvidAgriculture;

import com.corvidina.corvidAgriculture.items.Crop;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Bush {
    private static final CorvidAgriculture plugin = CorvidAgriculture.getPlugin(CorvidAgriculture.class);
    private final String world;
    private final int x,y,z;
    private byte age;
    private final boolean tall;
    private final String type;

    public Bush(Location loc, Crop type, boolean tall){
        this.world=loc.getWorld().getName();
        this.x=loc.getBlockX();
        this.y=loc.getBlockY();
        this.z=loc.getBlockZ();
        this.tall=tall;
        this.age=0;
        this.type=type.name().toLowerCase();
    }

    public Location toLocation(){
        World w = Bukkit.getWorld(world);
        return new Location(w,x,y,z);
    }

    public boolean isTall(){
        return tall;
    }

    public int getAge(){
        return age;
    }

    public void incrementAge(int increment){
        age+=(byte)increment;
    }

    public void resetAge(){
        age=0;
    }

    public Crop getType(){
        return Crop.valueOf(type.toUpperCase());
    }

    public static Bush buildBush(Location loc, Crop type, boolean isTall){
        Bush bush = new Bush(loc,type,isTall);
        plugin.getBushMap().put(loc,bush);
        loc = loc.clone();
        if(isTall) {
            loc.getBlock().setType(Material.AZALEA);
            loc.add(0,1,0);
            loc.getBlock().setType(Material.MOSS_BLOCK);
        } else {
            if(Math.random()<0.33){
                loc.getBlock().setType(Material.FLOWERING_AZALEA);
            } else {
                loc.getBlock().setType(Material.AZALEA);
            }
        }
        return bush;
    }

    public static void growBerry(Bush bush) {
        Location loc = bush.toLocation();
        if (bush.isTall()) {
            loc.add(0, 2, 0);
            if (loc.getBlock().getType()==Material.AIR){
                Block block = loc.getBlock();
                block.setType(Material.PLAYER_HEAD);
                ItemHandler.buildFruitBlock(block, bush.getType());
                bush.resetAge();
            } else {
                loc.add(0, -1, 0);

                //Bukkit.getServer().sendPlainMessage("made it to adj berries check");
                if (getAdjacentBerries(loc)<2){
                    //Bukkit.getServer().sendPlainMessage("made it through adj berries check");
                    switch ((int)(Math.random()*4)) {
                        case 0: {
                            loc.add(1,0,0);
                            if(loc.getBlock().getType()==Material.AIR) {
                                loc.getBlock().setType(Material.PLAYER_WALL_HEAD);
                                ItemHandler.buildFruitBlock(loc.getBlock(), bush.getType(), BlockFace.EAST);
                                bush.resetAge();
                            }
                            break;
                        }
                        case 1: {
                            loc.add(-1,0,0);
                            if(loc.getBlock().getType()==Material.AIR) {
                                loc.getBlock().setType(Material.PLAYER_WALL_HEAD);
                                ItemHandler.buildFruitBlock(loc.getBlock(), bush.getType(), BlockFace.WEST);
                                bush.resetAge();
                            }
                            break;
                        }
                        case 2: {
                            loc.add(0,0,1);
                            if(loc.getBlock().getType()==Material.AIR) {
                                loc.getBlock().setType(Material.PLAYER_WALL_HEAD);
                                ItemHandler.buildFruitBlock(loc.getBlock(), bush.getType(), BlockFace.SOUTH);
                                bush.resetAge();
                            }
                            break;
                        }
                        case 3: {
                            loc.add(0,0,-1);
                            if(loc.getBlock().getType()==Material.AIR) {
                                loc.getBlock().setType(Material.PLAYER_WALL_HEAD);
                                ItemHandler.buildFruitBlock(loc.getBlock(), bush.getType(), BlockFace.NORTH);
                                bush.resetAge();
                            }
                            break;
                        }
                    }
                }
            }
            return;
        } else {
            loc.add(0, 1, 0);
            Block block = loc.getBlock();
            block.setType(Material.PLAYER_HEAD);
            ItemHandler.buildFruitBlock(block, bush.getType());
            bush.resetAge();
        }
    }

    public static void dropSeeds(Bush bush) {
        Location loc = bush.toLocation().add(0.5,0.5,0.5);
        loc.getWorld().dropItemNaturally(loc,ItemHandler.buildItemBkt(Crop.getCorrespondingSeed(bush.getType())));
    }

    private static int getAdjacentBerries(Location loc){
        int tota = 0;
        if (plugin.getBerryLikeMap().containsKey(new Location(loc.getWorld(),loc.getBlockX()+1,loc.getBlockY(),loc.getBlockZ()))){
            tota++;
        }
        if (plugin.getBerryLikeMap().containsKey(new Location(loc.getWorld(),loc.getBlockX()-1,loc.getBlockY(),loc.getBlockZ()))) {
            tota++;
        }
        if (plugin.getBerryLikeMap().containsKey(new Location(loc.getWorld(), loc.getBlockX(),loc.getBlockY(),loc.getBlockZ()-1))) {
            tota++;
        }
        if (plugin.getBerryLikeMap().containsKey(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()+1))) {
            tota++;
        }
        return tota;

    }
}
