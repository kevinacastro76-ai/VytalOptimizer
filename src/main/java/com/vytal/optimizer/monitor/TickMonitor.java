package com.vytal.optimizer.monitor;

import com.vytal.optimizer.VytalOptimizer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Measures the real elapsed interval between executions of a synchronous
 * repeating task. This is an approximation of tick interval, not a profiler
 * and not a direct measurement of the whole server tick's MSPT.
 */
public final class TickMonitor {
    private final VytalOptimizer plugin;
    private final AtomicLong lastRunNanos = new AtomicLong(-1L);
    private final AtomicLong lastIntervalNanos = new AtomicLong(-1L);
    private BukkitTask task;

    public TickMonitor(VytalOptimizer plugin) {
        this.plugin = plugin;
    }

    public void start() {
        long interval = Math.max(1L, plugin.getConfig().getLong("settings.sampling-interval-ticks", 20L));
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long now = System.nanoTime();
            long previous = lastRunNanos.getAndSet(now);
            if (previous > 0L) {
                lastIntervalNanos.set(now - previous);
            }
        }, 1L, interval);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public double getApproximateIntervalMs() {
        long value = lastIntervalNanos.get();
        return value < 0L ? -1.0 : value / 1_000_000.0;
    }

    public double getApproximateTps() {
        double interval = getApproximateIntervalMs();
        if (interval < 0.0) return -1.0;
        return Math.min(20.0, 1000.0 / Math.max(1.0, interval));
    }
                                                  }
