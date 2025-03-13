package com.github.razorplay01.modbubblepuzzle.util;

/**
 * La clase `GameTask` representa una tarea programada que se ejecutará después de un
 * período de tiempo específico (en milisegundos). Utiliza un `Timer` interno para
 * controlar el tiempo restante antes de ejecutar la acción.
 * <p>
 * Esta clase es útil para programar eventos o acciones que deben ocurrir después de
 * un retraso en el juego, como animaciones, efectos temporales o eventos temporizados.
 */
public class GameTask {
    private final Runnable action; // La acción que se ejecutará cuando el temporizador llegue a cero
    private final Timer timer; // Temporizador interno para controlar el tiempo restante
    private boolean isCompleted; // Indica si la tarea ha sido completada

    /**
     * Constructor para crear una nueva tarea programada.
     *
     * @param action  La acción que se ejecutará cuando el temporizador llegue a cero.
     *                Debe ser una implementación de la interfaz `Runnable`.
     * @param delayMs El tiempo de retraso en milisegundos antes de que la acción se ejecute.
     */
    public GameTask(Runnable action, long delayMs) {
        this.action = action;
        this.timer = new Timer(delayMs); // Crear un temporizador con el retraso especificado
        this.isCompleted = false;
    }

    /**
     * Actualiza el estado de la tarea. Debe ser llamado en cada actualización del juego.
     * Verifica si el temporizador ha terminado y, si es así, ejecuta la acción.
     *
     * @return `true` si la acción fue ejecutada (es decir, si el temporizador ha terminado),
     * `false` si la tarea aún está en espera.
     */
    public boolean update() {
        if (!isCompleted && timer.isFinished()) {
            action.run(); // Ejecuta la acción cuando el temporizador llega a cero
            isCompleted = true; // Marca la tarea como completada
            return true; // Indica que la tarea ha terminado
        }
        return false; // Indica que la tarea aún está en progreso
    }

    /**
     * Pausa la tarea.
     */
    public void pause() {
        timer.pause();
    }

    /**
     * Reanuda la tarea.
     */
    public void resume() {
        timer.resume();
    }

    /**
     * Verifica si la tarea ha sido completada.
     *
     * @return `true` si la tarea ha sido completada, `false` en caso contrario.
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Reinicia la tarea con un nuevo retraso.
     *
     * @param delayMs El nuevo tiempo de retraso en milisegundos.
     */
    public void reset(long delayMs) {
        timer.setDuration(delayMs); // Cambia la duración del temporizador
        timer.start(); // Reinicia el temporizador
        isCompleted = false; // Marca la tarea como no completada
    }
}