package com.vytal.optimizer.command;

import com.vytal.optimizer.VytalOptimizer;
import com.vytal.optimizer.analyzer.ServerAnalyzer;
import com.vytal.optimizer.monitor.TickMonitor;
import com.vytal.optimizer.util.Colors;
import org.bukkit.command.*;

import java.util.ArrayList;
import java.util.List;

public final class OptimizerCommand implements TabExecutor {
    private final VytalOptimizer plugin;
    private final ServerAnalyzer analyzer;

    public OptimizerCommand(VytalOptimizer plugin, TickMonitor tickMonitor) {
        this.plugin = plugin;
        this.analyzer = new ServerAnalyzer(plugin, tickMonitor);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("vytaloptimizer.admin")) {
            Colors.line(sender, "&cNo tienes permiso para utilizar este comando.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("ayuda") || args[0].equalsIgnoreCase("help")) {
            help(sender, label);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "analizar", "analyze" -> {
                Colors.line(sender, "&aIniciando diagnóstico...");
                analyzer.printFullReport(sender);
                Colors.line(sender, "&aDiagnóstico completado. Revisa las advertencias y recomendaciones.");
            }
            case "plugins" -> analyzer.printPluginsAndTasks(sender);
            case "tps" -> analyzer.printTps(sender);
            case "memoria", "ram" -> analyzer.printMemory(sender);
            case "entidades", "mundos", "chunks" -> analyzer.printWorlds(sender);
            case "reporte", "report" -> analyzer.printFullReport(sender);
            default -> Colors.line(sender, "&cSubcomando desconocido. Usa &f/" + label + " ayuda&c.");
        }
        return true;
    }

    private void help(CommandSender sender, String label) {
        Colors.line(sender, "&8&m----------------------------------------");
        Colors.line(sender, "&b&lVytalOptimizer &7- Diagnóstico del servidor");
        Colors.line(sender, "&f/" + label + " analizar &8- &7Análisis general");
        Colors.line(sender, "&f/" + label + " plugins &8- &7Plugins y tareas pendientes");
        Colors.line(sender, "&f/" + label + " tps &8- &7TPS e intervalo de ticks");
        Colors.line(sender, "&f/" + label + " memoria &8- &7Memoria JVM");
        Colors.line(sender, "&f/" + label + " entidades &8- &7Entidades y chunks por mundo");
        Colors.line(sender, "&f/" + label + " reporte &8- &7Informe completo");
        Colors.line(sender, "&8&m----------------------------------------");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("vytaloptimizer.admin")) return List.of();
        if (args.length == 1) {
            List<String> options = List.of("analizar", "plugins", "tps", "memoria", "entidades", "reporte", "ayuda");
            String prefix = args[0].toLowerCase();
            List<String> matches = new ArrayList<>();
            for (String option : options) if (option.startsWith(prefix)) matches.add(option);
            return matches;
        }
        return List.of();
    }
  }
            
