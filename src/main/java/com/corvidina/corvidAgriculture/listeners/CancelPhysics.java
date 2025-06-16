package com.corvidina.corvidAgriculture.listeners;

import com.corvidina.corvidAgriculture.CorvidAgriculture;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

public class CancelPhysics implements Listener {
    private static final CorvidAgriculture plugin = CorvidAgriculture.getPlugin(CorvidAgriculture.class);
    @EventHandler
    public void onPhysics(BlockPhysicsEvent event) {
        if (event.getBlock().getType() == Material.FERN){
            if (plugin.getBerryLikeMap().containsKey(event.getBlock().getLocation())) {
                event.setCancelled(true);
            }
        }
        if (event.getBlock().getType() == Material.KELP_PLANT) {
            if (plugin.getRiceMap().containsKey(event.getBlock().getLocation())) {
                event.setCancelled(true);
            }
        }
    }
}
