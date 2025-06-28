package com.corvidina.corvidAgriculture.simulations;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerPlacesBlockSimulation implements ExecutableSimulation {
    private Location location;
    private Material type;
    private Player player;
    private BlockPlaceEvent blockPlaceEvent;


    public PlayerPlacesBlockSimulation(Location location, Material type, Player player) throws Exception {
        this.player=player;
        this.type=type;
        this.location=location;

        this.blockPlaceEvent = new BlockPlaceEvent(
                location.getBlock(),
                location.getBlock().getState(),
                player.getTargetBlockExact((int) (player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).getValue() + 0.99)),
                new ItemStack(type),
                player,
                true,
                EquipmentSlot.HAND
        );

        Bukkit.getPluginManager().callEvent(blockPlaceEvent);
    }

    public void run(){
        if(!blockPlaceEvent.isCancelled()) {
            location.getBlock().setType(type);
        }
    }
}
