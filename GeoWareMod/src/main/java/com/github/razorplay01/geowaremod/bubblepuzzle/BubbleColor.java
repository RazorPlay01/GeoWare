package com.github.razorplay01.geowaremod.bubblepuzzle;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.Identifier;

@Getter
@AllArgsConstructor
public enum BubbleColor {
    YELLOW(new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/sphere_yellow.png"), 0, 0, 28, 28, 28, 28, 0.8f)),
    BLUE(new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/sphere_blue.png"), 0, 0, 28, 28, 28, 28, 0.8f)),
    WHITE(new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/sphere_white.png"), 0, 0, 28, 28, 28, 28, 0.8f)),
    PURPLE(new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/sphere_purple.png"), 0, 0, 28, 28, 28, 28, 0.8f)),
    ORANGE(new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/sphere_orange.png"), 0, 0, 28, 28, 28, 28, 0.8f)),
    BLACK(new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/sphere_black.png"), 0, 0, 28, 28, 28, 28, 0.8f)),
    RED(new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/sphere_red.png"), 0, 0, 28, 28, 28, 28, 0.8f)),
    PINK(new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/sphere_pink.png"), 0, 0, 28, 28, 28, 28, 0.8f)),
    GREEN(new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/sphere_green.png"), 0, 0, 28, 28, 28, 28, 0.8f));
    private final Texture texture;
}
