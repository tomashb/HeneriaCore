package fr.heneria.core.manager;

import fr.heneria.core.HeneriaCore;
import fr.heneria.core.database.DatabaseManager;
import fr.heneria.core.model.HeneriaPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

public class PlayerDataManager {

    private final HeneriaCore plugin;
    private final DatabaseManager databaseManager;
    private final Map<UUID, HeneriaPlayer> playerCache;

    public PlayerDataManager(HeneriaCore plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.playerCache = new ConcurrentHashMap<>();
    }

    /**
     * Loads player data from the database. If not found, creates a new HeneriaPlayer object.
     * This method is blocking and should be called asynchronously.
     *
     * @param uuid     The player's UUID
     * @param username The player's username
     * @return The loaded or created HeneriaPlayer
     */
    public HeneriaPlayer loadPlayer(UUID uuid, String username) {
        if (plugin.getConfig().getBoolean("debug")) {
            plugin.getLogger().info("[DEBUG] Chargement des données pour UUID: " + uuid);
        }

        HeneriaPlayer player = null;
        String query = "SELECT * FROM heneria_players WHERE uuid = ?";

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int coins = resultSet.getInt("coins");
                    String rankPrefix = resultSet.getString("rank_prefix");
                    Timestamp firstJoin = resultSet.getTimestamp("first_join");
                    Timestamp lastJoin = resultSet.getTimestamp("last_join");

                    player = new HeneriaPlayer(uuid, username, coins, rankPrefix, firstJoin, lastJoin);

                    if (plugin.getConfig().getBoolean("debug")) {
                        plugin.getLogger().info("[DEBUG] Résultat SQL Load: Coins trouvés = " + coins);
                    }
                } else {
                     if (plugin.getConfig().getBoolean("debug")) {
                        plugin.getLogger().info("[DEBUG] Résultat SQL Load: Aucun résultat (Nouveau joueur).");
                    }
                }
            }

        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to load player data for " + username + ": " + e.getMessage());
            e.printStackTrace();
        }

        if (player == null) {
            player = new HeneriaPlayer(uuid, username);
            // Insert immediately as requested
            savePlayerToDatabase(player);
        } else {
            // Update username if changed (and last join time eventually)
            if (!player.getUsername().equals(username)) {
                player.setUsername(username);
            }
            player.setLastJoin(new Timestamp(System.currentTimeMillis()));
        }

        // Cache it
        playerCache.put(uuid, player);
        return player;
    }

    /**
     * Saves player data to the database.
     * This method is blocking and should be called asynchronously.
     *
     * @param player The player to save
     */
    public void savePlayerToDatabase(HeneriaPlayer player) {
        if (plugin.getConfig().getBoolean("debug")) {
             plugin.getLogger().info("[DEBUG] Sauvegarde lancée pour UUID: " + player.getUuid() + " Coins = " + player.getCoins());
        }

        String query = "INSERT INTO heneria_players (uuid, username, coins, rank_prefix, first_join, last_join) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE username=VALUES(username), coins=VALUES(coins), rank_prefix=VALUES(rank_prefix), last_join=VALUES(last_join)";

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, player.getUuid().toString());
            statement.setString(2, player.getUsername());
            statement.setInt(3, player.getCoins());
            statement.setString(4, player.getRankPrefix());
            statement.setTimestamp(5, player.getFirstJoin());
            statement.setTimestamp(6, player.getLastJoin());

            // No need to set update parameters manually with VALUES() syntax
            // The VALUES(col_name) function refers to the value that would have been inserted

            statement.executeUpdate();

            if (plugin.getConfig().getBoolean("debug")) {
                plugin.getLogger().info("[DEBUG] Sauvegarde terminée.");
            }

        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to save player data for " + player.getUsername() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void removePlayerFromCache(UUID uuid) {
        playerCache.remove(uuid);
    }

    public HeneriaPlayer getPlayer(UUID uuid) {
        return playerCache.get(uuid);
    }

    // Asynchronous save method helper
    public void savePlayerAsync(HeneriaPlayer player) {
        CompletableFuture.runAsync(() -> savePlayerToDatabase(player));
    }
}
