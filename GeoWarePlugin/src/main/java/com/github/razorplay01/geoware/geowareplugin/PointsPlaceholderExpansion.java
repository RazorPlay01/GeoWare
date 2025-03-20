package com.github.razorplay01.geoware.geowareplugin;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PointsPlaceholderExpansion extends PlaceholderExpansion {
    public PointsPlaceholderExpansion() {
    }

    @Override
    public @NotNull String getIdentifier() {
        return "2dgamepoints"; // Identificador del placeholder, ej. %2dgamepoints_points%
    }

    @Override
    public @NotNull String getAuthor() {
        return "RazorPlay01"; // Tu nombre o el del equipo
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0"; // Versión de tu expansión
    }

    @Override
    public boolean persist() {
        return true; // Hace que la expansión permanezca registrada tras recargar el servidor
    }

    @Override
    public boolean canRegister() {
        return true; // Permite que se registre si PlaceholderAPI está presente
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return ""; // Retorna vacío si no hay jugador (por ejemplo, en consola)
        }

        if (identifier.toLowerCase().equals("points")) {// Placeholder %2dgamepoints_points%
            return String.valueOf(GeoWarePlugin.getInstance().getPointsManager().getPlayerPoints(player));
        }
        return null; // Retorna null si el placeholder no es reconocido
    }
}