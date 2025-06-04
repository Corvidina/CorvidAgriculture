package com.corvidina.corvidAgriculture;

import org.bukkit.scheduler.BukkitRunnable;

public class CateringUpdater extends BukkitRunnable {
    private static final CorvidAgriculture plugin = CorvidAgriculture.getPlugin(CorvidAgriculture.class);
    @Override
    public void run() {
        plugin.getServerCateringView().reloadCaterings();
    }
}
