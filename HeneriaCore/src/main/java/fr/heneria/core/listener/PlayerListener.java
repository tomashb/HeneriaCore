package fr.heneria.core.listener;

import fr.heneria.core.manager.PlayerDataManager;
import fr.heneria.core.model.HeneriaPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerListener implements Listener {

    private final PlayerDataManager playerDataManager;

    public PlayerListener(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String username = event.getName();

        // This event is already async, so we can block here safely.
        // It's the perfect place to load data before the player actually joins.
        // Logs are handled inside loadPlayer.
        playerDataManager.loadPlayer(uuid, username);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        HeneriaPlayer player = playerDataManager.getPlayer(uuid);

        if (player != null) {
            // Save data asynchronously
            playerDataManager.savePlayerAsync(player);
            // Remove from cache
            playerDataManager.removePlayerFromCache(uuid);
        }
    }
}
