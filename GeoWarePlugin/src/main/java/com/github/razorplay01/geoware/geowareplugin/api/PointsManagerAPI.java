package com.github.razorplay01.geoware.geowareplugin.api;

import org.bukkit.OfflinePlayer;
import com.github.razorplay01.geoware.geowarecommon.util.Pair;
import java.util.List;

/**
 * Public API for managing player points in GeoWarePlugin.
 */
public interface PointsManagerAPI {
    /**
     * Adds points to a player's total.
     *
     * @param player Player to add points to
     * @param amount Number of points to add
     */
    void addPoints(OfflinePlayer player, int amount);

    /**
     * Subtracts points from a player's total.
     *
     * @param player Player to subtract points from
     * @param amount Number of points to subtract
     */
    void subtractPoints(OfflinePlayer player, int amount);

    /**
     * Adds points for multiple players in a single batch operation.
     *
     * @param pointsMap Map of players to their respective point amounts
     */
    void addPointsBulk(java.util.Map<OfflinePlayer, Integer> pointsMap);

    /**
     * Resets a player's points to zero.
     *
     * @param player Player whose points will be reset
     */
    void resetPlayerPoints(OfflinePlayer player);

    /**
     * Resets all players' points to zero.
     */
    void resetAllPoints();

    /**
     * Gets a player's current points.
     *
     * @param player Player to query
     * @return Player's points, or 0 if not found or on error
     */
    int getPlayerPoints(OfflinePlayer player);

    /**
     * Gets a player's points and leaderboard position.
     *
     * @param player Player to query
     * @return Pair of points and position, or (0, 0) if not found or on error
     */
    Pair<Integer, Integer> obtenerPuntosYPosicion(OfflinePlayer player);

    /**
     * Gets the top 12 players by points.
     *
     * @return List of strings in format "name: points"
     */
    List<String> getTopPlayers();

    /**
     * Gets the names of the top 12 players by points.
     *
     * @return List of player names
     */
    List<String> getTop12Players();

    /**
     * Gets the bottom 12 players with points greater than zero.
     *
     * @return List of strings in format "name: points"
     */
    List<String> getBottomPlayers();
}