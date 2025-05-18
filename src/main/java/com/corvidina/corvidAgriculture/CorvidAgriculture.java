package com.corvidina.corvidAgriculture;

import com.corvidina.corvidAgriculture.commands.AgricultureCommand;
import com.corvidina.corvidAgriculture.commands.SpawnCustomEntity;
import com.corvidina.corvidAgriculture.gui.AgricultureMenu;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import com.corvidina.corvidAgriculture.listeners.PlayerBreaksBlock;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.runtime.InventoryFramework;
import org.bukkit.plugin.java.JavaPlugin;

public final class CorvidAgriculture extends JavaPlugin {
    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Hello world from Corvid Agriculture!");

        registerCommands();
        registerListeners();

        ItemHandler.sellMultiplier=2;
    }



    private void registerListeners(){
        getServer().getPluginManager().registerEvents(new PlayerBreaksBlock(), this);
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
