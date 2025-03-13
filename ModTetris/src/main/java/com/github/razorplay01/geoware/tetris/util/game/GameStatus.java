package com.github.razorplay01.geoware.tetris.util.game;

public enum GameStatus {
    /**
     * El juego no se está inicializando.
     */
    NOT_INITIALIZE,
    /**
     * El juego se está inicializando.
     */
    INITIALIZING,

    /**
     * El juego está activo y en ejecución.
     */
    ACTIVE,

    /**
     * El juego está pausado.
     */
    PAUSED,

    /**
     * El juego está finalizando.
     */
    ENDING,

    /**
     * El juego ha terminado (por victoria, derrota o tiempo agotado).
     */
    FINISHED
}
