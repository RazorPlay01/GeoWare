package com.github.razorplay01.geoware.arkanoid.game.util.texture;

import net.minecraft.util.Identifier;

public record Texture(Identifier identifier, int u, int v, int width, int height, int textureWidth, int textureHeight, float scale) {
}
