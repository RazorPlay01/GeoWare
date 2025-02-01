package com.github.razorplay01.geoware.donkeykong.game.util.texture;

import com.github.razorplay01.geoware.donkeykong.DonkeyKong;
import net.minecraft.util.Identifier;

import java.util.List;

public class TextureProvider {
    private TextureProvider() {
        // []
    }

    private static final Identifier EMPTY_TEXTURE = Identifier.of(DonkeyKong.MOD_ID, "textures/gui/game/empty.png");
    private static final int EMPTY_TEXTURE_WIDTH = 64;
    private static final int EMPTY_TEXTURE_HEIGHT = 64;

    private static final Identifier PLAYER_TEXTURE = Identifier.of(DonkeyKong.MOD_ID, "textures/gui/game/player.png");
    private static final Identifier EXTRA_TEXTURE = Identifier.of(DonkeyKong.MOD_ID, "textures/gui/game/extra.png");
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

    public static final List<Texture> PLAYER_IDLE_HAMMET_R_TEXTURES = List.of(
            new Texture(EXTRA_TEXTURE, 30, 0, 28, 28, 79, 227, 1.0f),
            new Texture(EXTRA_TEXTURE, 30, 28, 28, 28, 79, 227, 1.0f));
    public static final List<Texture> PLAYER_IDLE_HAMMET_L_TEXTURES = List.of(
            new Texture(EXTRA_TEXTURE, 2, 0, 28, 28, 79, 227, 1.0f),
            new Texture(EXTRA_TEXTURE, 2, 28, 28, 28, 79, 227, 1.0f));

    public static final List<Texture> PLAYER_WALK_HAMMET_R_TEXTURES = List.of(
            new Texture(EXTRA_TEXTURE, 30, 111, 28, 28, 79, 227, 1.0f),
            new Texture(EXTRA_TEXTURE, 30, 139, 28, 29, 79, 227, 1.0f),
            new Texture(EXTRA_TEXTURE, 30, 168, 29, 28, 79, 227, 1.0f),
            new Texture(EXTRA_TEXTURE, 30, 199, 28, 28, 79, 227, 1.0f));
    public static final List<Texture> PLAYER_WALK_HAMMET_L_TEXTURES = List.of(
            new Texture(EXTRA_TEXTURE, 2, 111, 28, 28, 79, 227, 1.0f),
            new Texture(EXTRA_TEXTURE, 2, 139, 28, 29, 79, 227, 1.0f),
            new Texture(EXTRA_TEXTURE, 1, 168, 29, 28, 79, 227, 1.0f),
            new Texture(EXTRA_TEXTURE, 2, 199, 28, 28, 79, 227, 1.0f));

    public static final List<Texture> HAMMET_ITEM_TEXTURES = List.of(
            new Texture(EXTRA_TEXTURE, 61, 18, 13, 13, 79, 227, 1.0f));

    public static final List<Texture> PLATFORM_TEXTURES = List.of(
            new Texture(EXTRA_TEXTURE, 63, 42, 16, 10, 79, 227, 1.0f));
    public static final List<Texture> LADDER_TEXTURES = List.of(
            new Texture(EXTRA_TEXTURE, 64, 58, 14, 12, 79, 227, 1.0f));

    private static final Identifier BARREL_TEXTURE = Identifier.of(DonkeyKong.MOD_ID, "textures/gui/game/barrel.png");
    private static final int BARREL_TEXTURE_WIDTH = 80;
    private static final int BARREL_TEXTURE_HEIGHT = 32;
    public static final List<Texture> BARREL_R_TEXTURES = List.of(
            new Texture(BARREL_TEXTURE, 0, 0, 16, 16, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f),
            new Texture(BARREL_TEXTURE, 16, 0, 16, 16, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f),
            new Texture(BARREL_TEXTURE, 32, 0, 16, 16, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f),
            new Texture(BARREL_TEXTURE, 48, 0, 16, 16, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f));
    public static final List<Texture> BARREL_L_TEXTURES = List.of(
            new Texture(BARREL_TEXTURE, 0, 16, 16, 16, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f),
            new Texture(BARREL_TEXTURE, 16, 16, 16, 16, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f),
            new Texture(BARREL_TEXTURE, 32, 16, 16, 16, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f),
            new Texture(BARREL_TEXTURE, 48, 16, 16, 16, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f));
    public static final List<Texture> BARREL_V_TEXTURES = List.of(
            new Texture(BARREL_TEXTURE, 64, 0, 16, 16, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f),
            new Texture(BARREL_TEXTURE, 64, 16, 16, 16, BARREL_TEXTURE_WIDTH, BARREL_TEXTURE_HEIGHT, 1.0f));

