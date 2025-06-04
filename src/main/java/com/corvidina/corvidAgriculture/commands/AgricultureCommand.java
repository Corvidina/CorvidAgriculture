package com.corvidina.corvidAgriculture.commands;

import com.corvidina.corvidAgriculture.CorvidAgriculture;
import com.corvidina.corvidAgriculture.gui.AgricultureMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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

            AgricultureMenu.initAgricultureMenu().open(AgricultureMenu.class,player);
            
        }



        return true;
    }
}
