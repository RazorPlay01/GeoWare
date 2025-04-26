package com.github.razorplay01.geoware.geowareplugin;

import com.github.razorplay01.geoware.geowarecommon.util.Pair;
import com.github.razorplay01.geoware.geowareplugin.network.PacketSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.razorplay01.geoware.geowareplugin.network.PacketSender.sendScoreUpdaterPacketToClient;


/**
 * Gestiona los puntos de los jugadores en un servidor de Minecraft utilizando una base de datos SQLite.
 */
public class PointsManager {
    private final Connection databaseConnection;
    private static final String TABLE_NAME = "Puntos";
    private static final String POINTS_COLUMN = "puntos";
    private static final int TOP_LIMIT = 12;

    private static final ExecutorService executorService = Executors.newFixedThreadPool(20);

    /**
     * Crea una nueva instancia de PointsManager y establece la conexión a la base de datos.
     *
     * @param dataFolder Carpeta donde se almacenará el archivo de la base de datos
     * @throws SQLException Si ocurre un error al conectar con la base de datos
     */
    public PointsManager(File dataFolder) throws SQLException {
        File databaseFile = new File(dataFolder, "puntos.db");
        databaseFile.getParentFile().mkdirs();
        String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
        databaseConnection = DriverManager.getConnection(url);
        initializeDatabase();
    }

    /**
     * Inicializa la tabla de puntos en la base de datos si no existe.
     *
     * @throws SQLException Si ocurre un error al crear la tabla
     */
    private void initializeDatabase() throws SQLException {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (uuid TEXT PRIMARY KEY, nombre TEXT, " + POINTS_COLUMN + " INTEGER)";
        try (Statement statement = databaseConnection.createStatement()) {
            statement.execute(createTableQuery);
        }
    }

    /**
     * Cierra la conexión a la base de datos de manera segura.
     */
    public void closeConnection() {
        try {
            if (databaseConnection != null && !databaseConnection.isClosed()) {
                databaseConnection.close();
            }
        } catch (SQLException e) {
            GeoWarePlugin.LOGGER.error("Error closing database connection", e);
        }
    }

    /**
     * Añade puntos al total de un jugador.
     *
     * @param player Jugador al que se añadirán los puntos
     * @param amount Cantidad de puntos a añadir
     */
    public void addPoints(Player player, int amount) {
        modifyPoints(player, amount, true);
    }

    /**
     * Resta puntos del total de un jugador.
     *
     * @param player Jugador al que se restarán los puntos
     * @param amount Cantidad de puntos a restar
     */
    public void subtractPoints(Player player, int amount) {
        modifyPoints(player, amount, false);
    }

