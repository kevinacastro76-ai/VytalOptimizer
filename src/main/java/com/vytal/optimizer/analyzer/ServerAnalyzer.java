package com.vytal.optimizer.analyzer;

import com.vytal.optimizer.VytalOptimizer;
import com.vytal.optimizer.monitor.TickMonitor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.*;

public final class ServerAnalyzer {
    private final VytalOptimizer plugin;
    private final TickMonitor tickMonitor;

    public ServerAnalyzer(VytalOptimizer plugin, TickMonitor tickMonitor) {
        this.plugin = plugin;
        this.tickMonitor = tickMonitor;
    }

    public void printFullReport(org.bukkit.command.CommandSender sender) {
        header(sender, "DIAGNÓSTICO GENERAL");
        printTps(sender);
        printMemory(sender);
        printWorlds(sender);
        printPluginsAndTasks(sender);
        sender.sendMessage(" ");
        sender.sendMessage("§8§m----------------------------------------");
        sender.sendMessage("§7VytalOptimizer §8| §7Diagnóstico de solo lectura");
        sender.sendMessage("§8§m----------------------------------------");
    }

    public void printTps(org.bukkit.command.CommandSender sender) {
        header(sender, "RENDIMIENTO / TICK");
        double[] nativeTps = readTps();
        if (nativeTps != null && nativeTps.length >= 3) {
            sender.sendMessage("§7TPS (1m / 5m / 15m): §f" +
                    format(nativeTps[0]) + "§7 / §f" + format(nativeTps[1]) + "§7 / §f" + format(nativeTps[2]));
        } else {
            sender.sendMessage("§7TPS nativos: §eNo expuestos por esta plataforma.");
        }

        double interval = tickMonitor.getApproximateIntervalMs();
        if (interval >= 0) {
            sender.sendMessage("§7Intervalo aproximado del monitor: §f" + format(interval) + " ms");
            sender.sendMessage("§8  No equivale a MSPT medido por un profiler.");
        } else {
            sender.sendMessage("§7Intervalo aproximado: §eRecolectando primera muestra...");
        }

        Double averageTick = readAverageTickTime();
        if (averageTick != null) {
            sender.sendMessage("§7Tiempo medio de tick reportado por servidor: §f" + format(averageTick) + " ms");
        }
    }

    public void printMemory(org.bukkit.command.CommandSender sender) {
        header(sender, "MEMORIA JVM");
        Runtime runtime = Runtime.getRuntime();
        long max = runtime.maxMemory();
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        long used = total - free;
        sender.sendMessage("§7Memoria usada en heap: §f" + mb(used) + " MB");
        sender.sendMessage("§7Heap reservado: §f" + mb(total) + " MB");
        sender.sendMessage("§7Máximo heap: §f" + mb(max) + " MB");
        sender.sendMessage("§7Uso del máximo heap: " + percent(used, max));
        MemoryUsage nonHeap = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        if (nonHeap != null) sender.sendMessage("§7Memoria no-heap usada: §f" + mb(nonHeap.getUsed()) + " MB");
        sender.sendMessage("§8La API estándar no separa RAM por plugin.");
    }

    public void printWorlds(org.bukkit.command.CommandSender sender) {
        header(sender, "MUNDOS / ENTIDADES / CHUNKS");
        for (World world : Bukkit.getWorlds()) {
            int entities = world.getEntities().size();
            int chunks = world.getLoadedChunks().length;
            sender.sendMessage("§b" + world.getName() + " §8| §7Entidades: §f" + entities +
                    " §8| §7Chunks cargados: §f" + chunks);
            Map<String, Integer> entityTypes = new TreeMap<>();
            for (Entity entity : world.getEntities()) {
                entityTypes.merge(entity.getType().name(), 1, Integer::sum);
            }
            entityTypes.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(5)
                    .forEach(entry -> sender.sendMessage(" §8- §7" + entry.getKey() + ": §f" + entry.getValue()));
        }
        sender.sendMessage("§8Las cifras son instantáneas y pueden variar con jugadores y actividad.");
    }

    public void printPluginsAndTasks(org.bukkit.command.CommandSender sender) {
        header(sender, "PLUGINS Y TAREAS PENDIENTES");
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        sender.sendMessage("§7Plugins detectados: §f" + plugins.length);
        Map<String, Integer> taskCounts = new HashMap<>();
        List<BukkitTask> tasks = Bukkit.getScheduler().getPendingTasks();
        for (BukkitTask task : tasks) {
            String owner = task.getOwner() == null ? "Desconocido" : task.getOwner().getName();
            taskCounts.merge(owner, 1, Integer::sum);
        }
        sender.sendMessage("§7Tareas pendientes visibles: §f" + tasks.size());

        Arrays.stream(plugins)
                .sorted(Comparator.comparing(Plugin::getName, String.CASE_INSENSITIVE_ORDER))
                .forEach(p -> {
                    int count = taskCounts.getOrDefault(p.getName(), 0);
                    String state = p.isEnabled() ? "§aHabilitado" : "§cDeshabilitado";
                    sender.sendMessage("§8- §f" + p.getName() + " §7v" + p.getDescription().getVersion() +
                            " §8| " + state + " §8| §7Tareas pendientes: §f" + count);
                });

        sender.sendMessage("§eInterpretación: §7muchas tareas no demuestran por sí solas que un plugin cause lag.");
        sender.sendMessage("§7La API Bukkit compartida no expone tiempo de CPU exacto por plugin.");
    }

    public void printPluginList(org.bukkit.command.CommandSender sender) {
        header(sender, "PLUGINS INSTALADOS");
        for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
            sender.sendMessage((p.isEnabled() ? "§a● " : "§c● ") + "§f" + p.getName() +
                    " §7v" + p.getDescription().getVersion());
        }
        sender.sendMessage("§8Usa /vo analizar para incluir las tareas pendientes.");
    }

    private double[] readTps() {
        try {
            Object result = Bukkit.getServer().getClass().getMethod("getTPS").invoke(Bukkit.getServer());
            return result instanceof double[] values ? values : null;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }

    private Double readAverageTickTime() {
        try {
            Object result = Bukkit.getServer().getClass().getMethod("getAverageTickTime").invoke(Bukkit.getServer());
            return result instanceof Number number ? number.doubleValue() : null;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }

    private void header(org.bukkit.command.CommandSender sender, String title) {
        sender.sendMessage(" ");
        sender.sendMessage("§8§m----------------------------------------");
        sender.sendMessage("§b§lVytalOptimizer §8» §f" + title);
        sender.sendMessage("§8§m----------------------------------------");
    }

    private String percent(long used, long max) {
        if (max <= 0) return "§7N/D";
        double pct = used * 100.0 / max;
        String color = pct >= 90 ? "§c" : pct >= 75 ? "§e" : "§a";
        return color + format(pct) + "%";
    }

    private String mb(long bytes) {
        return format(bytes / 1024.0 / 1024.0);
    }

    private String format(double value) {
        return String.format(Locale.US, "%.2f", value);
    }
          }
          
