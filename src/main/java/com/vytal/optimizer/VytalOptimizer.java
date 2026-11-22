package com.vytal.optimizer;

import com.vytal.optimizer.command.OptimizerCommand;
import com.vytal.optimizer.monitor.TickMonitor;
import org.bukkit.plugin.java.JavaPlugin;

public final class VytalOptimizer extends JavaPlugin {
    private TickMonitor tickMonitor;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        tickMonitor = new TickMonitor(this);
        tickMonitor.start();

        OptimizerCommand command = new OptimizerCommand(this, tickMonitor);
        if (getCommand("vytaloptimizer") != null) {
            getCommand("vytaloptimizer").setExecutor(command);
            getCommand("vytaloptimizer").setTabCompleter(command);
        }

        getLogger().info("VytalOptimizer habilitado. Usa /vo ayuda.");
    }

    @Override
    public void onDisable() {
        if (tickMonitor != null) {
            tickMonitor.stop();
        }
        getLogger().info("VytalOptimizer deshabilitado.");
    }
}
