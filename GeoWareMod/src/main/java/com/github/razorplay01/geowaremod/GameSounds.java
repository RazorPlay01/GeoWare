package com.github.razorplay01.geowaremod;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class GameSounds {
    // Tetris
    public static final SoundEvent TETRIS_BAJAR = register("bajar_tetris");
    public static final SoundEvent TETRIS_END = register("end_tetris");
    public static final SoundEvent TETRIS_LINE = register("line_tetris");
    public static final SoundEvent TETRIS_MOVE = register("move_tetris");
    public static final SoundEvent TETRIS_ROTATE = register("rotate_tetris");

    // Arkanoid
    public static final SoundEvent ARKANOID_DUPLICAR = register("duplicar_arkanoid");
    public static final SoundEvent ARKANOID_END = register("end_arkanoid");
    public static final SoundEvent ARKANOID_GOLPE = register("golpe_arkanoid");
    public static final SoundEvent ARKANOID_REBOTE_BARRA = register("rebote_barra_arkanoid");
    public static final SoundEvent ARKANOID_REDUCCION = register("reduccion_arkanoid");
    public static final SoundEvent ARKANOID_UPGRADE_BAR = register("upgrade_bar_arkanoid");

    // Bubble Puzzle
    public static final SoundEvent BUBBLEPUZZLE_DISPARO = register("disparo_bubblepuzzle");
    public static final SoundEvent BUBBLEPUZZLE_END = register("end_bubblepuzzle");
    public static final SoundEvent BUBBLEPUZZLE_POP = register("pop_bubblepuzzle");

    // Donkey Kong
    public static final SoundEvent DONKEYKONG_BARRIL_EXPLOSION = register("barril_explosion_donkeykong");
    public static final SoundEvent DONKEYKONG_DEAD = register("dead_donkeykong");
    public static final SoundEvent DONKEYKONG_JUMP = register("jump_donkeykong");
    public static final SoundEvent DONKEYKONG_MARTILLO = register("martillo_donkeykong");
    public static final SoundEvent DONKEYKONG_WIN = register("win_donkeykong");

    // Fruit Focus
    public static final SoundEvent FRUITFOCUS_CORRECT = register("correct_fruitfocus");
    public static final SoundEvent FRUITFOCUS_DEAD = register("dead_fruitfocus");
    public static final SoundEvent FRUITFOCUS_ERROR = register("error_fruitfocus");
    public static final SoundEvent FRUITFOCUS_WIN = register("win_fruitfocus");

    // Galaga
    public static final SoundEvent GALAGA_DEAD = register("dead_galaga");
    public static final SoundEvent GALAGA_KILL = register("kill_galaga");
    public static final SoundEvent GALAGA_SHOOT = register("shoot_galaga");

    // Hanoi Towers
    public static final SoundEvent HANOITOWERS_DEAD = register("dead_hanoitowers");
    public static final SoundEvent HANOITOWERS_SELECT = register("select_hanoitowers");
    public static final SoundEvent HANOITOWERS_THROW = register("throw_hanoitowers");
    public static final SoundEvent HANOITOWERS_WIN = register("win_hanoitowers");

    // Keybind
    public static final SoundEvent KEYBIND_ERROR = register("error_keybind");
    public static final SoundEvent KEYBIND_PERFECT = register("perfect_keybind");

    // Robot Factory
    public static final SoundEvent ROBOTFACTORY_ENSAMBLE = register("ensamble_robotfactory");
    public static final SoundEvent ROBOTFACTORY_SELECT = register("select_robotfactory");

    // Scary Maze
    public static final SoundEvent SCARYMAZE_DEAD = register("dead_scarymaze");
    public static final SoundEvent SCARYMAZE_SELECT = register("select_scarymaze");
    public static final SoundEvent SCARYMAZE_WIN = register("win_scarymaze");

    private static SoundEvent register(String name) {
        Identifier id = Identifier.of(GeoWareMod.MOD_ID, name);
        SoundEvent soundEvent = SoundEvent.of(id);
        return Registry.register(Registries.SOUND_EVENT, id, soundEvent);
    }

    public static void registerSounds() {
        GeoWareMod.LOGGER.info("Registering all game sounds");
    }
}