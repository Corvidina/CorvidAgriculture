package com.corvidina.corvidAgriculture;

import com.corvidina.corvidAgriculture.items.Crops;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class BerryLike {
    private static final CorvidAgriculture plugin = CorvidAgriculture.getPlugin(CorvidAgriculture.class);
    private final String world;
    private final int x,y,z;
    private final String type;

    public BerryLike(Location loc, Crops type){
        this.world=loc.getWorld().getName();
        this.x=loc.getBlockX();
        this.y=loc.getBlockY();
        this.z=loc.getBlockZ();
        this.type=type.name().toLowerCase();
    }

    public Location toLocation(){
        return new Location(Bukkit.getWorld(world),x,y,z);
    }

    public Crops getType(){
        return Crops.valueOf(type.toUpperCase());
    }

    public static void breakBerry(BerryLike berry){
        Location loc = berry.toLocation();
        loc.getBlock().setType(Material.AIR);
        plugin.getBerryLikeMap().remove(loc);
        loc = loc.toBlockLocation();
        loc.getWorld().dropItemNaturally(loc.add(0.5,0.5,0.5), ItemHandler.buildItemBkt(berry.getType()));

        // break animation and sound to be implemented
    }

}
