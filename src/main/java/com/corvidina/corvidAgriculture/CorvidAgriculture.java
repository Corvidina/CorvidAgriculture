package com.corvidina.corvidAgriculture;

import com.corvidina.corvidAgriculture.commands.AgricultureCommand;
import com.corvidina.corvidAgriculture.commands.TestCommand;
import com.corvidina.corvidAgriculture.gui.ServerCatering;
import com.corvidina.corvidAgriculture.items.Crops;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import com.corvidina.corvidAgriculture.listeners.BlockBreaks;
import com.corvidina.corvidAgriculture.listeners.Grow;
import com.corvidina.corvidAgriculture.listeners.PlayerInteracts;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.devnatan.inventoryframework.ViewFrame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class CorvidAgriculture extends JavaPlugin {
    public ViewFrame cateringMenu;
    private ServerCatering serverCatering;
    private CateringUpdater cateringUpdater;
    // bush mappings
    private ConcurrentHashMap<Location,Bush> bushMap;
    private final File bushFile = new File(getDataFolder(),"bushes.json");
    // berrylike list
    private HashMap<Location,BerryLike> berryMap;
    private final File berryFile = new File(getDataFolder(),"berry_like.json");

    private HashMap<Location,FruitTreeSapling> saplingMap;
    private final File saplingFile = new File(getDataFolder(),"fruit_tree_saplings.json");

    private ConcurrentHashMap<Location,FruitGrowingLeaves> growingLeavesMap;
    private final File growingLeavesFile = new File(getDataFolder(), "growing_leaves.json");

    private final File configFile = new File(getDataFolder(), "config.json");


    private double sellMultiplier;
    private HashMap<Crops, Double> materialBasedSellMultiplier;

    // final file names for trees
    private final String[] treeNames = new String[]{
          "oak_fruit_tree_large_1",
          "oak_fruit_tree_large_2",
          "oak_fruit_tree_large_3",
          "oak_fruit_tree_small_1",
          "oak_fruit_tree_small_2",
          "oak_fruit_tree_small_3"
    };


    private TickHandler tickHandler;

    private int cateringAdditionRate = 60;
    @Override
    public void onEnable() {
        // Plugin startup logic
        loadBushData();
        loadBerryLikes();
        loadFruitTreeSaplings();
        loadGrowingLeaves();
        loadConfigs();

        getLogger().info("Hello world from CorvidAgriculture!");

        registerCommands();
        registerListeners();

        ItemHandler.sellMultiplier=2;

        cateringMenu=ViewFrame.create(this);
        serverCatering=new ServerCatering();
        cateringMenu.with(serverCatering);
        cateringMenu.register();
        cateringUpdater = new CateringUpdater();
        cateringUpdater.runTaskTimer(this, 0, (long)cateringAdditionRate*20);

        tickHandler = new TickHandler();
        tickHandler.runTaskTimer(this,200,400);
        printStructures();

    }

    public double getSellMultiplier(){
        return sellMultiplier;
    }

    public ConcurrentHashMap<Location,Bush> getBushMap(){
        return bushMap;
    }

    public HashMap<Location,BerryLike> getBerryLikeMap(){
        return berryMap;
    }

    public HashMap<Location,FruitTreeSapling> getFruitTreeSaplingMap(){
        return saplingMap;
    }

    public ConcurrentHashMap<Location,FruitGrowingLeaves> getGrowingLeavesMap(){
        return growingLeavesMap;
    }

    public HashMap<Crops, Double> getMaterialBasedSellMultiplier(){
        return materialBasedSellMultiplier;
    }

    public String[] getTreeNames(){
        return treeNames;
    }

    public int getCateringAdditionRate(){
        return cateringAdditionRate;
    }

    public ViewFrame getCateringMenu(){
        return cateringMenu;
    }

    public ServerCatering getServerCateringView(){
        return serverCatering;
    }

    private void registerListeners(){
        getServer().getPluginManager().registerEvents(new Grow(),this);
        getServer().getPluginManager().registerEvents(new PlayerInteracts(), this);
        getServer().getPluginManager().registerEvents(new BlockBreaks(), this);
    }

    private void registerCommands(){
        this.getCommand("test").setExecutor(new TestCommand());
        this.getCommand("agriculture").setExecutor(new AgricultureCommand(this));
        this.getCommand("ager").setExecutor(new AgricultureCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveBushData();

        saveBerryLikes();
        saveFruitTreeSaplings();
        saveGrowingLeaves();
        saveConfigs();
    }

    private void loadConfigs(){
        if(!configFile.exists()){
            // initialize configs
            sellMultiplier=1;
            materialBasedSellMultiplier=new HashMap<>();


            try {
                configFile.createNewFile();
            } catch (Exception e){
                e.printStackTrace();
            }
            return;
        }
        sellMultiplier=1;
        materialBasedSellMultiplier=new HashMap<>();


    }

    private void saveConfigs(){

    }

    private void saveBushData() {
        List<Bush> bushList = bushMap.values()
                .stream()
                .toList();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(bushFile)) {
            gson.toJson(bushList, writer);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void loadBushData(){
        if(!bushFile.exists()){
             bushMap = new ConcurrentHashMap<>();
             if(!getDataFolder().exists()){
                try {
                    getDataFolder().mkdir();
                } catch (Exception e){
                    e.printStackTrace();
                }
             }
             try {
                 bushFile.createNewFile();
             } catch (Exception e){
                 e.printStackTrace();
             }
             return;
        }

        Gson gson = new Gson();
        try (Reader reader = new FileReader(bushFile)) {
            Type listType = new TypeToken<List<Bush>>() {}.getType();
            List<Bush> list = gson.fromJson(reader, listType);
            bushMap = new ConcurrentHashMap<>();
            if(list!=null) {
                for(Bush bush : list){
                    bushMap.put(bush.toLocation(),bush);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveBerryLikes(){
        List<BerryLike> berryList = berryMap.values()
                .stream()
                .toList();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(berryFile)) {
            gson.toJson(berryList, writer);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadBerryLikes(){
        if(!berryFile.exists()){
            berryMap = new HashMap<>();
            try {
                berryFile.createNewFile();
            } catch (Exception e){
                e.printStackTrace();
            }
            return;
        }

        Gson gson = new Gson();
        try (Reader reader = new FileReader(berryFile)) {
            Type listType = new TypeToken<List<BerryLike>>() {}.getType();
            List<BerryLike> list = gson.fromJson(reader, listType);
            berryMap = new HashMap<>();
            if(list!=null) {
                for(BerryLike berry : list){
                    berryMap.put(berry.toLocation(),berry);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveFruitTreeSaplings(){
        List<FruitTreeSapling> saplingList = saplingMap.values()
                .stream()
                .toList();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(saplingFile)) {
            gson.toJson(saplingList, writer);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadFruitTreeSaplings(){
        if(!saplingFile.exists()){
            saplingMap = new HashMap<>();
            try {
                saplingFile.createNewFile();
            } catch (Exception e){
                e.printStackTrace();
            }
            return;
        }

        Gson gson = new Gson();
        try (Reader reader = new FileReader(saplingFile)) {
            Type listType = new TypeToken<List<BerryLike>>() {}.getType();
            List<FruitTreeSapling> list = gson.fromJson(reader, listType);
            saplingMap = new HashMap<>();
            if(list!=null) {
                for(FruitTreeSapling sapling : list){
                    saplingMap.put(sapling.toLocation(),sapling);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveGrowingLeaves(){
        List<FruitGrowingLeaves> growingLeaves = growingLeavesMap.values()
                .stream()
                .toList();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(growingLeavesFile)) {
            gson.toJson(growingLeaves, writer);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadGrowingLeaves(){
        if(!growingLeavesFile.exists()){
            growingLeavesMap = new ConcurrentHashMap<>();
            try {
                growingLeavesFile.createNewFile();
            } catch (Exception e){
                e.printStackTrace();
            }
            return;
        }

        Gson gson = new Gson();
        try (Reader reader = new FileReader(growingLeavesFile)) {
            Type listType = new TypeToken<List<FruitGrowingLeaves>>() {}.getType();
            List<FruitGrowingLeaves> list = gson.fromJson(reader, listType);
            growingLeavesMap = new ConcurrentHashMap<>();
            if(list!=null) {
                for(FruitGrowingLeaves leaves : list){
                    growingLeavesMap.put(leaves.toLocation(),leaves);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void printStructures(){
        copyStructureToWorld("oak_fruit_tree_large_1.nbt");
        copyStructureToWorld("oak_fruit_tree_large_2.nbt");
        copyStructureToWorld("oak_fruit_tree_large_3.nbt");
        copyStructureToWorld("oak_fruit_tree_small_1.nbt");
        copyStructureToWorld("oak_fruit_tree_small_2.nbt");
        copyStructureToWorld("oak_fruit_tree_small_3.nbt");
    }

    public void copyStructureToWorld(String structureFileName) {
        File worldFolder=Bukkit.getWorlds().getFirst().getWorldFolder();
        InputStream in = getClass().getResourceAsStream("/schematics/" + structureFileName);
        if (in == null) {
            Bukkit.getLogger().warning("Failed to find structure resource: " + structureFileName);
            return;
        }


        File dest = new File(worldFolder, "generated/corvidagriculture/structures/" + structureFileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        try (OutputStream out = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}