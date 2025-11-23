package fr.heneria.core.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.heneria.core.HeneriaCore;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {

    private final HeneriaCore plugin;
    private HikariDataSource dataSource;

    public DatabaseManager(HeneriaCore plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        FileConfiguration config = plugin.getConfig();

        String host = config.getString("database.host");
        int port = config.getInt("database.port");
        String database = config.getString("database.database");
        String username = config.getString("database.username");
        String password = config.getString("database.password");
        int poolSize = config.getInt("database.pool_size");

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");

        // Performance settings
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.dataSource = new HikariDataSource(hikariConfig);

        initTables();
    }

    private void initTables() {
        // Run async to avoid blocking main thread if DB is slow, although initialize is usually called in onEnable
        // Ideally we should block startup if DB is critical, but the prompt emphasizes async.
        // However, we need the table to exist before players join.
        // We will execute this synchronously here to ensure tables exist,
        // OR rely on the fact that players join later.
        // Given prompt constraints ("Tout appel SQL doit être fait en Asynchrone"),
        // I will use runAsync but log any errors.

        CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection();
                 Statement statement = connection.createStatement()) {

                String sql = "CREATE TABLE IF NOT EXISTS heneria_players (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "username VARCHAR(16), " +
                        "coins INT DEFAULT 0, " +
                        "rank_prefix VARCHAR(64), " +
                        "first_join TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "last_join TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ");";

                statement.executeUpdate(sql);
                plugin.getLogger().info("Database tables initialized successfully.");

            } catch (SQLException e) {
                plugin.getLogger().severe("Could not initialize database tables: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
