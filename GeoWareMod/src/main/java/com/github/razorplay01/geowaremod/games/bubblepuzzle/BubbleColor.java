package com.github.razorplay01.geowaremod.games.bubblepuzzle;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.Identifier;

@Getter
@AllArgsConstructor
public enum BubbleColor {
    YELLOW(new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/sphere_yellow.png"), 0, 0, 28, 28, 28, 28, 0.9f)),
    BLUE(new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/sphere_blue.png"), 0, 0, 28, 28, 28, 28, 0.9f)),
    WHITE(new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/sphere_white.png"), 0, 0, 28, 28, 28, 28, 0.9f)),
    PURPLE(new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/sphere_purple.png"), 0, 0, 28, 28, 28, 28, 0.9f)),
    ORANGE(new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/sphere_orange.png"), 0, 0, 28, 28, 28, 28, 0.9f)),
    BLACK(new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/sphere_black.png"), 0, 0, 28, 28, 28, 28, 0.9f)),
    RED(new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/sphere_red.png"), 0, 0, 28, 28, 28, 28, 0.9f)),
    PINK(new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/sphere_pink.png"), 0, 0, 28, 28, 28, 28, 0.9f)),
    GREEN(new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/sphere_green.png"), 0, 0, 28, 28, 28, 28, 0.9f));
    private final Texture texture;
}
