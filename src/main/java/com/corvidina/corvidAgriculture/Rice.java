package com.corvidina.corvidAgriculture;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Rice {
    private String world;
    private int x,y,z;

    public Rice(Location loc) {
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
        this.world = loc.getWorld().getName();
    }

    public Location toLocation(){
        return new Location(Bukkit.getWorld(world),x,y,z);
    }

    public static void growRice(Rice rice){
        Location loc = rice.toLocation();


    }


}
