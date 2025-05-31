package de.locoqlix.devbewerbung.manager;

import de.locoqlix.devbewerbung.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag; // Wichtig!
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GuiManager {

    private final Main plugin;
    private final StatisticManager statisticManager;

    public static final NamespacedKey CATEGORY_KEY = new NamespacedKey("serverstats", "category_item");
    public static final NamespacedKey MOB_TYPE_KEY = new NamespacedKey("serverstats", "mob_type_item");
    public static final NamespacedKey BACK_BUTTON_KEY = new NamespacedKey("serverstats", "back_button");
    public static final NamespacedKey PAGE_ACTION_KEY = new NamespacedKey("serverstats", "page_action");
    public static final NamespacedKey CURRENT_PAGE_KEY = new NamespacedKey("serverstats", "current_page");

    public static final String MAIN_STATS_GUI_TITLE = ChatColor.DARK_AQUA + "Server Statistiken";
    public static final String MOB_STATS_GUI_TITLE_PREFIX = ChatColor.DARK_GREEN + "Mob Statistiken";
    public static final String BLOCK_STATS_GUI_TITLE = ChatColor.DARK_BLUE + "Block Aktivitäten";
    public static final String REDSTONE_STATS_GUI_TITLE = ChatColor.DARK_RED + "Redstone Updates";
    public static final String GENERAL_STATS_GUI_TITLE = ChatColor.GOLD + "Allgemeine Statistiken";

    private static final int MOBS_PER_PAGE = 36;

    public GuiManager(Main plugin, StatisticManager statisticManager) {
        this.plugin = plugin;
        this.statisticManager = statisticManager;
    }

    private ItemStack createBackButton() {
        ItemStack backButton = createGuiItem(Material.BARRIER, ChatColor.RED + "" + ChatColor.BOLD + "Zurück",
                Arrays.asList(ChatColor.GRAY + "Kehrt zur Hauptübersicht zurück."));
        ItemMeta meta = backButton.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(BACK_BUTTON_KEY, PersistentDataType.STRING, "main_gui");
            backButton.setItemMeta(meta);
        }
        return backButton;
    }

    public void openMainStatsGui(Player player) {
        Inventory gui = Bukkit.createInventory(player, 27, MAIN_STATS_GUI_TITLE);

        ItemStack mobsCategoryItem = createGuiItem(Material.SPAWNER, ChatColor.AQUA + "Mob Statistiken", Arrays.asList(ChatColor.GRAY + "Zeigt detaillierte Mob-Informationen an."));
        setCategory(mobsCategoryItem, "mobs");
        gui.setItem(10, mobsCategoryItem);

        ItemStack blocksCategoryItem = createGuiItem(Material.GRASS_BLOCK, ChatColor.GREEN + "Block Aktivitäten", Arrays.asList(ChatColor.GRAY + "Zeigt platzierte/abgebaute Blöcke an."));
        setCategory(blocksCategoryItem, "blocks");
        gui.setItem(12, blocksCategoryItem);

        ItemStack redstoneCategoryItem = createGuiItem(Material.REDSTONE, ChatColor.RED + "Redstone Updates", Arrays.asList(ChatColor.GRAY + "Zeigt die Anzahl der Redstone-Updates an."));
        setCategory(redstoneCategoryItem, "redstone");
        gui.setItem(14, redstoneCategoryItem);

        ItemStack generalCategoryItem = createGuiItem(Material.BOOK, ChatColor.YELLOW + "Allgemeine Statistiken", Arrays.asList(ChatColor.GRAY + "Spieler, Welten, Item-Entitäten etc."));
        setCategory(generalCategoryItem, "general");
        gui.setItem(16, generalCategoryItem);

        ItemStack filler = createGuiItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, filler);
            }
        }
        player.openInventory(gui);
    }

    private void setCategory(ItemStack item, String categoryName) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(CATEGORY_KEY, PersistentDataType.STRING, categoryName);
            item.setItemMeta(meta);
        }
    }

    public void openMobsGui(Player player, int page) {
        List<Map.Entry<EntityType, Integer>> sortedMobCounts = statisticManager.getCurrentMobCounts().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());

        int totalMobsTypes = sortedMobCounts.size();
        int totalPages = (int) Math.ceil((double) totalMobsTypes / MOBS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;
        page = Math.max(1, Math.min(page, totalPages));

        Inventory gui = Bukkit.createInventory(player, 54, MOB_STATS_GUI_TITLE_PREFIX + ChatColor.GRAY + " (Seite " + page + "/" + totalPages + ")");

        int startIndex = (page - 1) * MOBS_PER_PAGE;
        int endIndex = Math.min(startIndex + MOBS_PER_PAGE, totalMobsTypes);

        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            if (slot >= MOBS_PER_PAGE) break;
            Map.Entry<EntityType, Integer> entry = sortedMobCounts.get(i);
            EntityType type = entry.getKey();
            int count = entry.getValue();

            Material mobMaterial = getMaterialForEntityType(type);
            ItemStack mobItem = new ItemStack(mobMaterial, Math.min(count, 64));
            ItemMeta meta = mobItem.getItemMeta();

            if (meta != null) {
                meta.setDisplayName(ChatColor.GREEN + formatEntityTypeName(type));
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.WHITE + "Anzahl: " + ChatColor.YELLOW + count);
                lore.add(ChatColor.DARK_GRAY + "--------------------");
                lore.add(ChatColor.GRAY + "Kategorie: " + ChatColor.AQUA + (type.isAlive() ? "Lebend" : (type.isSpawnable() ? "Spawnbar" : "Sonstige")));
                meta.getPersistentDataContainer().set(MOB_TYPE_KEY, PersistentDataType.STRING, type.name());
                meta.setLore(lore);
                mobItem.setItemMeta(meta);
            }
            gui.setItem(slot++, mobItem);
        }

        if (totalMobsTypes == 0) {
            gui.setItem(22, createGuiItem(Material.BARRIER, ChatColor.RED + "Keine Mobs gefunden",
                    Arrays.asList(ChatColor.GRAY + "Aktuell sind keine Mobs geladen.")));
        }

        ItemStack filler = createGuiItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 45; i < 54; i++) {
            gui.setItem(i, filler);
        }
        gui.setItem(45, createBackButton());

        if (page > 1) {
            ItemStack prevPageItem = createGuiItem(Material.ARROW, ChatColor.AQUA + "Vorherige Seite", Arrays.asList(ChatColor.GRAY + "Zu Seite " + (page - 1)));
            ItemMeta prevMeta = prevPageItem.getItemMeta();
            if (prevMeta != null) {
                prevMeta.getPersistentDataContainer().set(PAGE_ACTION_KEY, PersistentDataType.STRING, "prev");
                prevMeta.getPersistentDataContainer().set(CURRENT_PAGE_KEY, PersistentDataType.INTEGER, page);
                prevPageItem.setItemMeta(prevMeta);
            }
            gui.setItem(48, prevPageItem);
        }
        if (page < totalPages) {
            ItemStack nextPageItem = createGuiItem(Material.ARROW, ChatColor.AQUA + "Nächste Seite", Arrays.asList(ChatColor.GRAY + "Zu Seite " + (page + 1)));
            ItemMeta nextMeta = nextPageItem.getItemMeta();
            if (nextMeta != null) {
                nextMeta.getPersistentDataContainer().set(PAGE_ACTION_KEY, PersistentDataType.STRING, "next");
                nextMeta.getPersistentDataContainer().set(CURRENT_PAGE_KEY, PersistentDataType.INTEGER, page);
                nextPageItem.setItemMeta(nextMeta);
            }
            gui.setItem(50, nextPageItem);
        }
        player.openInventory(gui);
    }

    public void openBlockStatsGui(Player player) {
        Inventory gui = Bukkit.createInventory(player, 27, BLOCK_STATS_GUI_TITLE);
        ItemStack blockActivityItem = createGuiItem(
                Material.DIAMOND_PICKAXE,
                ChatColor.BLUE + "Block Aktivitäten (letzte 5 Min)",
                Arrays.asList(
                        ChatColor.WHITE + "Anzahl: " + ChatColor.YELLOW + statisticManager.getBlockActivityLast5Min(),
                        ChatColor.GRAY + "Zählt platzierte und abgebaute Blöcke."
                )
        );
        gui.setItem(13, blockActivityItem);
        gui.setItem(gui.getSize() - 5, createBackButton());
        ItemStack filler = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, filler);
            }
        }
        player.openInventory(gui);
    }

    public void openRedstoneStatsGui(Player player) {
        Inventory gui = Bukkit.createInventory(player, 27, REDSTONE_STATS_GUI_TITLE);

        ItemStack redstoneCountItem = createGuiItem(
                Material.REDSTONE_TORCH,
                ChatColor.RED + "Redstone Updates",
                Arrays.asList(
                        // ANGEPASST: Verwendet die neue Methode und den neuen Text
                        ChatColor.WHITE + "Anzahl (letzte 5 Min): " + ChatColor.YELLOW + statisticManager.getRedstoneUpdatesLast5Min()
                )
        );
        gui.setItem(13, redstoneCountItem);

        gui.setItem(gui.getSize() - 5, createBackButton()); // Zurück-Button

        ItemStack filler = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, filler);
            }
        }
        player.openInventory(gui);
    }

    public void openGeneralStatsGui(Player player) {
        Inventory gui = Bukkit.createInventory(player, 45, GENERAL_STATS_GUI_TITLE);

        // Reihe 1
        ItemStack playerCountItem = createGuiItemWithSkull(player, ChatColor.YELLOW + "Spieler Online",
                Arrays.asList(ChatColor.WHITE + "Anzahl: " + ChatColor.GOLD + statisticManager.getPlayerCount()));
        gui.setItem(1, playerCountItem);

        ItemStack worldCountItem = createGuiItem(Material.GRASS_BLOCK, ChatColor.GREEN + "Welten",
                Arrays.asList(
                        ChatColor.WHITE + "Anzahl: " + ChatColor.GOLD + statisticManager.getWorldCount(),
                        ChatColor.WHITE + "Gel. Chunks: " + ChatColor.GOLD + statisticManager.getTotalLoadedChunks(),
                        ChatColor.GRAY + "Namen: " + String.join(", ", statisticManager.getWorldNames())
                ));
        gui.setItem(4, worldCountItem);

        ItemStack totalEntityCountItem = createGuiItem(Material.ARMOR_STAND, ChatColor.BLUE + "Gesamt Entitäten",
                Arrays.asList(ChatColor.WHITE + "Anzahl: " + ChatColor.GOLD + statisticManager.getTotalEntityCount()));
        gui.setItem(7, totalEntityCountItem);

        // Reihe 2: Performance (TPS, RAM)
        double currentTps = statisticManager.getCurrentTPS();
        DecimalFormat df = new DecimalFormat("#0.0#");
        ChatColor tpsColor = getTPSColor(currentTps);
        ItemStack tpsItem = createGuiItem(Material.CLOCK, tpsColor + "Server TPS",
                Arrays.asList(
                        ChatColor.WHITE + "Aktuell: " + tpsColor + df.format(currentTps),
                        ChatColor.GRAY + "(basierend auf den letzten " + StatisticManager.TPS_SAMPLE_SIZE + " Ticks)"
                ));
        gui.setItem(19, tpsItem); // Slot 10 der 2. Reihe (index 19)

        ItemStack ramItem = createGuiItem(Material.FURNACE, ChatColor.LIGHT_PURPLE + "RAM Auslastung",
                Arrays.asList(
                        ChatColor.WHITE + "Maximal: " + ChatColor.GOLD + statisticManager.getMaxRamMB() + " MB",
                        ChatColor.WHITE + "Zugewiesen: " + ChatColor.GOLD + statisticManager.getAllocatedRamMB() + " MB",
                        ChatColor.WHITE + "Genutzt: " + ChatColor.GOLD + statisticManager.getUsedRamMB() + " MB"
                ));
        gui.setItem(22, ramItem); // Slot 13 der 2. Reihe (index 22)

        // Reihe 3
        ItemStack itemEntityCountItem = createGuiItem(Material.ITEM_FRAME, ChatColor.AQUA + "Item Entities",
                Arrays.asList(ChatColor.WHITE + "Anzahl: " + ChatColor.GOLD + statisticManager.getItemEntityCount()));
        gui.setItem(28, itemEntityCountItem);

        int avgPing = statisticManager.getAveragePing();
        String pingDisplay;
        ChatColor pingColorForDisplay = ChatColor.YELLOW;
        if (avgPing == -1) {
            pingDisplay = ChatColor.GRAY + "N/A (Keine Spieler)";
        } else if (avgPing == -2) {
            pingDisplay = ChatColor.RED + "Nicht messbar (Fehler)";
        } else {
            pingDisplay = ChatColor.GOLD.toString() + avgPing + " ms";
            if (avgPing < 50) pingColorForDisplay = ChatColor.GREEN;
            else if (avgPing > 150) pingColorForDisplay = ChatColor.RED;
        }
        ItemStack pingItem = createGuiItem(Material.ENDER_PEARL, pingColorForDisplay + "Durchschnitts-Ping",
                Arrays.asList(pingDisplay));
        gui.setItem(31, pingItem);

        gui.setItem(gui.getSize() - 5, createBackButton()); // Mittig in der letzten Reihe (Slot 40)
        ItemStack filler = createGuiItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, filler);
            }
        }
        player.openInventory(gui);
    }

    private ChatColor getTPSColor(double tps) {
        if (tps >= 19.0) return ChatColor.DARK_GREEN;
        if (tps >= 16.0) return ChatColor.YELLOW;
        if (tps >= 12.0) return ChatColor.GOLD;
        return ChatColor.RED;
    }

    private ItemStack createGuiItem(Material material, String name, List<String> loreLines) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (loreLines != null && !loreLines.isEmpty()) {
                meta.setLore(loreLines);
            }
            // ItemFlag.HIDE_POTION_EFFECTS ist seit API 1.9 verfügbar.
            // Deine plugin.yml hat api-version: 1.21, also sollte das funktionieren.
            // Falls es dennoch Kompilierfehler gibt, stelle sicher, dass deine Spigot-JAR zum Kompilieren aktuell ist.
            // Als letzte Instanz kannst du HIDE_POTION_EFFECTS entfernen, wenn es absolut nicht geht.
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createGuiItem(Material material, String name) {
        return createGuiItem(material, name, null);
    }

    private ItemStack createGuiItemWithSkull(Player owner, String name, List<String> loreLines) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            if (owner != null) {
                meta.setOwningPlayer(owner);
            }
            meta.setDisplayName(name);
            if (loreLines != null && !loreLines.isEmpty()) {
                meta.setLore(loreLines);
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        return item;
    }

    private Material getMaterialForEntityType(EntityType type) {
        String typeName = type.name();
        Material spawnEggMaterial = Material.getMaterial(typeName + "_SPAWN_EGG");
        if (spawnEggMaterial != null) {
            return spawnEggMaterial;
        }
        switch (type) {
            case PLAYER: return Material.PLAYER_HEAD;
            case ITEM_FRAME: return Material.ITEM_FRAME;
            case PAINTING: return Material.PAINTING;
            case ARMOR_STAND: return Material.ARMOR_STAND;
            case COMMAND_BLOCK_MINECART: return Material.COMMAND_BLOCK_MINECART;
            case MINECART: return Material.MINECART;
            case CHEST_MINECART: return Material.CHEST_MINECART;
            case FURNACE_MINECART: return Material.FURNACE_MINECART;
            case HOPPER_MINECART: return Material.HOPPER_MINECART;
            case TNT_MINECART: return Material.TNT_MINECART;
            case ENDER_DRAGON: return Material.DRAGON_HEAD;
            default:
                if (type == EntityType.SHEEP) return Material.WHITE_WOOL;
                if (type == EntityType.COW) return Material.LEATHER;
                if (type == EntityType.PIG) return Material.PORKCHOP;
                if (type == EntityType.CHICKEN) return Material.FEATHER;
                if (type == EntityType.ZOMBIE) return Material.ROTTEN_FLESH;
                if (type == EntityType.SKELETON) return Material.BONE;
                if (type == EntityType.CREEPER) return Material.GUNPOWDER;
                if (type == EntityType.SPIDER) return Material.STRING;
                if (type == EntityType.ENDERMAN) return Material.ENDER_PEARL;
                return Material.PAPER; // Generisches Fallback
        }
    }

    private String formatEntityTypeName(EntityType type) {
        String name = type.name().toLowerCase().replace('_', ' ');
        String[] words = name.split(" ");
        StringBuilder formattedName = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                formattedName.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1)).append(" ");
            }
        }
        return formattedName.toString().trim();
    }
}