package com.corvidina.corvidAgriculture;

import com.corvidina.corvidAgriculture.items.Crop;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class FruitGrowingLeaves {
    private static final CorvidAgriculture plugin = CorvidAgriculture.getPlugin(CorvidAgriculture.class);
    private final String world,type;
    private final double x,y,z;
    private byte age;

    public FruitGrowingLeaves(Location loc, Crop type){
        this.type=type.name().toLowerCase();
        this.world=loc.getWorld().getName();
        this.x=loc.getX();
        this.y=loc.getY();
        this.z=loc.getZ();
    }

    public Location toLocation(){
        return new Location(Bukkit.getWorld(world),x,y,z);
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

    public static void breakLeaves(FruitGrowingLeaves leaves){
        Location loc = leaves.toLocation();
        plugin.getGrowingLeavesMap().remove(loc);
        loc.getBlock().setType(Material.AIR);
        loc = loc.toBlockLocation();
        loc.getWorld().dropItemNaturally(loc.add(0.5,0.5,0.5), ItemHandler.buildItemBkt(Crop.getCorrespondingSeed(leaves.getType())));
    }
}
