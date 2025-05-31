package de.locoqlix.devbewerbung;

import de.locoqlix.devbewerbung.command.StatsCommand;
import de.locoqlix.devbewerbung.listener.GuiListener; // NEU
import de.locoqlix.devbewerbung.listener.StatisticListener;
import de.locoqlix.devbewerbung.manager.GuiManager; // NEU
import de.locoqlix.devbewerbung.manager.StatisticManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private StatisticManager statisticManager;
    private GuiManager guiManager; // NEU

    @Override
    public void onEnable() {
        getLogger().info("ServerStatsPlugin wird aktiviert!");

        // Manager initialisieren
        this.statisticManager = new StatisticManager(this);
        this.guiManager = new GuiManager(this, this.statisticManager); // NEU

        // Listener registrieren
        getServer().getPluginManager().registerEvents(new StatisticListener(this.statisticManager), this);
        getServer().getPluginManager().registerEvents(new GuiListener(this.guiManager, this.statisticManager), this); // NEU

        // Command registrieren
        // Der Command braucht jetzt den GuiManager statt direkt den StatisticManager für die Ausgabe
        getCommand("serverstats").setExecutor(new StatsCommand(this.guiManager)); // Angepasst


        // Optional: Standardpermission setzen, falls keine Permissions-Plugin vorhanden ist
        // if (getServer().getPluginManager().getPermission("serverstats.use") == null) {
        //     getServer().getPluginManager().addPermission(new org.bukkit.permissions.Permission("serverstats.use", org.bukkit.permissions.PermissionDefault.OP));
        // }
    }

    @Override
    public void onDisable() {
        getLogger().info("ServerStatsPlugin wird deaktiviert!");
        if (statisticManager != null) {
            statisticManager.shutdown();
        }
    }

    // Getter werden nicht mehr unbedingt benötigt, da Manager jetzt untereinander kommunizieren oder übergeben werden.
    // public StatisticManager getStatisticManager() {
    //     return statisticManager;
    // }
    // public GuiManager getGuiManager() {
    //     return guiManager;
    // }
}