    private static final Identifier FIRE_TEXTURE = Identifier.of(DonkeyKong.MOD_ID, "textures/gui/game/fire.png");
    public static final List<Texture> FIRE_R_TEXTURES = List.of(
            new Texture(FIRE_TEXTURE, 0, 0, 16, 16, 32, 32, 1.0f),
            new Texture(FIRE_TEXTURE, 16, 0, 16, 16, 32, 32, 1.0f));
    public static final List<Texture> FIRE_L_TEXTURES = List.of(
            new Texture(FIRE_TEXTURE, 0, 16, 16, 16, 32, 32, 1.0f),
            new Texture(FIRE_TEXTURE, 16, 16, 16, 16, 32, 32, 1.0f));

    private static final Identifier PARTICLE_TEXTURE = Identifier.of(DonkeyKong.MOD_ID, "textures/gui/game/pop_particle.png");
    public static final List<Texture> PARTICLE_TEXTURES = List.of(
            new Texture(PARTICLE_TEXTURE, 0, 0, 16, 16, 80, 16, 1.0f),
            new Texture(PARTICLE_TEXTURE, 16, 0, 16, 16, 80, 16, 1.0f),
            new Texture(PARTICLE_TEXTURE, 32, 0, 16, 16, 80, 16, 1.0f),
            new Texture(PARTICLE_TEXTURE, 48, 0, 16, 16, 80, 16, 1.0f),
            new Texture(EMPTY_TEXTURE, 0, 0, 16, 16, EMPTY_TEXTURE_WIDTH, EMPTY_TEXTURE_HEIGHT, 1.0f)
    );
    private static final Identifier DONKEY_KONG_TEXTURE = Identifier.of(DonkeyKong.MOD_ID, "textures/gui/game/donkey_kong.png");
    private static final int DONKEY_KONG_TEXTURE_WIDTH = 138;
    private static final int DONKEY_KONG_TEXTURE_HEIGHT = 126;
    public static final List<Texture> DONKEY_KONG_BARREL_TEXTURES = List.of(
            new Texture(DONKEY_KONG_TEXTURE, 0, 42, 46, 42, DONKEY_KONG_TEXTURE_WIDTH, DONKEY_KONG_TEXTURE_HEIGHT, 1.0f),
            new Texture(DONKEY_KONG_TEXTURE, 46, 42, 46, 42, DONKEY_KONG_TEXTURE_WIDTH, DONKEY_KONG_TEXTURE_HEIGHT, 1.0f),
            new Texture(DONKEY_KONG_TEXTURE, 92, 42, 46, 42, DONKEY_KONG_TEXTURE_WIDTH, DONKEY_KONG_TEXTURE_HEIGHT, 1.0f),
            new Texture(EMPTY_TEXTURE, 0, 84, 46, 42, EMPTY_TEXTURE_WIDTH, EMPTY_TEXTURE_HEIGHT, 1.0f));
    public static final List<Texture> DONKEY_KONG_IDLE_TEXTURES = List.of(
            new Texture(DONKEY_KONG_TEXTURE, 0, 0, 46, 42, DONKEY_KONG_TEXTURE_WIDTH, DONKEY_KONG_TEXTURE_HEIGHT, 1.0f));
    public static final List<Texture> DONKEY_KONG_EXTRA_TEXTURES = List.of(
            new Texture(DONKEY_KONG_TEXTURE, 46, 0, 46, 42, DONKEY_KONG_TEXTURE_WIDTH, DONKEY_KONG_TEXTURE_HEIGHT, 1.0f),
            new Texture(DONKEY_KONG_TEXTURE, 92, 0, 46, 42, DONKEY_KONG_TEXTURE_WIDTH, DONKEY_KONG_TEXTURE_HEIGHT, 1.0f),
            new Texture(EMPTY_TEXTURE, 0, 0, 46, 42, EMPTY_TEXTURE_WIDTH, EMPTY_TEXTURE_HEIGHT, 1.0f));
}