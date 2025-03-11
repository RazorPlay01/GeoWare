package com.github.razorplay01.geoware.hanoitowers.game.util;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

@Getter
@Setter
public class Timer {
    private long startTime; // Tiempo de inicio del temporizador
    private long duration; // Duración del temporizador en milisegundos
    private long pausedTime; // Tiempo acumulado mientras el temporizador estaba pausado
    private boolean running; // Indica si el temporizador está en marcha
    private boolean paused; // Indica si el temporizador está pausado

    /**
     * Constructor para crear un temporizador con una duración específica.
     *
     * @param durationMillis Duración del temporizador en milisegundos.
     */
    public Timer(long durationMillis) {
        this.duration = durationMillis;
        this.running = false;
        this.paused = false;
        this.pausedTime = 0;
    }

    /**
     * Inicia el temporizador.
     */
    public void start() {
        this.startTime = System.currentTimeMillis();
        this.running = true;
        this.paused = false;
        this.pausedTime = 0;
    }

    /**
     * Detiene el temporizador.
     */
    public void stop() {
        this.running = false;
        this.paused = false;
        this.pausedTime = 0;
    }

    /**
     * Pausa el temporizador.
     */
    public void pause() {
        if (running && !paused) {
            paused = true;
            pausedTime = System.currentTimeMillis() - startTime;
        }
    }

    /**
     * Reanuda el temporizador.
     */
    public void resume() {
        if (running && paused) {
            paused = false;
            startTime = System.currentTimeMillis() - pausedTime;
            pausedTime = 0;
        }
    }

    /**
     * Reinicia el temporizador con la misma duración.
     */
    public void reset() {
        this.startTime = System.currentTimeMillis();
        this.running = true;
        this.paused = false;
        this.pausedTime = 0;
    }

    /**
     * Verifica si el temporizador ha terminado.
     *
     * @return true si el temporizador ha terminado, false en caso contrario.
     */
    public boolean isFinished() {
        if (!running || paused) {
            return false;
        }
        return System.currentTimeMillis() - startTime >= duration;
    }

    /**
     * Obtiene el tiempo transcurrido desde que se inició el temporizador.
     *
     * @return Tiempo transcurrido en milisegundos.
     */
    public long getElapsedTime() {
        if (paused) {
            return pausedTime;
        }
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Obtiene el tiempo restante para que el temporizador termine.
     *
     * @return Tiempo restante en milisegundos.
     */
    public long getRemainingTime() {
        return Math.max(0, duration - getElapsedTime());
    }

    public void renderTimer(DrawContext context, int xPos, int yPos) {
        long remainingTime = this.getRemainingTime() / 1000;
        String scoreMessage = String.format("%02d:%02d", (remainingTime / 60), (remainingTime % 60));
        context.drawText(MinecraftClient.getInstance().textRenderer, scoreMessage,
                xPos, yPos, 0xFFFFFF00, true);
    }
}