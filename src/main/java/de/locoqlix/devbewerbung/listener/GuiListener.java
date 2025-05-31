package de.locoqlix.devbewerbung.listener;

import de.locoqlix.devbewerbung.manager.GuiManager;
import de.locoqlix.devbewerbung.manager.StatisticManager; // Nicht mehr direkt benötigt
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class GuiListener implements Listener {

    private final GuiManager guiManager;

    public GuiListener(GuiManager guiManager, StatisticManager statisticManager) {
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        InventoryView view = event.getView();
        String inventoryTitle = view.getTitle();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        // --- Genereller "Zurück"-Button-Handler ---
        if (pdc.has(GuiManager.BACK_BUTTON_KEY, PersistentDataType.STRING)) {
            String targetGui = pdc.get(GuiManager.BACK_BUTTON_KEY, PersistentDataType.STRING);
            if ("main_gui".equals(targetGui)) {
                event.setCancelled(true);
                player.closeInventory(); // Schließen, bevor neues geöffnet wird, um Flackern zu vermeiden
                guiManager.openMainStatsGui(player);
                return;
            }
        }

        // --- Haupt-Statistik-GUI ---
        if (inventoryTitle.equals(GuiManager.MAIN_STATS_GUI_TITLE)) {
            event.setCancelled(true);
            if (pdc.has(GuiManager.CATEGORY_KEY, PersistentDataType.STRING)) {
                String category = pdc.get(GuiManager.CATEGORY_KEY, PersistentDataType.STRING);
                if (category == null) return;

                player.closeInventory();
                switch (category) {
                    case "mobs":
                        guiManager.openMobsGui(player, 1); // Starte immer auf Seite 1
                        break;
                    case "blocks":
                        guiManager.openBlockStatsGui(player);
                        break;
                    case "redstone":
                        guiManager.openRedstoneStatsGui(player);
                        break;
                    case "general":
                        guiManager.openGeneralStatsGui(player);
                        break;
                }
            }
        }
        // --- Mob-Statistik-GUI (Paginierung) ---
        else if (inventoryTitle.startsWith(GuiManager.MOB_STATS_GUI_TITLE_PREFIX)) {
            event.setCancelled(true);
            if (pdc.has(GuiManager.PAGE_ACTION_KEY, PersistentDataType.STRING) &&
                    pdc.has(GuiManager.CURRENT_PAGE_KEY, PersistentDataType.INTEGER)) {

                String action = pdc.get(GuiManager.PAGE_ACTION_KEY, PersistentDataType.STRING);
                int currentPage = pdc.get(GuiManager.CURRENT_PAGE_KEY, PersistentDataType.INTEGER);

                if ("next".equals(action)) {
                    player.closeInventory();
                    guiManager.openMobsGui(player, currentPage + 1);
                } else if ("prev".equals(action)) {
                    player.closeInventory();
                    guiManager.openMobsGui(player, currentPage - 1);
                }
            }
            // Klick auf ein Mob-Item selbst macht aktuell nichts
        }
        // --- Andere Statistik-GUIs (nur Cancel, da Zurück-Button oben behandelt wird) ---
        else if (inventoryTitle.equals(GuiManager.BLOCK_STATS_GUI_TITLE) ||
                inventoryTitle.equals(GuiManager.REDSTONE_STATS_GUI_TITLE) ||
                inventoryTitle.equals(GuiManager.GENERAL_STATS_GUI_TITLE)) {
            event.setCancelled(true);
        }
    }
}