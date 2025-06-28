package com.corvidina.corvidAgriculture;

import com.corvidina.corvidAgriculture.simulations.ExecutableSimulation;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Queue;

public class TickHandler extends BukkitRunnable {
    private static final CorvidAgriculture plugin = CorvidAgriculture.getPlugin(CorvidAgriculture.class);

    @Override
    public void run(){
        Queue<ExecutableSimulation> queue = plugin.getExecutableSimulationQueue();

        for (ExecutableSimulation executable = queue.poll(); executable!=null ; executable = queue.poll()) {
            executable.run();
        }


    }

}
