package com.github.razorplay01.geowaremod.games.guitarhero;

/**
 * @param keyCode   Código de la tecla (F, G, H, J)
 * @param spawnTime Tiempo en ms en que aparece
 * @param hitTime   Tiempo en ms en que debe ser presionada
 */
public record Note(int keyCode, long spawnTime, long hitTime) {
}