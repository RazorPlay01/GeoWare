package com.github.razorplay01.geowaremod.games.guitarhero;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.Particle;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.stage.Game;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class GuitarHeroGame extends Game {
    private static final int GAME_WIDTH = 200;
    private static final int GAME_HEIGHT = 340; // Alto ajustado a 340
    private static final int NOTE_SIZE = 20;
    public static final int TARGET_Y = GAME_HEIGHT - 50;

    private final List<MovingNote> movingNotes;
    private final List<Particle> particles;
    private final List<Note> songNotes;
    @Getter
    private long gameStartTime;

    public GuitarHeroGame(GameScreen screen, int timeLimitSeconds, int prevScore) {
        super(screen, 0, timeLimitSeconds, prevScore);
        movingNotes = new ArrayList<>();
        particles = new ArrayList<>();
        songNotes = createSong(); // Usamos la partitura definida en código
    }

    private List<Note> createSong() {
        List<Note> notes = new ArrayList<>();

        notes.add(new Note(GLFW.GLFW_KEY_F, 1000, 10500));   // 3500 ms
        notes.add(new Note(GLFW.GLFW_KEY_G, 3000, 12500));   // 3500 ms
        notes.add(new Note(GLFW.GLFW_KEY_H, 5000, 15000));   // 4000 ms
        notes.add(new Note(GLFW.GLFW_KEY_J, 7000, 16500));  // 3500 ms
        notes.add(new Note(GLFW.GLFW_KEY_F, 9000, 19000));  // 4000 ms
        notes.add(new Note(GLFW.GLFW_KEY_G, 11000, 20500)); // 3500 ms
        notes.add(new Note(GLFW.GLFW_KEY_H, 13000, 23000)); // 4000 ms
        notes.add(new Note(GLFW.GLFW_KEY_J, 15000, 24500)); // 3500 ms
        notes.add(new Note(GLFW.GLFW_KEY_F, 17000, 27000)); // 4000 ms
        notes.add(new Note(GLFW.GLFW_KEY_G, 19000, 29500)); // 3500 ms
        return notes;
    }

    @Override
    public void init() {
        super.init();
        gameStartTime = System.currentTimeMillis();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (status == GameStatus.ACTIVE) {
            long currentTime = System.currentTimeMillis() - gameStartTime;

            Iterator<Note> noteIterator = songNotes.iterator();
            while (noteIterator.hasNext()) {
                Note note = noteIterator.next();
                if (currentTime < note.spawnTime()) break; // Optimización
                if (currentTime >= note.spawnTime() && !isNoteSpawned(note)) {
                    movingNotes.add(new MovingNote(NOTE_SIZE, note.spawnTime(), note.hitTime(), note.keyCode(), this));
                    noteIterator.remove();
                }
            }

            movingNotes.removeIf(note -> !note.isActive());
            for (MovingNote note : movingNotes) {
                note.update(delta);
            }
            particles.removeIf(Particle::isFinished);
            for (Particle particle : particles) {
                particle.update();
            }
        }

        // Fondo simple (gris)
        context.fill(screen.getGameScreenXPos(), screen.getGameScreenYPos(),
                screen.getGameScreenXPos() + GAME_WIDTH, screen.getGameScreenYPos() + GAME_HEIGHT,
                0xFF808080);

        // Renderizar notas y partículas
        for (MovingNote note : movingNotes) {
            note.draw(context);
        }
        for (Particle particle : particles) {
            particle.render(context, delta);
        }

        // Zona objetivo con colores por tecla
        context.fill(screen.getGameScreenXPos(), screen.getGameScreenYPos() + TARGET_Y,
                screen.getGameScreenXPos() + 50, screen.getGameScreenYPos() + TARGET_Y + NOTE_SIZE, 0xFFFF0000); // F - Rojo
        context.fill(screen.getGameScreenXPos() + 50, screen.getGameScreenYPos() + TARGET_Y,
                screen.getGameScreenXPos() + 100, screen.getGameScreenYPos() + TARGET_Y + NOTE_SIZE, 0xFF00FF00); // G - Verde
        context.fill(screen.getGameScreenXPos() + 100, screen.getGameScreenYPos() + TARGET_Y,
                screen.getGameScreenXPos() + 150, screen.getGameScreenYPos() + TARGET_Y + NOTE_SIZE, 0xFF0000FF); // H - Azul
        context.fill(screen.getGameScreenXPos() + 150, screen.getGameScreenYPos() + TARGET_Y,
                screen.getGameScreenXPos() + 200, screen.getGameScreenYPos() + TARGET_Y + NOTE_SIZE, 0xFFFFFF00); // J - Amarillo

        // Depuración temporal
        if (status == GameStatus.ACTIVE) {
            long currentTime = System.currentTimeMillis() - gameStartTime;
            context.drawText(getTextRenderer(), "Time: " + currentTime, screen.getGameScreenXPos(), screen.getGameScreenYPos() - 20, 0xFFFFFFFF, false);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    @Override
    public int getScreenWidth() {
        return GAME_WIDTH;
    }

    @Override
    public int getScreenHeight() {
        return GAME_HEIGHT;
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        Iterator<MovingNote> iterator = movingNotes.iterator();
        while (iterator.hasNext()) {
            MovingNote note = iterator.next();
            if (note.checkKey(keyCode)) {
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public void update() {
        super.update();
    }

    public void addParticle(Particle particle) {
        if (particles.size() < 50) { // Límite de partículas
            particles.add(particle);
        }
    }

    private boolean isNoteSpawned(Note note) {
        return movingNotes.stream().anyMatch(n -> n.getKey() == note.keyCode() && Math.abs(n.getSpawnTime() - note.spawnTime()) < 100);
    }
}