package com.corvidina.corvidAgriculture.commands;

import com.corvidina.corvidAgriculture.entities.Crow;
import com.corvidina.corvidAgriculture.entities.HexMarker;
import com.corvidina.corvidAgriculture.entities.Insect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

public class SpawnCustomEntity
    implements CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        //Logic here
        if(strings.length==0){
            commandSender.sendMessage("No secondary argument");
            return false;
        }


        //Logic for if commandSender is a player
        if(commandSender instanceof Player) {
            switch (strings[0]){
                case "crow": {
                    Player player=(Player)commandSender;
                    Crow crow = new Crow(((CraftWorld)player.getWorld()).getHandle());
                    crow.setPos(player.getX(),player.getY()+0.75,player.getZ());
                    ((CraftWorld) player.getWorld()).addEntity(crow, CreatureSpawnEvent.SpawnReason.CUSTOM);
                    break;
                }
                case "insect": {
                    Player player=(Player)commandSender;
                    Insect insect = new Insect(((CraftWorld)player.getWorld()).getHandle());
                    insect.setPos(player.getX(),player.getY()+0.75,player.getZ());
                    ((CraftWorld) player.getWorld()).addEntity(insect, CreatureSpawnEvent.SpawnReason.CUSTOM);
                    break;
                }
                case "hex_marker": {
                    Player player=(Player)commandSender;
                    HexMarker hexMarker = new HexMarker(((CraftWorld)player.getWorld()).getHandle());
                    hexMarker.setPos(player.getX(),player.getY(),player.getZ());
                    ((CraftWorld)player.getWorld()).addEntity(hexMarker,CreatureSpawnEvent.SpawnReason.CUSTOM);
                }
                default: {
                    commandSender.sendMessage("Invalid secondary argument");
                    return false;
                }
            }
            return true;
        }

        return false;
    }
}
