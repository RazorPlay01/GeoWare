package com.github.razorplay01.geoware.geowareplugin;

import com.github.razorplay01.geoware.geowarecommon.util.Pair;
import com.github.razorplay01.geoware.geowareplugin.api.PointsManagerAPI;
import com.github.razorplay01.geoware.geowareplugin.network.PacketSender;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manages player points in a Minecraft server using a SQLite database with optimized performance.
 */
public class PointsManager implements PointsManagerAPI {
    private final HikariDataSource dataSource;
    private static final String TABLE_NAME = "Puntos";
    private static final String POINTS_COLUMN = "puntos";
    private static final int TOP_LIMIT = 12;
    private final GeoWarePlugin plugin;
    private BukkitTask scoreUpdateTask;

    /**
     * Creates a new PointsManager instance and initializes the database connection pool.
     *
     * @param dataFolder Folder where the database file will be stored
     * @param plugin     The GeoWarePlugin instance
     * @throws SQLException If an error occurs while connecting to the database
     */
    public PointsManager(File dataFolder, GeoWarePlugin plugin) throws SQLException {
        this.plugin = plugin;
        File databaseFile = new File(dataFolder, "puntos.db");
        databaseFile.getParentFile().mkdirs();
        String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setMaximumPoolSize(2); // Reducido para minimizar concurrencia
        config.setMinimumIdle(1);
        config.setConnectionTimeout(60000); // Aumentado para mayor tolerancia
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.dataSource = new HikariDataSource(config);
        initializeDatabase();
        startScoreUpdateTask();
    }

