package fr.heneria.core.model;

import java.sql.Timestamp;
import java.util.UUID;

public class HeneriaPlayer {

    private final UUID uuid;
    private String username;
    private int coins;
    private String rankPrefix;
    private Timestamp firstJoin;
    private Timestamp lastJoin;

    public HeneriaPlayer(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.coins = 0;
        this.rankPrefix = "";
        this.firstJoin = new Timestamp(System.currentTimeMillis());
        this.lastJoin = new Timestamp(System.currentTimeMillis());
    }

    public HeneriaPlayer(UUID uuid, String username, int coins, String rankPrefix, Timestamp firstJoin, Timestamp lastJoin) {
        this.uuid = uuid;
        this.username = username;
        this.coins = coins;
        this.rankPrefix = rankPrefix;
        this.firstJoin = firstJoin;
        this.lastJoin = lastJoin;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getRankPrefix() {
        return rankPrefix;
    }

    public void setRankPrefix(String rankPrefix) {
        this.rankPrefix = rankPrefix;
    }

    public Timestamp getFirstJoin() {
        return firstJoin;
    }

    public void setFirstJoin(Timestamp firstJoin) {
        this.firstJoin = firstJoin;
    }

    public Timestamp getLastJoin() {
        return lastJoin;
    }

    public void setLastJoin(Timestamp lastJoin) {
        this.lastJoin = lastJoin;
    }
}