    /**
     * Modifica los puntos de un jugador (suma o resta).
     *
     * @param player     Jugador cuyos puntos serán modificados
     * @param amount     Cantidad de puntos a modificar
     * @param isAddition Indica si se suma (true) o resta (false)
     */
    private void modifyPoints(Player player, int amount, boolean isAddition) {
        String uuid = player.getUniqueId().toString();
        String playerName = player.getName();

        executorService.submit(() -> {
            try {
                int currentPoints = getPlayerPoints(player);
                int updatedPoints = isAddition ? currentPoints + amount : Math.max(0, currentPoints - amount);

                String upsertQuery = "INSERT OR REPLACE INTO " + TABLE_NAME +
                        " (uuid, nombre, " + POINTS_COLUMN + ") VALUES (?, ?, ?)";
                try (PreparedStatement statement = databaseConnection.prepareStatement(upsertQuery)) {
                    statement.setString(1, uuid);
                    statement.setString(2, playerName);
                    statement.setInt(3, updatedPoints);
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                GeoWarePlugin.LOGGER.error("Error modifying points for player {}", playerName, e);
            }
        });

        sendScoreUpdaterPacketToClient(player);
    }

    /**
     * Restablece los puntos de un jugador a cero.
     *
     * @param player Jugador cuyos puntos serán reiniciados
     */
    public void resetPlayerPoints(Player player) {
        String uuid = player.getUniqueId().toString();
        String resetQuery = "UPDATE " + TABLE_NAME + " SET " + POINTS_COLUMN + " = 0 WHERE uuid = ?";

        try (PreparedStatement statement = databaseConnection.prepareStatement(resetQuery)) {
            statement.setString(1, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            GeoWarePlugin.LOGGER.error("Error resetting points for player {}", player.getName(), e);
        }
        sendScoreUpdaterPacketToClient(player);
    }

    /**
     * Restablece los puntos de todos los jugadores a cero.
     */
    public void resetAllPoints() {
        String resetAllQuery = "UPDATE " + TABLE_NAME + " SET " + POINTS_COLUMN + " = 0";
        try (Statement statement = databaseConnection.createStatement()) {
            statement.execute(resetAllQuery);
        } catch (SQLException e) {
            GeoWarePlugin.LOGGER.error("Error resetting all players' points", e);
        }
        Bukkit.getOnlinePlayers().forEach(PacketSender::sendScoreUpdaterPacketToClient);
    }

    /**
     * Obtiene los puntos actuales de un jugador.
     *
     * @param player Jugador cuyos puntos se consultarán
     * @return Cantidad de puntos del jugador, 0 si no tiene registro o en caso de error
     */
    public int getPlayerPoints(Player player) {
        String uuid = player.getUniqueId().toString();
        String selectQuery = "SELECT " + POINTS_COLUMN + " FROM " + TABLE_NAME + " WHERE uuid = ?";

        try (PreparedStatement statement = databaseConnection.prepareStatement(selectQuery)) {
            statement.setString(1, uuid);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getInt(POINTS_COLUMN) : 0;
            }
        } catch (SQLException e) {
            GeoWarePlugin.LOGGER.error("Error retrieving points for player {}", player.getName(), e);
            return 0;
        }
    }

    public Pair<Integer, Integer> obtenerPuntosYPosicion(Player player) {
        String uuid = player.getUniqueId().toString();
        try (Statement stmt = databaseConnection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT uuid, puntos FROM Puntos ORDER BY puntos DESC")) {
            int posicion = 0;
            int puntos = 0;
            while (rs.next()) {
                posicion++;
                if (rs.getString("uuid").equals(uuid)) {
                    puntos = rs.getInt("puntos");
                    break;
                }
            }
            if (posicion == 0) {
                return new Pair<>(0, 0);
            }
            return new Pair<>(puntos, posicion);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Pair<>(0, 0);
        }
    }

    /**
     * Obtiene la lista de los 10 jugadores con más puntos.
     *
     * @return Lista de cadenas con formato "nombre: puntos"
     */
    public List<String> getTopPlayers() {
        List<String> topPlayers = new ArrayList<>();
        String topQuery = "SELECT nombre, " + POINTS_COLUMN + " FROM " + TABLE_NAME +
                " ORDER BY " + POINTS_COLUMN + " DESC LIMIT " + TOP_LIMIT;

        try (Statement statement = databaseConnection.createStatement();
             ResultSet result = statement.executeQuery(topQuery)) {
            while (result.next()) {
                topPlayers.add(result.getString("nombre") + ": " + result.getInt(POINTS_COLUMN));
            }
        } catch (SQLException e) {
            GeoWarePlugin.LOGGER.error("Error retrieving top players", e);
        }
        return topPlayers;
    }

    /**
     * Obtiene la lista de los 12 jugadores con más puntos, mostrando solo sus nombres.
     *
     * @return Lista de nombres de usuario
     */
    public List<String> getTop12Players() {
        List<String> topPlayers = new ArrayList<>();
        String topQuery = "SELECT nombre FROM " + TABLE_NAME +
                " ORDER BY " + POINTS_COLUMN + " DESC LIMIT " + TOP_LIMIT;

        try (Statement statement = databaseConnection.createStatement();
             ResultSet result = statement.executeQuery(topQuery)) {
            while (result.next()) {
                topPlayers.add(result.getString("nombre"));
            }
        } catch (SQLException e) {
            GeoWarePlugin.LOGGER.error("Error retrieving top players", e);
        }
        return topPlayers;
    }

    /**
     * Obtiene la lista de los 10 jugadores con menos puntos (excluyendo cero).
     *
     * @return Lista de cadenas con formato "nombre: puntos"
     */
    public List<String> getBottomPlayers() {
        List<String> bottomPlayers = new ArrayList<>();
        String bottomQuery = "SELECT nombre, " + POINTS_COLUMN + " FROM " + TABLE_NAME +
                " WHERE " + POINTS_COLUMN + " > 0 ORDER BY " + POINTS_COLUMN + " ASC LIMIT " + TOP_LIMIT;

        try (Statement statement = databaseConnection.createStatement();
             ResultSet result = statement.executeQuery(bottomQuery)) {
            while (result.next()) {
                bottomPlayers.add(result.getString("nombre") + ": " + result.getInt(POINTS_COLUMN));
            }
        } catch (SQLException e) {
            GeoWarePlugin.LOGGER.error("Error retrieving bottom players", e);
        }
        return bottomPlayers;
    }
}
