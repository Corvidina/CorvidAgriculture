package com.corvidina.corvidAgriculture;

import com.corvidina.corvidAgriculture.items.Crop;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import com.corvidina.corvidAgriculture.items.Seeds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class Rice {
    private static final CorvidAgriculture plugin = CorvidAgriculture.getPlugin(CorvidAgriculture.class);
    private String world;
    private int x,y,z;
    private byte age;

    public Rice(Location loc, byte age) {
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

    public static Rice buildRice(Location loc){
        Rice rice = new Rice(loc,(byte)0);
        loc.getBlock().setType(Material.KELP);
        plugin.getRiceMap().put(loc.toBlockLocation(),rice);
        return rice;
    }

    public static void growRice(Rice rice){
        Location loc = rice.toLocation();
        loc.add(0,1,0);
        if(loc.getBlock().getType()==Material.AIR){
            loc.getBlock().setType(Material.FERN,false);
            rice.toLocation().getBlock().setType(Material.KELP_PLANT,false);
            plugin.getBerryLikeMap().put(loc, new BerryLike(loc, Crop.RICE));
        }
    }

    public static void breakRice(Rice rice){
        Location loc = rice.toLocation();
        loc.getBlock().setType(Material.WATER);
        loc.add(0.5,0.5,0.5);
        double[] d = ItemHandler.getValueChances();
        ItemHandler.setValueChances(new double[]{1,0,0,0,0,0});
        loc.getWorld().dropItemNaturally(loc, ItemHandler.buildItemBkt(Seeds.RICE));
        ItemHandler.setValueChances(d);
    }


}
