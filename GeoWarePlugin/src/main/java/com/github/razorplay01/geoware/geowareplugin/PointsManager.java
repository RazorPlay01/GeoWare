package com.github.razorplay01.geoware.geowareplugin;

import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PointsManager {
    private final Connection connection;

    public PointsManager(File dataFolder) throws SQLException {
        File dbFile = new File(dataFolder, "puntos.db");
        if (!dbFile.getParentFile().exists()) {
            dbFile.getParentFile().mkdirs();
        }
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        connection = DriverManager.getConnection(url);
        inicializarBaseDeDatos();
    }

    private void inicializarBaseDeDatos() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS Puntos (uuid TEXT PRIMARY KEY, nombre TEXT, puntos INTEGER)");
        }
    }

    public void cerrarConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Sumar puntos a un jugador
    public void sumarPuntos(Player player, int puntos) {
        modificarPuntos(player, puntos, true);
    }

    // Restar puntos a un jugador
    public void restarPuntos(Player player, int puntos) {
        modificarPuntos(player, puntos, false);
    }

    private void modificarPuntos(Player player, int cantidad, boolean sumar) {
        String uuid = player.getUniqueId().toString();
        String nombre = player.getName();
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT OR REPLACE INTO Puntos (uuid, nombre, puntos) VALUES (?, ?, COALESCE((SELECT puntos FROM Puntos WHERE uuid = ?) " + (sumar ? "+" : "-") + " ?, 0))")) {
            ps.setString(1, uuid);
            ps.setString(2, nombre);
            ps.setString(3, uuid);
            ps.setInt(4, cantidad);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Reiniciar puntos de un jugador
    public void reiniciarPuntos(Player player) {
        String uuid = player.getUniqueId().toString();
        try (PreparedStatement ps = connection.prepareStatement("UPDATE Puntos SET puntos = 0 WHERE uuid = ?")) {
            ps.setString(1, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Reiniciar puntos de todos los jugadores
    public void reiniciarTodos() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("UPDATE Puntos SET puntos = 0");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Obtener puntos de un jugador
    public int obtenerPuntos(Player player) {
        String uuid = player.getUniqueId().toString();
        try (PreparedStatement ps = connection.prepareStatement("SELECT puntos FROM Puntos WHERE uuid = ?")) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            int puntos = rs.next() ? rs.getInt("puntos") : 0;
            rs.close();
            return puntos;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Obtener top 10 mejores
    public List<String> topMejores() {
        List<String> top = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nombre, puntos FROM Puntos ORDER BY puntos DESC LIMIT 10")) {
            while (rs.next()) {
                top.add(rs.getString("nombre") + ": " + rs.getInt("puntos"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return top;
    }

    // Obtener top 10 peores
    public List<String> topPeores() {
        List<String> top = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nombre, puntos FROM Puntos WHERE puntos > 0 ORDER BY puntos ASC LIMIT 10")) {
            while (rs.next()) {
                top.add(rs.getString("nombre") + ": " + rs.getInt("puntos"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return top;
    }
}