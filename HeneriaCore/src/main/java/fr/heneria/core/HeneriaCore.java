package fr.heneria.core;

import fr.heneria.core.command.CoinsCommand;
import fr.heneria.core.database.DatabaseManager;
import fr.heneria.core.listener.PlayerListener;
import fr.heneria.core.manager.EconomyManager;
import fr.heneria.core.manager.PlayerDataManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HeneriaCore extends JavaPlugin {

    private DatabaseManager databaseManager;
    private PlayerDataManager playerDataManager;
    private EconomyManager economyManager;

    @Override
    public void onEnable() {
        // Load config
        saveDefaultConfig();

        // Initialize Database
        this.databaseManager = new DatabaseManager(this);
        this.databaseManager.initialize();

        // Initialize Managers
        this.playerDataManager = new PlayerDataManager(this, databaseManager);
        this.economyManager = new EconomyManager(playerDataManager);

        // Register Commands
        if (getCommand("coins") != null) {
            getCommand("coins").setExecutor(new CoinsCommand(economyManager));
        }

        // Register Listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(playerDataManager), this);

        getLogger().info("HeneriaCore has been enabled!");
    }

    @Override
    public void onDisable() {
        // Close Database connection
        if (databaseManager != null) {
            databaseManager.close();
        }

        getLogger().info("HeneriaCore has been disabled!");
    }

    // Static accessors for API usage (optional but helpful for other plugins)
    // For a clearer API design, we might use a ServiceManager or Singleton pattern,
    // but typically getters on the main class are enough for simple needs.

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }
}
