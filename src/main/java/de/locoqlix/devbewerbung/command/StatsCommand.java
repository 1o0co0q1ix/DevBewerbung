package de.locoqlix.devbewerbung.command;

import de.locoqlix.devbewerbung.manager.GuiManager; // NEU
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// import java.util.List; // Nicht mehr für direkte Chat-Ausgabe benötigt

public class StatsCommand implements CommandExecutor {

    private final GuiManager guiManager; // NEU

    public StatsCommand(GuiManager guiManager) { // Angepasst
        this.guiManager = guiManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Dieser Befehl kann nur von einem Spieler ausgeführt werden, um die GUI anzuzeigen.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("serverstats.use")) {
            player.sendMessage(ChatColor.RED + "Du hast keine Berechtigung, diesen Befehl zu verwenden.");
            return true;
        }

        guiManager.openMainStatsGui(player); // Öffnet die Haupt-GUI
        return true;
    }
}