    /**
     * Initializes the points table in the database and enables WAL mode.
     *
     * @throws SQLException If an error occurs while creating the table
     */
    private void initializeDatabase() throws SQLException {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (uuid TEXT PRIMARY KEY, nombre TEXT, " + POINTS_COLUMN + " INTEGER)";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableQuery);
            // Habilitar modo WAL
            stmt.execute("PRAGMA journal_mode=WAL;");
        }
    }

    /**
     * Starts a scheduled task to update scores for all players every 3 seconds.
     */
    private void startScoreUpdateTask() {
        scoreUpdateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PacketSender.sendScoreUpdaterPacketToClient(player);
            }
        }, 0L, 60L); // 3 segundos
    }

    /**
     * Closes the database connection pool and cancels scheduled tasks.
     */
    public void closeConnection() {
        if (scoreUpdateTask != null) {
            scoreUpdateTask.cancel();
        }
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    /**
     * Adds points to a player's total.
     *
     * @param player Player to add points to
     * @param amount Number of points to add
     */
    @Override
    public void addPoints(OfflinePlayer player, int amount) {
        if (player == null) {
            GeoWarePlugin.LOGGER.warn("Intento de añadir puntos a un jugador nulo");
            return;
        }
        modifyPoints(player, amount, true);
    }

    /**
     * Subtracts points from a player's total.
     *
     * @param player Player to subtract points from
     * @param amount Number of points to subtract
     */
    @Override
    public void subtractPoints(OfflinePlayer player, int amount) {
        if (player == null) {
            GeoWarePlugin.LOGGER.warn("Intento de restar puntos a un jugador nulo");
            return;
        }
        modifyPoints(player, amount, false);
    }

    /**
     * Modifies a player's points (add or subtract) asynchronously with retry logic.
     *
     * @param player     Player whose points will be modified
     * @param amount     Number of points to modify
     * @param isAddition True to add, false to subtract
     */
    private void modifyPoints(OfflinePlayer player, int amount, boolean isAddition) {
        String uuid = player.getUniqueId().toString();
        String playerName = player.getName() != null ? player.getName() : "Unknown";

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int retries = 3;
            while (retries > 0) {
                try (Connection conn = dataSource.getConnection()) {
                    int currentPoints = getPlayerPoints(player, conn);
                    int updatedPoints = isAddition ? (currentPoints + amount) : Math.max(0, currentPoints - amount);
                    String upsertQuery = "INSERT OR REPLACE INTO " + TABLE_NAME + " (uuid, nombre, puntos) VALUES (?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(upsertQuery)) {
                        stmt.setString(1, uuid);
                        stmt.setString(2, playerName);
                        stmt.setInt(3, updatedPoints);
                        stmt.executeUpdate();
                    }
                    return;
                } catch (SQLException e) {
                    if (e.getMessage().contains("SQLITE_BUSY") && retries > 0) {
                        retries--;
                        try {
                            Thread.sleep(50); // Esperar antes de reintentar
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        GeoWarePlugin.LOGGER.error("Error modifying points for player {}", playerName, e);
                        return;
                    }
                }
            }
            GeoWarePlugin.LOGGER.warn("No se pudo modificar puntos para {} después de reintentos", playerName);
        });
    }

    /**
     * Adds points for multiple players in a single batch operation with retry logic.
     *
     * @param pointsMap Map of players to their respective point amounts
     */
    @Override
    public void addPointsBulk(Map<OfflinePlayer, Integer> pointsMap) {
        if (pointsMap == null || pointsMap.isEmpty()) {
            GeoWarePlugin.LOGGER.warn("Intento de ejecutar addPointsBulk con un mapa nulo o vacío");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int retries = 3;
            while (retries > 0) {
                try (Connection conn = dataSource.getConnection()) {
                    conn.setAutoCommit(false); // Enable batch processing
                    String upsertQuery = "INSERT OR REPLACE INTO " + TABLE_NAME + " (uuid, nombre, puntos) VALUES (?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(upsertQuery)) {
                        for (Map.Entry<OfflinePlayer, Integer> entry : pointsMap.entrySet()) {
                            OfflinePlayer player = entry.getKey();
                            if (player == null) {
                                GeoWarePlugin.LOGGER.warn("Jugador nulo encontrado en addPointsBulk");
                                continue;
                            }
                            int pointsToAdd = entry.getValue();
                            int currentPoints = getPlayerPoints(player, conn);
                            int updatedPoints = currentPoints + pointsToAdd;
                            stmt.setString(1, player.getUniqueId().toString());
                            stmt.setString(2, player.getName() != null ? player.getName() : "Unknown");
                            stmt.setInt(3, updatedPoints);
                            stmt.addBatch();
                        }
                        stmt.executeBatch();
                        conn.commit();
                    } catch (SQLException e) {
                        conn.rollback();
                        throw e;
                    } finally {
                        conn.setAutoCommit(true);
                    }
                    return;
                } catch (SQLException e) {
                    if (e.getMessage().contains("SQLITE_BUSY") && retries > 0) {
                        retries--;
                        try {
                            Thread.sleep(50); // Esperar antes de reintentar
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        GeoWarePlugin.LOGGER.error("Error during bulk points update", e);
                        return;
                    }
                }
            }
            GeoWarePlugin.LOGGER.warn("No se pudo ejecutar addPointsBulk después de reintentos");
        });
    }

    /**
     * Resets a player's points to zero.
     *
     * @param player Player whose points will be reset
     */
    @Override
    public void resetPlayerPoints(OfflinePlayer player) {
        if (player == null) {
            GeoWarePlugin.LOGGER.warn("Intento de reiniciar puntos de un jugador nulo");
            return;
        }
        String uuid = player.getUniqueId().toString();
        String resetQuery = "UPDATE " + TABLE_NAME + " SET " + POINTS_COLUMN + " = 0 WHERE uuid = ?";

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int retries = 3;
            while (retries > 0) {
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(resetQuery)) {
                    stmt.setString(1, uuid);
                    stmt.executeUpdate();
                    return;
                } catch (SQLException e) {
                    if (e.getMessage().contains("SQLITE_BUSY") && retries > 0) {
                        retries--;
                        try {
                            Thread.sleep(50); // Esperar antes de reintentar
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        GeoWarePlugin.LOGGER.error("Error resetting points for player {}", player.getName() != null ? player.getName() : "Unknown", e);
                        return;
                    }
                }
            }
            GeoWarePlugin.LOGGER.warn("No se pudo reiniciar puntos para {} después de reintentos", player.getName() != null ? player.getName() : "Unknown");
        });
    }

    /**
     * Resets all players' points to zero.
     */
    @Override
    public void resetAllPoints() {
        String resetAllQuery = "UPDATE " + TABLE_NAME + " SET " + POINTS_COLUMN + " = 0";
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int retries = 3;
            while (retries > 0) {
                try (Connection conn = dataSource.getConnection();
                     Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(resetAllQuery);
                    return;
                } catch (SQLException e) {
                    if (e.getMessage().contains("SQLITE_BUSY") && retries > 0) {
                        retries--;
                        try {
                            Thread.sleep(50); // Esperar antes de reintentar
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        GeoWarePlugin.LOGGER.error("Error resetting all players' points", e);
                        return;
                    }
                }
            }
            GeoWarePlugin.LOGGER.warn("No se pudo reiniciar todos los puntos después de reintentos");
        });
    }

    /**
     * Gets a player's current points.
     *
     * @param player Player to query
     * @return Player's points, or 0 if not found or on error
     */
    @Override
    public int getPlayerPoints(OfflinePlayer player) {
        if (player == null) {
            GeoWarePlugin.LOGGER.warn("Intento de obtener puntos de un jugador nulo");
            return 0;
        }
        try (Connection conn = dataSource.getConnection()) {
            return getPlayerPoints(player, conn);
        } catch (SQLException e) {
            GeoWarePlugin.LOGGER.error("Error retrieving points for player {}", player.getName() != null ? player.getName() : "Unknown", e);
            return 0;
        }
    }

    /**
     * Gets a player's points using an existing connection.
     *
     * @param player Player to query
     * @param conn   Database connection
     * @return Player's points, or 0 if not found or on error
     */
    private int getPlayerPoints(OfflinePlayer player, Connection conn) throws SQLException {
        String uuid = player.getUniqueId().toString();
        String selectQuery = "SELECT " + POINTS_COLUMN + " FROM " + TABLE_NAME + " WHERE uuid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
            stmt.setString(1, uuid);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(POINTS_COLUMN) : 0;
            }
        }
    }

    /**
     * Gets a player's points and leaderboard position.
     *
     * @param player Player to query
     * @return Pair of points and position, or (0, 0) if not found or on error
     */
    @Override
    public Pair<Integer, Integer> obtenerPuntosYPosicion(OfflinePlayer player) {
        if (player == null) {
            GeoWarePlugin.LOGGER.warn("Intento de obtener puntos y posición de un jugador nulo");
            return new Pair<>(0, 0);
        }
        String uuid = player.getUniqueId().toString();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT uuid, puntos FROM " + TABLE_NAME + " ORDER BY puntos DESC")) {
            int position = 0;
            int points = 0;
            while (rs.next()) {
                position++;
                if (rs.getString("uuid").equals(uuid)) {
                    points = rs.getInt("puntos");
                    break;
                }
            }
            return position == 0 ? new Pair<>(0, 0) : new Pair<>(points, position);
        } catch (SQLException e) {
            GeoWarePlugin.LOGGER.error("Error retrieving points and position for player {}", player.getName() != null ? player.getName() : "Unknown", e);
            return new Pair<>(0, 0);
        }
    }

    /**
     * Gets the top 12 players by points.
     *
     * @return List of strings in format "name: points"
     */
    @Override
    public List<String> getTopPlayers() {
        List<String> topPlayers = new ArrayList<>();
        String topQuery = "SELECT nombre, " + POINTS_COLUMN + " FROM " + TABLE_NAME +
                " ORDER BY " + POINTS_COLUMN + " DESC LIMIT " + TOP_LIMIT;

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(topQuery)) {
            while (rs.next()) {
                topPlayers.add(rs.getString("nombre") + ": " + rs.getInt(POINTS_COLUMN));
            }
        } catch (SQLException e) {
            GeoWarePlugin.LOGGER.error("Error retrieving top players", e);
        }
        return topPlayers;
    }

    /**
     * Gets the names of the top 12 players by points.
     *
     * @return List of player names
     */
    @Override
    public List<String> getTop12Players() {
        List<String> topPlayers = new ArrayList<>();
        String topQuery = "SELECT nombre FROM " + TABLE_NAME +
                " ORDER BY " + POINTS_COLUMN + " DESC LIMIT " + TOP_LIMIT;

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(topQuery)) {
            while (rs.next()) {
                topPlayers.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            GeoWarePlugin.LOGGER.error("Error retrieving top players", e);
        }
        return topPlayers;
    }

    /**
     * Gets the bottom 12 players with points greater than zero.
     *
     * @return List of strings in format "name: points"
     */
    @Override
    public List<String> getBottomPlayers() {
        List<String> bottomPlayers = new ArrayList<>();
        String bottomQuery = "SELECT nombre, " + POINTS_COLUMN + " FROM " + TABLE_NAME +
                " WHERE " + POINTS_COLUMN + " > 0 ORDER BY " + POINTS_COLUMN + " ASC LIMIT " + TOP_LIMIT;

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(bottomQuery)) {
            while (rs.next()) {
                bottomPlayers.add(rs.getString("nombre") + ": " + rs.getInt(POINTS_COLUMN));
            }
        } catch (SQLException e) {
            GeoWarePlugin.LOGGER.error("Error retrieving bottom players", e);
        }
        return bottomPlayers;
    }
}