package com.corvidina.corvidAgriculture.commands;

import com.corvidina.corvidAgriculture.CorvidAgriculture;
import com.corvidina.corvidAgriculture.gui.AgricultureMenu;
import com.corvidina.corvidAgriculture.items.CorvidAgricultureItems;
import me.devnatan.inventoryframework.AnvilInputFeature;
import me.devnatan.inventoryframework.ViewFrame;
import me.lucko.spark.paper.proto.SparkProtos;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class AgricultureCommand
    implements CommandExecutor
{
    private final CorvidAgriculture plugin;
    public AgricultureCommand(CorvidAgriculture plugin){
        this.plugin=plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(commandSender instanceof Player){
            final Player player = (Player)commandSender;

            ViewFrame viewFrame = ViewFrame.create(plugin).
                    install(AnvilInputFeature.AnvilInput).
                    with(new AgricultureMenu()).
                    register();
            viewFrame.open(AgricultureMenu.class, player);

        }

        return true;
    }
}
