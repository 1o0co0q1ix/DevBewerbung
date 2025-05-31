# ServerStats Plugin für Spigot/Paper

![Plugin Logo oder Banner]([Platzhalter_Plugin_Banner.png])

Ein umfassendes Spigot/Paper-Plugin zur Anzeige detaillierter Serverstatistiken über eine benutzerfreundliche GUI. Behalte den Überblick über Mobs, Blockaktivitäten, Redstone-Nutzung, Spieler, Welten und wichtige Performance-Metriken deines Minecraft-Servers.

---

## ✨ Features

*   **Intuitive GUI:** Alle Statistiken sind über einen einfachen Befehl und eine übersichtliche grafische Oberfläche zugänglich.
*   **Mob-Statistiken:**
    *   Anzeige aller geladenen Mob-Typen.
    *   Anzahl pro Mob-Typ.
    *   Paginierung für eine große Anzahl verschiedener Mob-Typen.
    *   ![Mob Statistik GUI]([Mob_GUI.png])
*   **Block-Aktivitäten:**
    *   Anzahl der platzierten und abgebauten Blöcke in den letzten 5 Minuten.
    *   ![Block Statistik GUI]([Block_GUI.png])
*   **Redstone-Updates:**
    *   Anzahl der Redstone-Updates in den letzten 5 Minuten.
    *   ![Redstone Statistik GUI]([Redstone_GUI.png])
*   **Allgemeine Serverinformationen:**
    *   Anzahl der Online-Spieler.
    *   Anzahl der geladenen Welten und deren Namen.
    *   Gesamtzahl der Item-Entitäten.
    *   Gesamtzahl aller Entitäten.
    *   Anzahl der geladenen Chunks.
    *   ![Allgemeine Statistik GUI]([General_GUI.png])
*   **Performance-Metriken:**
    *   Aktuelle Server-TPS (Ticks Per Second).
    *   RAM-Auslastung (Maximal, Zugewiesen, Genutzt).
    *   Durchschnittlicher Spieler-Ping.
*   **Einfache Navigation:** "Zurück"-Buttons in allen Untermenüs für eine flüssige Bedienung.
*   **Berechtigungsgesteuert:** Zugriff auf die Statistiken kann über eine Permission kontrolliert werden.

---

## 🚀 Installation

1.  Lade die neueste `DevBewerbung-X.X.X.jar` von der [Releases-Seite]([Platzhalter_Link_zu_Releases_oder_Download]).
2.  Platziere die JAR-Datei in den `plugins`-Ordner deines Spigot/Paper-Servers.
3.  Starte oder lade deinen Server neu (`/reload` oder `/restart`).
4.  Das Plugin ist nun aktiv!

---

## 🛠️ Benutzung

*   **Hauptbefehl:** `/serverstats` (Aliase: `/sstats`, `/servstats`)
    *   Öffnet die Haupt-GUI, von der aus du zu den verschiedenen Statistik-Kategorien navigieren kannst.
    *   ![Haupt GUI]([Platzhalter_Main_GUI.png])

*   **Navigation:**
    *   Klicke auf die verschiedenen Items in der Haupt-GUI, um die jeweiligen Detailansichten zu öffnen.
    *   Benutze den "Zurück"-Button (oft ein Pfeil oder eine Barriere), um zum vorherigen Menü zurückzukehren.
    *   In der Mob-Statistik-GUI gibt es Pfeile zum Blättern durch die Seiten, falls viele Mob-Typen vorhanden sind.

---

## ⚙️ Konfiguration

Aktuell benötigt das Plugin keine separate Konfigurationsdatei. Alle Einstellungen sind fest im Code verankert.

---

## 🔑 Berechtigungen

*   `serverstats.use`
    *   Erlaubt Spielern die Benutzung des `/serverstats` Befehls und den Zugriff auf die Statistik-GUI.
    *   Standard: OP-Spieler (kann je nach Permissions-Plugin variieren).

---

## 📸 Screenshots

Hier eine kleine Vorschau der GUIs:

**Hauptmenü:**
![Hauptmenü Screenshot]([Platzhalter_Main_GUI_Screenshot_Detail.png])

**Mob-Übersicht (mit Paginierung):**
![Mob-Übersicht Screenshot]([Platzhalter_Mob_GUI_Screenshot_Detail.png])

**Allgemeine Statistiken (Ausschnitt Performance):**
![Allgemeine Statistiken Screenshot]([Platzhalter_General_GUI_Screenshot_Detail.png])

*(Füge hier weitere relevante Screenshots ein)*

---

## ❗ Bekannte Probleme / Geplante Features

*   **Bekannte Probleme:**
    *   (Liste hier ggf. bekannte kleinere Bugs oder Einschränkungen auf)
*   **Geplante Features:**
    *   Konfigurationsdatei für anpassbare Einstellungen.
    *   Weitere Detailstatistiken (z.B. pro Welt).
    *   Möglichkeit, Statistiken über einen bestimmten Zeitraum zu loggen.
    *   (Deine weiteren Ideen)

---

## 🤝 Mitwirken

Wenn du Fehler findest oder Verbesserungsvorschläge hast, erstelle bitte ein [Issue]([Platzhalter_Link_zu_Issues_auf_GitHub_o.ä.]) auf GitHub (falls vorhanden).

Pull Requests sind ebenfalls willkommen!

---

## 📜 Lizenz

Dieses Plugin steht unter der [MIT-Lizenz]([Platzhalter_Link_zur_LICENSE_Datei_falls_vorhanden_oder_Name_der_Lizenz]).

---

*Entwickelt von locoqlix ([Dein GitHub Profil Link oder Webseite])*