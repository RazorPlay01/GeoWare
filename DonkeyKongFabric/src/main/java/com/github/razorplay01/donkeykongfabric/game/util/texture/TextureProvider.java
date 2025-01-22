package com.github.razorplay01.donkeykongfabric.game.util.texture;

import com.github.razorplay01.donkeykongfabric.DonkeyKongFabric;
import net.minecraft.util.Identifier;

import java.util.List;

public class TextureProvider {
    private TextureProvider() {
        // []
    }

    private static final Identifier PLAYER_TEXTURE = Identifier.of(DonkeyKongFabric.MOD_ID, "textures/gui/game/player.png");
    private static final Identifier PLAYER_EXTRA_TEXTURE = Identifier.of(DonkeyKongFabric.MOD_ID, "textures/gui/game/extra.png");
    public static final List<Texture> PLAYER_CLIMB_TEXTURES = List.of(
            new Texture(PLAYER_TEXTURE, 0, 0, 18, 18, 72, 72, 1.0f),
            new Texture(PLAYER_TEXTURE, 18, 0, 18, 18, 72, 72, 1.0f));
    public static final List<Texture> PLAYER_IDLE_R_TEXTURES = List.of(
            new Texture(PLAYER_TEXTURE, 0, 36, 18, 18, 72, 72, 1.0f));
    public static final List<Texture> PLAYER_IDLE_L_TEXTURES = List.of(
            new Texture(PLAYER_TEXTURE, 18, 36, 18, 18, 72, 72, 1.0f));
    public static final List<Texture> PLAYER_JUMP_R_TEXTURES = List.of(
            new Texture(PLAYER_TEXTURE, 36, 0, 18, 18, 72, 72, 1.0f));
    public static final List<Texture> PLAYER_WIN_R_TEXTURES = List.of(
            new Texture(PLAYER_TEXTURE, 0, 18, 18, 18, 72, 72, 1.0f));
    public static final List<Texture> PLAYER_WIN_L_TEXTURES = List.of(
            new Texture(PLAYER_TEXTURE, 18, 18, 18, 18, 72, 72, 1.0f));
    public static final List<Texture> PLAYER_JUMP_L_TEXTURES = List.of(
            new Texture(PLAYER_TEXTURE, 54, 0, 18, 18, 72, 72, 1.0f));
    public static final List<Texture> PLAYER_WALK_R_TEXTURES = List.of(
            new Texture(PLAYER_TEXTURE, 36, 18, 18, 18, 72, 72, 1.0f),
            new Texture(PLAYER_TEXTURE, 36, 36, 18, 18, 72, 72, 1.0f));
    public static final List<Texture> PLAYER_WALK_L_TEXTURES = List.of(
            new Texture(PLAYER_TEXTURE, 54, 18, 18, 18, 72, 72, 1.0f),
            new Texture(PLAYER_TEXTURE, 54, 36, 18, 18, 72, 72, 1.0f));
    public static final List<Texture> PLAYER_DIE_TEXTURES = List.of(
            new Texture(PLAYER_TEXTURE, 0, 54, 18, 18, 72, 72, 1.0f),
            new Texture(PLAYER_TEXTURE, 18, 54, 18, 18, 72, 72, 1.0f),
            new Texture(PLAYER_TEXTURE, 36, 54, 18, 18, 72, 72, 1.0f),
            new Texture(PLAYER_TEXTURE, 54, 54, 18, 18, 72, 72, 1.0f));

    public static final List<Texture> PLAYER_HAMMET_R_TEXTURES = List.of(
            new Texture(PLAYER_EXTRA_TEXTURE, 27, 0, 28, 28, 84, 56, 1.0f),
            new Texture(PLAYER_EXTRA_TEXTURE, 27, 28, 28, 28, 84, 56, 1.0f));
    public static final List<Texture> PLAYER_HAMMET_L_TEXTURES = List.of(
            new Texture(PLAYER_EXTRA_TEXTURE, 0, 0, 28, 28, 84, 56, 1.0f),
            new Texture(PLAYER_EXTRA_TEXTURE, 0, 28, 28, 28, 84, 56, 1.0f));

