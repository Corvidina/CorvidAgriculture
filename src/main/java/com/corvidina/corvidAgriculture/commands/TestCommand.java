package com.corvidina.corvidAgriculture.commands;

import com.corvidina.corvidAgriculture.*;
import com.corvidina.corvidAgriculture.entities.Crow;
import com.corvidina.corvidAgriculture.entities.HexMarker;
import com.corvidina.corvidAgriculture.entities.Insect;
import com.corvidina.corvidAgriculture.items.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class TestCommand
    implements CommandExecutor
{
    private Rice rice;
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
                    Bush.buildBush(player.getLocation(), Crop.CRANBERRY,true);
                    break;
                }
                case "rice": {
                    rice = Rice.buildRice(player.getLocation());
                    break;
                }
                case "rice_test": {
                    Rice.growRice(rice);
                    break;
                }
                case "fruit_leaves": {
                    player.getLocation().getBlock().setType(Material.OAK_LEAVES);
                    FruitGrowingLeaves fruitGrowingLeaves = new FruitGrowingLeaves(player.getLocation(), Crop.LEMON);
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
                    } else {
                        commandSender.sendMessage("Failed to have a type argument");
                    }
                    break;
                }
                case "dough": {
                    player.getInventory().addItem(ItemHandler.buildItemBkt(HelperIngredient.DOUGH));
                    break;
                }
                case "accelerate_bushes": {
                    for (Location loc : plugin.getBushMap().keySet()) {
                        if(loc.isChunkLoaded()) {
                            Bush.growBerry(plugin.getBushMap().get(loc));
                        }
                    }
                    break;
                }
                case "add_replanter": {
                    if(ItemHandler.itemIsHoe(player.getInventory().getItem(EquipmentSlot.HAND).getType())){
                        ItemStack stack = player.getInventory().getItem(EquipmentSlot.HAND);
                        stack = ItemHandler.addZealToHoe(stack, Zeal.REPLANTER);
                        player.getInventory().setItem(EquipmentSlot.HAND,stack);
                    }
                    break;
                }
                case "print_zeals": {
                    HashMap<Zeal, Integer> map = ItemHandler.getZeals(player.getInventory().getItem(EquipmentSlot.HAND));
                    for (Zeal zeal : map.keySet()) {
                        Bukkit.getLogger().info(zeal.toString() + " : " + map.get(zeal));
                    }
                    break;
                }
                case "print_replanter": {
                    Bukkit.getLogger().info(""+ItemHandler.getZeal(player.getInventory().getItem(EquipmentSlot.HAND), Zeal.REPLANTER));
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
