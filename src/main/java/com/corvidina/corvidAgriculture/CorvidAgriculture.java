package com.corvidina.corvidAgriculture;

import com.corvidina.corvidAgriculture.commands.AgricultureCommand;
import com.corvidina.corvidAgriculture.commands.SpawnCustomEntity;
import com.corvidina.corvidAgriculture.gui.ServerCatering;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import com.corvidina.corvidAgriculture.listeners.CactusGrows;
import com.corvidina.corvidAgriculture.listeners.PlayerBreaksBlock;
import me.devnatan.inventoryframework.AnvilInputFeature;
import me.devnatan.inventoryframework.ViewFrame;
import org.bukkit.plugin.java.JavaPlugin;

public final class CorvidAgriculture extends JavaPlugin {
    public static ViewFrame cateringMenu;
    private ServerCatering serverCatering;

    private int cateringAdditionRate = 60;
    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Hello world from Corvid Agriculture!");

        registerCommands();
        registerListeners();

        ItemHandler.sellMultiplier=2;

        cateringMenu = ViewFrame.create(this);
        serverCatering =new ServerCatering();
        cateringMenu.install(AnvilInputFeature.AnvilInput);
        cateringMenu.with(serverCatering);
        cateringMenu.register();
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
        getServer().getPluginManager().registerEvents(new PlayerBreaksBlock(), this);
        getServer().getPluginManager().registerEvents(new CactusGrows(),this);
    }

    private void registerCommands(){
        this.getCommand("spawncustomentity").setExecutor(new SpawnCustomEntity());
        this.getCommand("agriculture").setExecutor(new AgricultureCommand(this));
        this.getCommand("ager").setExecutor(new AgricultureCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }
}
