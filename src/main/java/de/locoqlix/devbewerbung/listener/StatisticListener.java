package de.locoqlix.devbewerbung.listener;

import de.locoqlix.devbewerbung.manager.StatisticManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

public class StatisticListener implements Listener {

    private final StatisticManager statisticManager;

    public StatisticListener(StatisticManager statisticManager) {
        this.statisticManager = statisticManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRedstoneUpdate(BlockRedstoneEvent event) {
        statisticManager.recordRedstoneUpdate(); // Geändert von incrementRedstoneUpdates
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        statisticManager.recordBlockActivity();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        statisticManager.recordBlockActivity();
    }
}