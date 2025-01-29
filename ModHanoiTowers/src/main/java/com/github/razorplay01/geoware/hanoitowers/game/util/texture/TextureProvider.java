package com.github.razorplay01.geoware.hanoitowers.game.util.texture;

import com.github.razorplay01.geoware.hanoitowers.HanoiTowers;
import net.minecraft.util.Identifier;

import java.util.List;

public class TextureProvider {
    private TextureProvider() {
        // []
    }

    private static final Identifier PLAYER_TEXTURE = Identifier.of(HanoiTowers.MOD_ID, "textures/gui/game/player.png");
    private static final Identifier PLAYER_EXTRA_TEXTURE = Identifier.of(HanoiTowers.MOD_ID, "textures/gui/game/extra.png");
    public static final List<Texture> PLAYER_CLIMB_TEXTURES = List.of(
            new Texture(PLAYER_TEXTURE, 0, 0, 18, 18, 72, 72, 1.0f),
            new Texture(PLAYER_TEXTURE, 18, 0, 18, 18, 72, 72, 1.0f));
}