    private static final Identifier BARREL_TEXTURE = Identifier.of(DonkeyKongFabric.MOD_ID, "textures/gui/game/barrel.png");
    private static final int BARREL_WIDTH = 12;
    private static final int BARREL_HEIGHT = 12;
    private static final int BARREL_TEXTURE_WIDTH = 64;
    private static final int BARREL_TEXTURE_HEIGHT = 24;
    public static final List<Texture> BARREL_R_TEXTURES = List.of(
            new Texture(BARREL_TEXTURE, 0, 0, BARREL_WIDTH, BARREL_HEIGHT, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f),
            new Texture(BARREL_TEXTURE, 12, 0, BARREL_WIDTH, BARREL_HEIGHT, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f),
            new Texture(BARREL_TEXTURE, 24, 0, BARREL_WIDTH, BARREL_HEIGHT, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f),
            new Texture(BARREL_TEXTURE, 36, 0, BARREL_WIDTH, BARREL_HEIGHT, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f));
    public static final List<Texture> BARREL_L_TEXTURES = List.of(
            new Texture(BARREL_TEXTURE, 0, 12, BARREL_WIDTH, BARREL_HEIGHT, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f),
            new Texture(BARREL_TEXTURE, 12, 12, BARREL_WIDTH, BARREL_HEIGHT, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f),
            new Texture(BARREL_TEXTURE, 24, 12, BARREL_WIDTH, BARREL_HEIGHT, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f),
            new Texture(BARREL_TEXTURE, 36, 12, BARREL_WIDTH, BARREL_HEIGHT, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f));
    public static final List<Texture> BARREL_V_TEXTURES = List.of(
            new Texture(BARREL_TEXTURE, 48, 0, 17, BARREL_HEIGHT, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f),
            new Texture(BARREL_TEXTURE, 48, 12, 17, BARREL_HEIGHT, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f));

    private static final Identifier FIRE_TEXTURE = Identifier.of(DonkeyKongFabric.MOD_ID, "textures/gui/game/fire.png");
    public static final List<Texture> FIRE_R_TEXTURES = List.of(
            new Texture(FIRE_TEXTURE, 0, 0, 16, 16, 32, 32, 1.0f),
            new Texture(FIRE_TEXTURE, 16, 0, 16, 16, 32, 32, 1.0f));
    public static final List<Texture> FIRE_L_TEXTURES = List.of(
            new Texture(FIRE_TEXTURE, 0, 16, 16, 16, 32, 32, 1.0f),
            new Texture(FIRE_TEXTURE, 16, 16, 16, 16, 32, 32, 1.0f));

    private static final Identifier EXTRA_TEXTURE = Identifier.of(DonkeyKongFabric.MOD_ID, "textures/gui/game/extra.png");
    public static final List<Texture> HAMMET_ITEM_TEXTURES = List.of(
            new Texture(EXTRA_TEXTURE, 56, 18, 13, 13, 84, 56, 1.0f));

    private static final Identifier PARTICLE_TEXTURE = Identifier.of(DonkeyKongFabric.MOD_ID, "textures/gui/game/pop_particle.png");
    public static final List<Texture> PARTICLE_TEXTURES = List.of(
            new Texture(PARTICLE_TEXTURE, 0, 0, 16, 16, 80, 16, 1.0f),
            new Texture(PARTICLE_TEXTURE, 16, 0, 16, 16, 80, 16, 1.0f),
            new Texture(PARTICLE_TEXTURE, 32, 0, 16, 16, 80, 16, 1.0f),
            new Texture(PARTICLE_TEXTURE, 48, 0, 16, 16, 80, 16, 1.0f),
            new Texture(PARTICLE_TEXTURE, 64, 0, 16, 16, 80, 16, 1.0f)
    );
    private static final Identifier DONKEY_KONG_TEXTURE = Identifier.of(DonkeyKongFabric.MOD_ID, "textures/gui/game/donkey_kong.png");
    public static final List<Texture> DONKEY_KONG_BARREL_TEXTURES = List.of(
            new Texture(DONKEY_KONG_TEXTURE, 0, 32, 45, 40, 144, 120, 1.0f),
            new Texture(DONKEY_KONG_TEXTURE, 46, 32, 45, 40, 144, 120, 1.0f),
            new Texture(DONKEY_KONG_TEXTURE, 92, 32, 45, 40, 144, 120, 1.0f),
            new Texture(DONKEY_KONG_TEXTURE, 0, 80, 45, 40, 144, 120, 1.0f));
    public static final List<Texture> DONKEY_KONG_IDLE_TEXTURES = List.of(
            new Texture(DONKEY_KONG_TEXTURE, 0, 0, 45, 40, 144, 120, 1.0f));
    public static final List<Texture> DONKEY_KONG_EXTRA_TEXTURES = List.of(
            new Texture(DONKEY_KONG_TEXTURE, 46, 0, 45, 40, 144, 120, 1.0f),
            new Texture(DONKEY_KONG_TEXTURE, 92, 0, 45, 40, 144, 120, 1.0f),
            new Texture(DONKEY_KONG_TEXTURE, 0, 80, 45, 40  , 144, 120, 1.0f));
}