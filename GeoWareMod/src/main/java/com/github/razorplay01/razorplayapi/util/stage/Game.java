package com.github.razorplay01.razorplayapi.util.stage;

import com.github.razorplay01.geoware.geowarecommon.network.packet.ScorePacket;
import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.geowaremod.network.FabricCustomPayload;
import com.github.razorplay01.razorplayapi.util.FloatingText;
import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.GameTask;
import com.github.razorplay01.razorplayapi.util.Timer;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class Game implements IGame {
    protected final GameScreen screen;
    protected final MinecraftClient client = MinecraftClient.getInstance();
    protected final TextRenderer textRenderer = client.textRenderer;

    protected final Timer initDelay;
    protected final Timer gameDutarion;
    protected final Timer finalTimer;
    protected List<GameTask> tasks = new ArrayList<>();
    protected List<GameTask> newTasks = new ArrayList<>();
    protected GameStatus status;
    protected final int prevScore;

    protected final List<FloatingText> floatingTexts = new ArrayList<>();
    protected int gameScore;

    protected Game(Screen screen, int initDelay, int timeLimitSeconds, int prevScore) {
        this.screen = (GameScreen) screen;
        this.initDelay = new Timer(initDelay * 1000L);
        this.gameDutarion = new Timer(timeLimitSeconds * 1000L);
        this.finalTimer = new Timer(5000L);
        this.prevScore = prevScore;
    }

    @Override
    public void init() {
        this.status = GameStatus.INITIALIZING;
    }

    @Override
    public void update() {
        switch (status) {
            case INITIALIZING -> {
                if (!this.initDelay.isRunning()) {
                    this.initDelay.start();
                }
                if (this.initDelay.isFinished()) {
                    this.status = GameStatus.ACTIVE;
                }
            }
            case ACTIVE -> {
                if (!this.gameDutarion.isRunning()) {
                    this.gameDutarion.start();
                }
                if (this.gameDutarion.isFinished()) {
                    this.status = GameStatus.ENDING;
                }
            }
            case ENDING -> {
                if (!this.finalTimer.isRunning()) {
                    this.finalTimer.start();
                }
                if (this.finalTimer.isFinished()) {
                    this.status = GameStatus.FINISHED;
                    ScorePacket scorePacket = new ScorePacket(gameScore);
                    GeoWareMod.LOGGER.info("Packet: " + scorePacket.getPacketId() + ", send to server.");
                    ClientPlayNetworking.send(new FabricCustomPayload(new ScorePacket(gameScore)));
                    this.screen.close();
                }
            }
            default -> {
                // []
            }
        }


        // Update all FloatingTexts
        floatingTexts.removeIf(text -> !text.isActive());
        // Update all tasks
        List<GameTask> tasksSnapshot = new ArrayList<>(tasks);
        List<GameTask> tasksToRemove = new ArrayList<>();

        for (GameTask task : tasksSnapshot) {
            if (task.update()) {
                tasksToRemove.add(task);
            }
        }
        tasks.removeAll(tasksToRemove);

        // Transferir nuevas tareas y limpiar newTasks
        if (!newTasks.isEmpty()) {
            tasks.addAll(newTasks);
            newTasks.clear(); // Limpiar para evitar duplicados
        }
    }

    public void scheduleTask(Runnable action, long delayMs) {
        newTasks.add(new GameTask(action, delayMs)); // Siempre agregar a newTasks
    }

    public void pause() {
        if (this.status == GameStatus.NOT_INITIALIZE) {
            return;
        }
        this.initDelay.pause();
        this.gameDutarion.pause();
        this.finalTimer.pause();
        this.status = GameStatus.PAUSED;
    }


    public void resume() {
        if (this.status == GameStatus.NOT_INITIALIZE) {
            return;
        }
        this.initDelay.resume();
        this.gameDutarion.resume();
        this.finalTimer.resume();
        if (this.initDelay.isRunning()) {
            this.status = GameStatus.INITIALIZING;
        } else if (this.gameDutarion.isRunning()) {
            this.status = GameStatus.ACTIVE;
        } else if (this.finalTimer.isRunning()) {
            this.status = GameStatus.ENDING;
        } else {
            this.status = GameStatus.NOT_INITIALIZE;
        }
    }

    //game.update();
    //game.scheduleTask(() -> System.out.println("¡Tarea completada después de 3 segundos!"), 3000);

    /**
     * Añade puntos al gameScore
     *
     * @param points Cantidad de puntos a añadir
     */
    public void addScore(int points, float xPos, float yPos) {
        gameScore += points;
        String scoreText = "" + points;
        floatingTexts.add(new FloatingText(xPos, yPos, scoreText, 0.8f));
    }

    public void addScore(int points) {
        gameScore += points;
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {

    }

    public void handleMouseInput(double mouseX, double mouseY, int button) {

    }

    public void handlemouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {

    }

    public void keyReleased(int keyCode, int scanCode, int modifiers) {
    }

    public void playSound(SoundEvent sound, float volume, float pitch) {
        MinecraftClient.getInstance().getSoundManager().play(
                new PositionedSoundInstance(
                        sound.getId(),
                        SoundCategory.MASTER,
                        volume, // Volumen
                        pitch, // Tono
                        Random.create(),
                        false, // No repetir
                        0, // Retraso
                        SoundInstance.AttenuationType.NONE,
                        0.0, 0.0, 0.0, // Posición (no importa para MASTER)
                        true // Relativo
                )
        );
    }

    protected void playSound(SoundEvent sound) {
        playSound(sound, 1.0f, 1.0f);
    }
}