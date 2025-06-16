package com.corvidina.corvidAgriculture;

import com.corvidina.corvidAgriculture.items.Crop;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import com.corvidina.corvidAgriculture.items.Seeds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class LettucePlant {
    private static final CorvidAgriculture plugin = CorvidAgriculture.getPlugin(CorvidAgriculture.class);
    private byte age;
    private int x,y,z;
    private String world;
    public LettucePlant(Location loc, byte age) {
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
        this.age=age;
        this.world = loc.getWorld().getName();
    }

    public Location toLocation(){
        return new Location(Bukkit.getWorld(world),x,y,z);
    }

    public void incrementAge(int increment){
        age+=(byte)increment;
    }

    public byte getAge(){
        return age;
    }

    public void resetAge(){
        age=0;
    }

    public static LettucePlant buildLettucePlant(Location loc){
        LettucePlant lettucePlant = new LettucePlant(loc,(byte)0);
        loc.getBlock().setType(Material.TORCHFLOWER_SEEDS);
        plugin.getLettucePlantMap().put(loc.toBlockLocation(),lettucePlant);
        return lettucePlant;
    }

    public static void growLettucePlant(LettucePlant lettucePlant){
        Location loc = lettucePlant.toLocation();

        ItemHandler.buildFruitBlock(loc.getBlock(),Crop.LETTUCE);
        plugin.getBerryLikeMap().put(loc, new BerryLike(loc, Crop.LETTUCE));

        plugin.getLettucePlantMap().remove(loc);
    }

    public static void breakLettucePlant(LettucePlant lettucePlant){
        Location loc = lettucePlant.toLocation();
        loc.getBlock().setType(Material.AIR);
        loc.add(0.5,0.5,0.5);

        loc.getWorld().dropItemNaturally(loc, ItemHandler.buildItemBkt(Seeds.LETTUCE_SEEDS));
    }


}
