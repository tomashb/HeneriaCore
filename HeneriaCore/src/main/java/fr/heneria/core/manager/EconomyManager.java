package fr.heneria.core.manager;

import fr.heneria.core.model.HeneriaPlayer;
import org.bukkit.entity.Player;

public class EconomyManager {

    private final PlayerDataManager playerDataManager;

    public EconomyManager(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    public void addCoins(Player player, int amount) {
        HeneriaPlayer hp = playerDataManager.getPlayer(player.getUniqueId());
        if (hp != null) {
            hp.setCoins(hp.getCoins() + amount);
        }
    }

    public void removeCoins(Player player, int amount) {
        HeneriaPlayer hp = playerDataManager.getPlayer(player.getUniqueId());
        if (hp != null) {
            hp.setCoins(Math.max(0, hp.getCoins() - amount));
        }
    }

    public void setCoins(Player player, int amount) {
        HeneriaPlayer hp = playerDataManager.getPlayer(player.getUniqueId());
        if (hp != null) {
            hp.setCoins(Math.max(0, amount));
        }
    }

    public int getCoins(Player player) {
        HeneriaPlayer hp = playerDataManager.getPlayer(player.getUniqueId());
        return hp != null ? hp.getCoins() : 0;
    }
}
