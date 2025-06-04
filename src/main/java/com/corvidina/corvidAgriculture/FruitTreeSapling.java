package com.corvidina.corvidAgriculture;

import com.corvidina.corvidAgriculture.items.Crops;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftWorld;

public class FruitTreeSapling {
    private static final CorvidAgriculture plugin = CorvidAgriculture.getPlugin(CorvidAgriculture.class);
    private final String world;
    private final int x,y,z;
    private final String type;


    public FruitTreeSapling(Location loc, Crops type){
        this.x=loc.getBlockX();
        this.y=loc.getBlockY();
        this.z=loc.getBlockZ();
        this.world=loc.getWorld().getName();
        this.type=type.name().toLowerCase();
    }

    public Location toLocation(){
        return new Location(Bukkit.getWorld(world),x,y,z);
    }

    public Crops getType(){
        return Crops.valueOf(type.toUpperCase());
    }

    public static FruitTreeSapling buildFruitTreeSapling(Location loc, Crops type) {
        loc.getBlock().setType(Material.OAK_SAPLING);
        FruitTreeSapling sapling = new FruitTreeSapling(loc,type);
        plugin.getFruitTreeSaplingMap().put(loc.toBlockLocation(),sapling);
        return sapling;
    }

    public static void growSapling(FruitTreeSapling sapling){
        // Growth code here - it may be complicated
        Location loc = sapling.toLocation();

        String structureName = plugin.getTreeNames()[(int)(plugin.getTreeNames().length*Math.random())];
        ServerLevel serverLevel = ((CraftWorld) loc.getWorld()).getHandle();
        StructureTemplate template = serverLevel.getStructureManager().get(ResourceLocation.tryBuild("corvidagriculture", structureName)).orElse(null);

        if (template == null) {
            Bukkit.getLogger().warning("Could not find structure: corvidagriculture:" + structureName);
            return;
        }

        BlockPos pos = null;

        StructurePlaceSettings settings = null;

        Mirror mirror = Mirror.NONE;

        switch ((int)(Math.random()*4)){
            case 0 : {
                settings = new StructurePlaceSettings()
                        .setIgnoreEntities(true)
                        .setMirror(mirror)
                        .setRotation(Rotation.NONE)
                        .setRandom(serverLevel.random);
                pos = BlockPos.containing(loc.getX()-2,loc.getY(),loc.getZ()-2);
                //Bukkit.getServer().sendPlainMessage("0");
                break;
            }
            case 1 : {
                settings = new StructurePlaceSettings()
                        .setIgnoreEntities(true)
                        .setMirror(mirror)
                        .setRotation(Rotation.CLOCKWISE_90)
                        .setRandom(serverLevel.random);
                pos = BlockPos.containing(loc.getX()+2,loc.getY(),loc.getZ()-2);
                //Bukkit.getServer().sendPlainMessage("90");
                break;
            }
            case 2 : {
                settings = new StructurePlaceSettings()
                        .setIgnoreEntities(true)
                        .setMirror(mirror)
                        .setRotation(Rotation.CLOCKWISE_180)
                        .setRandom(serverLevel.random);
                pos = BlockPos.containing(loc.getX()+2,loc.getY(),loc.getZ()+2);
                //Bukkit.getServer().sendPlainMessage("180");
                break;
            }
            case 3 : {
                settings = new StructurePlaceSettings()
                        .setIgnoreEntities(true)
                        .setMirror(mirror)
                        .setRotation(Rotation.COUNTERCLOCKWISE_90)
                        .setRandom(serverLevel.random);
                pos = BlockPos.containing(loc.getX()-2,loc.getY(),loc.getZ()+2);
                //Bukkit.getServer().sendPlainMessage("270");
            }
        }

        int yAddition = structureName.contains("large")?6:5;

        for (int x = loc.getBlockX()-2; x <= loc.getBlockX()+2;x++){
            for (int y = loc.getBlockY();y <= loc.getBlockY()+yAddition;y++){
                for (int z = loc.getBlockZ()-2;z <= loc.getBlockZ()+2; z++){
                    if(x==loc.getBlockX()&&y==loc.getBlockY()&&z==loc.getBlockZ())
                        continue;
                    if(new Location(loc.getWorld(),x,y,z).getBlock().getType()!=Material.AIR) {
                        //Bukkit.getServer().sendPlainMessage("No space: " + new Location(loc.getWorld(),x,y,z).getBlock().getType());
                        //Bukkit.getServer().sendPlainMessage(x+","+y+","+z);
                        //Bukkit.getServer().sendPlainMessage(loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ());

                        return;
                    }
                }
            }
        }

        template.placeInWorld(serverLevel, pos, pos, settings, serverLevel.random, 3);

        for (int x = loc.getBlockX()-2; x < loc.getBlockX()+2; x++){
            for (int y = loc.getBlockY(); y < loc.getBlockY()+yAddition; y++) {
                for (int z = loc.getBlockZ()-2; z < loc.getBlockZ()+2; z++){
                    Location loca = new Location(loc.getWorld(),x,y,z);
                    if(loca.getBlock().getType()==Material.OAK_LEAVES) {
                        if (numAdjacentAirBlocks(loca) > 0) {
                            if (Math.random() < 0.4) {
                                //make a fruit growing leaf
                                plugin.getGrowingLeavesMap().put(loca, new FruitGrowingLeaves(loca, sapling.getType()));
                            }
                        }
                    }
                }
            }
        }

        plugin.getFruitTreeSaplingMap().remove(loc);
    }

    public static void breakSapling(FruitTreeSapling sapling){
        Location loc = sapling.toLocation().add(0.5,0.5,0.5);
        loc.getWorld().dropItemNaturally(loc, ItemHandler.buildItemBkt(Crops.getCorrespondingSeed(sapling.getType())));
    }

    private static int numAdjacentAirBlocks(Location loc){
        int num = 0;
        loc.add(0,1,0);
        if(loc.getBlock().getType()==Material.AIR)
            num++;
        loc.add(1,-1,0);
        if(loc.getBlock().getType()==Material.AIR)
            num++;
        loc.add(-1,-1,0);
        if(loc.getBlock().getType()==Material.AIR)
            num++;
        loc.add(-1,1,0);
        if(loc.getBlock().getType()==Material.AIR)
            num++;
        loc.add(1,0,1);
        if(loc.getBlock().getType()==Material.AIR)
            num++;
        loc.add(0,0,-2);
        if(loc.getBlock().getType()==Material.AIR)
            num++;
        loc.add(0,0,2);
        return num;
    }
}
