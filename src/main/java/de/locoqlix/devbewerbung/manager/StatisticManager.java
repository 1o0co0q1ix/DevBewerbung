package de.locoqlix.devbewerbung.manager;

import de.locoqlix.devbewerbung.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

public class StatisticManager {
    private final Main plugin;
    private Queue<Long> redstoneUpdateTimestamps;
    private Queue<Long> blockActivityTimestamps;
    private final long FIVE_MINUTES_MS = 5 * 60 * 1000;
    private BukkitTask blockActivityCleanerTask;

    // --- Für eigene TPS-Berechnung ---
    public static final int TPS_SAMPLE_SIZE = 100; // Anzahl der letzten Tick-Zeiten für Durchschnitt
    private final Deque<Long> tickTimestamps = new ConcurrentLinkedDeque<>();
    private BukkitTask tpsSamplerTask;
    private double currentTPS = 20.0;


    public StatisticManager(Main plugin) {
        this.plugin = plugin;
        this.redstoneUpdateTimestamps = new LinkedList<>();
        this.blockActivityTimestamps = new LinkedList<>();
        startBlockActivityCleaner();
        startTPSSampler();
    }

    // --- Datensammlungsmethoden ---
    public void recordRedstoneUpdate() {
        synchronized (redstoneUpdateTimestamps) {
            redstoneUpdateTimestamps.add(System.currentTimeMillis());
        }
    }

    public void recordBlockActivity() {
        synchronized (blockActivityTimestamps) {
            blockActivityTimestamps.add(System.currentTimeMillis());
        }
    }

    private void startBlockActivityCleaner() {
        blockActivityCleanerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::cleanOldBlockActivities, 20L * 60, 20L * 60);
    }

    private void cleanOldBlockActivities() {
        long currentTime = System.currentTimeMillis();
        synchronized (blockActivityTimestamps) {
            while (!blockActivityTimestamps.isEmpty() && blockActivityTimestamps.peek() < (currentTime - FIVE_MINUTES_MS)) {
                blockActivityTimestamps.poll();
            }
        }
    }

    private void cleanOldRedstoneUpdates() {
        long currentTime = System.currentTimeMillis();
        synchronized (redstoneUpdateTimestamps) {
            while (!redstoneUpdateTimestamps.isEmpty() && redstoneUpdateTimestamps.peek() < (currentTime - FIVE_MINUTES_MS)) {
                redstoneUpdateTimestamps.poll();
            }
        }
    }

    private void startTPSSampler() {
        tpsSamplerTask = new BukkitRunnable() {
            long lastTickTime = System.nanoTime();

            @Override
            public void run() {
                long currentTime = System.nanoTime();
                long timeSinceLastTick = currentTime - lastTickTime;
                lastTickTime = currentTime;

                tickTimestamps.addLast(timeSinceLastTick);
                if (tickTimestamps.size() > TPS_SAMPLE_SIZE) {
                    tickTimestamps.removeFirst();
                }
                calculateCurrentTPS();
            }
        }.runTaskTimer(plugin, 0L, 1L); // Läuft jeden Tick
    }

    private void calculateCurrentTPS() {
        if (tickTimestamps.isEmpty()) {
            currentTPS = 20.0;
            return;
        }
        List<Long> currentSamples = new ArrayList<>(tickTimestamps); // Kopie für sichere Iteration
        if (currentSamples.isEmpty()) {
            currentTPS = 20.0;
            return;
        }
        long totalTickTimeNano = 0;
        for (Long tickTime : currentSamples) {
            totalTickTimeNano += tickTime;
        }
        double averageTickTimeNano = (double) totalTickTimeNano / currentSamples.size();
        double tps = 1_000_000_000.0 / averageTickTimeNano; // 1 Sekunde in Nanosekunden
        currentTPS = Math.min(20.0, tps); // TPS kann nicht über 20 sein
    }

    public void shutdown() {
        if (blockActivityCleanerTask != null && !blockActivityCleanerTask.isCancelled()) {
            blockActivityCleanerTask.cancel();
        }
        if (tpsSamplerTask != null && !tpsSamplerTask.isCancelled()) {
            tpsSamplerTask.cancel();
        }
    }

    // --- Datenabrufmethoden ---
    public Map<EntityType, Integer> getCurrentMobCounts() {
        Map<EntityType, Integer> mobCounts = new HashMap<>();
        for (World world : Bukkit.getWorlds()) {
            for (LivingEntity entity : world.getLivingEntities()) {
                if (!(entity instanceof Player)) {
                    mobCounts.put(entity.getType(), mobCounts.getOrDefault(entity.getType(), 0) + 1);
                }
            }
        }
        return mobCounts;
    }

    public int getRedstoneUpdatesLast5Min() {
        cleanOldRedstoneUpdates();
        synchronized (redstoneUpdateTimestamps) {
            return redstoneUpdateTimestamps.size();
        }
    }

    public int getBlockActivityLast5Min() {
        cleanOldBlockActivities();
        synchronized (blockActivityTimestamps) {
            return blockActivityTimestamps.size();
        }
    }

    public int getItemEntityCount() {
        int count = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Item) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getPlayerCount() {
        return Bukkit.getOnlinePlayers().size();
    }

    public List<String> getWorldNames() {
        return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
    }

    public int getWorldCount() {
        return Bukkit.getWorlds().size();
    }

    public double getCurrentTPS() {
        return this.currentTPS;
    }

    public long getMaxRamMB() {
        return Runtime.getRuntime().maxMemory() / (1024 * 1024);
    }

    public long getAllocatedRamMB() {
        return Runtime.getRuntime().totalMemory() / (1024 * 1024);
    }

    public long getUsedRamMB() {
        Runtime r = Runtime.getRuntime();
        return (r.totalMemory() - r.freeMemory()) / (1024 * 1024);
    }

    public int getTotalLoadedChunks() {
        int totalChunks = 0;
        for (World world : Bukkit.getWorlds()) {
            totalChunks += world.getLoadedChunks().length;
        }
        return totalChunks;
    }

    public int getTotalEntityCount() {
        int totalEntities = 0;
        for (World world : Bukkit.getWorlds()) {
            totalEntities += world.getEntities().size();
        }
        return totalEntities;
    }

    public int getAveragePing() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        if (onlinePlayers.isEmpty()) {
            return -1; // Keine Spieler online
        }
        double totalPing = 0;
        int playerCountWithPing = 0;
        for (Player player : onlinePlayers) {
            try {
                // player.getPing() ist seit Spigot 1.17 verfügbar.
                // Deine plugin.yml hat api-version: 1.21, also sollte das funktionieren.
                int ping = player.getPing();
                if (ping >= 0) { // Manchmal kann Ping negativ sein, wenn nicht verfügbar
                    totalPing += ping;
                    playerCountWithPing++;
                }
            } catch (NoSuchMethodError e) {
                // Sollte mit API 1.21 nicht passieren.
                // Gibt -2 zurück, um anzuzeigen, dass die Methode nicht verfügbar ist (z.B. bei älteren Versionen)
                return -2;
            }
        }
        return playerCountWithPing > 0 ? (int) (totalPing / playerCountWithPing) : -1; // -1 wenn keine Pings erfasst wurden
    }
}