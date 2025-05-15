package com.corvidina.corvidAgriculture.commands;

import com.corvidina.corvidAgriculture.items.CorvidAgricultureItems;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AgricultureCommand
    implements CommandExecutor
{

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(commandSender instanceof Player){
            Player player = (Player)commandSender;
            player.give((ItemHandler.buildItem(CorvidAgricultureItems.WHEAT)).asBukkitCopy());
        }

        return true;
    }
}
