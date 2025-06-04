package com.corvidina.corvidAgriculture.commands;

import com.corvidina.corvidAgriculture.Bush;
import com.corvidina.corvidAgriculture.CorvidAgriculture;
import com.corvidina.corvidAgriculture.FruitGrowingLeaves;
import com.corvidina.corvidAgriculture.FruitTreeSapling;
import com.corvidina.corvidAgriculture.entities.Crow;
import com.corvidina.corvidAgriculture.entities.HexMarker;
import com.corvidina.corvidAgriculture.entities.Insect;
import com.corvidina.corvidAgriculture.items.Crops;
import com.corvidina.corvidAgriculture.items.ItemHandler;
import com.corvidina.corvidAgriculture.items.Seeds;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class TestCommand
    implements CommandExecutor
{
    private FruitTreeSapling sapling;
    private static final CorvidAgriculture plugin = CorvidAgriculture.getPlugin(CorvidAgriculture.class);
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        //Logic here
        if(strings.length==0){
            commandSender.sendMessage("No secondary argument");
            return false;
        }

        //Logic for if commandSender is a player
        if(commandSender instanceof Player player) {
            switch (strings[0]){
                case "crow": {
                    Crow crow = new Crow(((CraftWorld)player.getWorld()).getHandle());
                    crow.setPos(player.getX(),player.getY()+0.75,player.getZ());
                    ((CraftWorld) player.getWorld()).addEntity(crow, CreatureSpawnEvent.SpawnReason.CUSTOM);
                    break;
                }
                case "insect": {
                    Insect insect = new Insect(((CraftWorld)player.getWorld()).getHandle());
                    insect.setPos(player.getX(),player.getY()+0.75,player.getZ());
                    ((CraftWorld) player.getWorld()).addEntity(insect, CreatureSpawnEvent.SpawnReason.CUSTOM);
                    break;
                }
                case "hex_marker": {
                    HexMarker hexMarker = new HexMarker(((CraftWorld)player.getWorld()).getHandle());
                    hexMarker.setPos(player.getX(),player.getY(),player.getZ());
                    ((CraftWorld)player.getWorld()).addEntity(hexMarker,CreatureSpawnEvent.SpawnReason.CUSTOM);
                    break;
                }
                case "bush": {
                    Bush.buildBush(player.getLocation(), Crops.CRANBERRY,true);
                    break;
                }
                case "tree": {
                    sapling = FruitTreeSapling.buildFruitTreeSapling(player.getLocation(), Crops.LEMON);
                    break;
                }
                case "tree_test": {
                    FruitTreeSapling.growSapling(sapling);
                    break;
                }
                case "fruit_leaves": {
                    player.getLocation().getBlock().setType(Material.OAK_LEAVES);
                    FruitGrowingLeaves fruitGrowingLeaves = new FruitGrowingLeaves(player.getLocation(), Crops.LEMON);
                    fruitGrowingLeaves.incrementAge(7);
                    plugin.getGrowingLeavesMap().put(fruitGrowingLeaves.toLocation(),fruitGrowingLeaves);
                }
                case "accelerate_fruit_leaves": {
                    for(FruitGrowingLeaves growingLeaves : plugin.getGrowingLeavesMap().values()){
                        growingLeaves.incrementAge(7);
                    }
                    break;
                }
                case "seeds": {
                    if(strings.length>1) {
                        player.getInventory().addItem(ItemHandler.buildItemBkt(Seeds.valueOf(strings[1].toUpperCase())));
                    }
                    break;
                }
                default: {
                    commandSender.sendMessage("Invalid secondary argument");
                    return true;
                }
            }
            return true;
        }
        return false;
    }
}
