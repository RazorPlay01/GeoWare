package com.github.razorplay01.geowaremod.games.guitarhero;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.Particle;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.stage.Game;
import com.github.razorplay01.razorplayapi.util.texture.Animation;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.watermedia.api.player.videolan.MusicPlayer;

import java.util.*;

public class GuitarHeroGame extends Game {
    private static final int BASE_SCREEN_WIDTH = 96;
    private static final int BASE_SCREEN_HEIGHT = 134;
    private static final float SCALE = 1.5f;
    private static final int BASE_NOTE_WIDTH = 17;
    private static final int BASE_NOTE_HEIGHT = 13;
    private static final int BASE_ZONE_WIDTH = 19;
    private static final int BASE_ZONE_HEIGHT = 16;
    private static final int NOTE_SPAWN_Y_OFFSET = -10;
    private static final long PRESS_WINDOW = 200; // ±200ms para acertar

    private final List<Particle> particles = new ArrayList<>();
    private final List<Note> notes = new ArrayList<>();
    private final Key[] keys = new Key[4];
    private final Animation[] noteParticles = new Animation[4];
    private final List<NoteData> noteData = new ArrayList<>();
    private final List<EventData> eventData = new ArrayList<>();
    @Getter
    private final UUID musicPlayerId;

    private final String[] keyNames = {"F", "G", "H", "J"};
    private final int[] keyUOffsets = {0, 19, 38, 57};
    private final int[] noteUOffsets = {0, 17, 34, 51}; // F, G, H, J

    private BossBar bossBar = new BossBar();

    public GuitarHeroGame(Screen screen, int prevScore, UUID musicPlayerId) {
        super(screen, 0, 129, prevScore); // 64s de juego (56s a 120s)
        this.musicPlayerId = musicPlayerId;
        initializeNoteData();
        initializeEventData();
    }

    private void initializeNoteData() {
        // Partitura ajustada para t=56s a t=120s (56000ms a 120000ms)
        noteData.addAll(List.of(
                new NoteData("F", 56000 + 2700, 56000 + 4200),
                new NoteData("H", 56000 + 3500, 56000 + 5000),
                new NoteData("H", 56000 + 4700, 56000 + 6200),
                new NoteData("F", 56000 + 8500, 56000 + 10000),
                new NoteData("G", 56000 + 9700, 56000 + 11200),
                new NoteData("G", 56000 + 11700, 56000 + 13200),
                new NoteData("F", 56000 + 16500, 56000 + 18000),
                new NoteData("J", 56000 + 16700, 56000 + 18200),
                new NoteData("J", 56000 + 23700, 56000 + 25200),
                new NoteData("F", 56000 + 24500, 56000 + 26000),
                new NoteData("J", 56000 + 24700, 56000 + 26200),
                new NoteData("H", 56000 + 25500, 56000 + 27000),
                new NoteData("H", 56000 + 40500, 56000 + 42000),
                new NoteData("J", 56000 + 49500, 56000 + 51000),
                new NoteData("G", 56000 + 49700, 56000 + 51200),
                new NoteData("F", 56000 + 50700, 56000 + 52200),
                new NoteData("J", 56000 + 62500, 56000 + 64000)
        ));
    }

    private void initializeEventData() {
        eventData.addAll(List.of(
                new EventData(56000 + 3000, () -> bossBar.removeoclockLife(1)),
                new EventData(56000 + 4200, () -> bossBar.removeoclockLife(1)),
                new EventData(56000 + 5000, () -> bossBar.removeoclockLife(1)),

                new EventData(56000 + 3000, () -> bossBar.removebbnosLife(1)),
                new EventData(56000 + 4200, () -> bossBar.removebbnosLife(1)),
                new EventData(56000 + 5000, () -> bossBar.removebbnosLife(1))
                /*,
                new EventData(56000 + 6200, () -> ),
                new EventData(56000 + 9200, () -> ),
                new EventData(56000 + 10000, () -> ),
                new EventData(56000 + 11200, () -> ),
                new EventData(56000 + 13200, () -> ),
                new EventData(56000 + 18000, () -> ),
                new EventData(56000 + 18200, () -> ),
                new EventData(56000 + 19000, () -> ),
                new EventData(56000 + 19200, () -> ),
                new EventData(56000 + 22000, () -> ),
                new EventData(56000 + 22200, () -> ),
                new EventData(56000 + 23000, () -> ),
                new EventData(56000 + 23200, () -> ),
                new EventData(56000 + 25200, () -> ),
                new EventData(56000 + 26000, () -> ),
                new EventData(56000 + 26200, () -> ),
                new EventData(56000 + 27000, () -> ),
                new EventData(56000 + 32200, () -> ),
                new EventData(56000 + 34200, () -> ),
                new EventData(56000 + 42000, () -> ),
                new EventData(56000 + 51000, () -> ),
                new EventData(56000 + 51200, () -> ),
                new EventData(56000 + 52200, () -> ),
                new EventData(56000 + 64000, () -> )*/
        ));
    }

    @Override
    public void init() {
        super.init();
        initializeKeys();
        initializeParticles();
        notes.clear();
        particles.clear();
        noteData.forEach(data -> data.spawned = false); // Reiniciar estado de spawn
        eventData.forEach(data -> data.executed = false);
    }

    private void initializeKeys() {
        int baseX = (int) (4 * SCALE); // x=0
        int baseY = screen.getGameScreenYPos() + (int) (112 * SCALE);
        int scaledZoneWidth = (int) (BASE_ZONE_WIDTH * SCALE);
        int scaledZoneHeight = (int) (BASE_ZONE_HEIGHT * SCALE);

        for (int i = 0; i < keys.length; i++) {
            int xOffset = i * (int) (23 * SCALE);
            keys[i] = new Key(
                    scaledZoneWidth,
                    scaledZoneHeight,
                    keyUOffsets[i],
                    0,
                    baseX + xOffset,
                    baseY,
                    new RectangleHitbox(
                            keyNames[i],
                            baseX + xOffset,
                            baseY,
                            scaledZoneWidth,
                            scaledZoneHeight,
                            0,
                            0,
                            0xffffff
                    )
            );
        }
    }

    private void initializeParticles() {
        String[] particlePaths = {
                "f_particle.png",
                "g_particle.png",
                "h_particle.png",
                "j_particle.png"
        };

        for (int i = 0; i < noteParticles.length; i++) {
            noteParticles[i] = new Animation(
                    Texture.createTextureList(
                            Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/" + particlePaths[i]),
                            22,
                            25,
                            22,
                            200,
                            8,
                            SCALE,
                            false
                    ),
                    2,
                    false
            );
        }
    }

    @Override
    public void update() {
        super.update();
        if (status != GameStatus.ACTIVE) return;

        MusicPlayer musicPlayer = GeoWareMod.playingAudios.entrySet().stream()
                .filter(entry -> entry.getValue().equals(musicPlayerId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        long currentTime = musicPlayer != null && !musicPlayer.isEnded() && !musicPlayer.isBroken() ?
                musicPlayer.getTime() : 0;

        for (EventData data : eventData) {
            if (!data.executed && currentTime >= data.time) {
                data.action.run();
                data.executed = true;
            }
        }

        // Generar notas según partitura
        for (NoteData data : noteData) {
            if (!data.spawned && currentTime >= data.spawnTime) {
                int keyIndex = switch (data.key) {
                    case "F" -> 0;
                    case "G" -> 1;
                    case "H" -> 2;
                    case "J" -> 3;
                    default -> -1;
                };
                if (keyIndex >= 0) {
                    spawnNote(keyIndex, keys[keyIndex], data.pressTime);
                    data.spawned = true;
                }
            }
        }

        // Actualizar partículas
        particles.removeIf(Particle::isFinished);

        // Actualizar notas
        Iterator<Note> noteIterator = notes.iterator();
        while (noteIterator.hasNext()) {
            Note note = noteIterator.next();
            note.update();
            if (note.isOutOfBounds(screen.getGameScreenYPos(), getScreenHeight())) {
                noteIterator.remove();
            }
        }

        // Cerrar pantalla en t=120s
        if (currentTime >= 120000 && status == GameStatus.ACTIVE) {
            status = GameStatus.FINISHED;
            screen.close();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        bossBar.render(context, screen, System.currentTimeMillis());
        for (Key key : keys) {
            key.render(context);
        }
        for (Particle particle : particles) {
            particle.update();
            particle.render(context, delta);
        }
        for (Note note : notes) {
            note.render(context);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier fondo = Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/fondo.png");
        int scaledWidth = (int) (BASE_SCREEN_WIDTH * SCALE);
        int scaledHeight = (int) (BASE_SCREEN_HEIGHT * SCALE);
        context.drawTexture(
                fondo,
                0, // x=0
                screen.getGameScreenYPos(),
                scaledWidth,
                scaledHeight,
                0,
                0,
                BASE_SCREEN_WIDTH,
                BASE_SCREEN_HEIGHT,
                BASE_SCREEN_WIDTH,
                BASE_SCREEN_HEIGHT
        );
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);
        int keyIndex = -1;
        switch (keyCode) {
            case GLFW.GLFW_KEY_F -> keyIndex = 0;
            case GLFW.GLFW_KEY_G -> keyIndex = 1;
            case GLFW.GLFW_KEY_H -> keyIndex = 2;
            case GLFW.GLFW_KEY_J -> keyIndex = 3;
            case GLFW.GLFW_KEY_V -> spawnNote(0, keys[0], 0); // Para pruebas
            case GLFW.GLFW_KEY_B -> spawnNote(1, keys[1], 0);
            case GLFW.GLFW_KEY_N -> spawnNote(2, keys[2], 0);
            case GLFW.GLFW_KEY_M -> spawnNote(3, keys[3], 0);
        }

        if (keyIndex >= 0) {
            keys[keyIndex].setPressed(true);
            MusicPlayer musicPlayer = GeoWareMod.playingAudios.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(musicPlayerId))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);
            long currentTime = musicPlayer != null && !musicPlayer.isEnded() && !musicPlayer.isBroken() ?
                    musicPlayer.getTime() : 0;
            // Buscar la nota más vieja para esta tecla (mayor yPos)
            int finalKeyIndex = keyIndex;
            Note closestNote = notes.stream()
                    .filter(note -> note.getKey() == keys[finalKeyIndex])
                    .max(Comparator.comparingInt(Note::getYPos))
                    .orElse(null);

            if (closestNote != null) {
                // Verificar colisión y ventana de tiempo
                if (closestNote.getHitbox().intersects(keys[keyIndex].getHitbox()) &&
                        Math.abs(currentTime - closestNote.getPressTime()) <= PRESS_WINDOW) {
                    // Colisión y dentro de la ventana: sumar puntos y añadir partícula
                    notes.remove(closestNote);
                    addScore(1);
                    addParticle(keys[keyIndex], noteParticles[keyIndex]);
                } else {
                    // Sin colisión o fuera de la ventana: eliminar nota sin sumar puntos
                    notes.remove(closestNote);
                }
            }
        }
    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {
        super.keyReleased(keyCode, scanCode, modifiers);
        int keyIndex = -1;
        switch (keyCode) {
            case GLFW.GLFW_KEY_F -> keyIndex = 0;
            case GLFW.GLFW_KEY_G -> keyIndex = 1;
            case GLFW.GLFW_KEY_H -> keyIndex = 2;
            case GLFW.GLFW_KEY_J -> keyIndex = 3;
        }
        if (keyIndex >= 0) {
            keys[keyIndex].setPressed(false);
        }
    }

    @Override
    public int getScreenWidth() {
        return (int) (BASE_SCREEN_WIDTH * SCALE);
    }

    @Override
    public int getScreenHeight() {
        return (int) (BASE_SCREEN_HEIGHT * SCALE);
    }

    private void addParticle(Key key, Animation animation) {
        particles.add(new Particle(
                key.getXPos(),
                key.getYPos(),
                (int) (BASE_ZONE_WIDTH * SCALE),
                (int) (BASE_ZONE_HEIGHT * SCALE),
                screen,
                animation
        ));
    }

    private void spawnNote(int noteIndex, Key key, long pressTime) {
        int scaledNoteWidth = (int) (BASE_NOTE_WIDTH * SCALE);
        int scaledNoteHeight = (int) (BASE_NOTE_HEIGHT * SCALE);
        int xPos = keys[noteIndex].getXPos() + (int) ((BASE_ZONE_WIDTH - BASE_NOTE_WIDTH) / 2 * SCALE);
        // Calcular velocidad: distancia desde spawn hasta zona de presión / tiempo
        float spawnY = screen.getGameScreenYPos() + scaledNoteHeight + (int) (NOTE_SPAWN_Y_OFFSET * SCALE);
        float targetY = screen.getGameScreenYPos() + (int) (112 * SCALE);
        float distance = targetY - spawnY;
        float timeMs = 1500; // 1500ms para todas las notas
        float noteSpeed = distance / (timeMs / 50.0f); // 50ms por tick (20 ticks/s)
        notes.add(new Note(
                scaledNoteWidth,
                scaledNoteHeight,
                noteUOffsets[noteIndex],
                0,
                xPos,
                (int) spawnY,
                key,
                noteSpeed,
                pressTime
        ));
    }

    private static class NoteData {
        String key;
        long spawnTime;
        long pressTime;
        boolean spawned;

        NoteData(String key, long spawnTime, long pressTime) {
            this.key = key;
            this.spawnTime = spawnTime;
            this.pressTime = pressTime;
            this.spawned = false;
        }
    }

    private static class EventData {
        long time;
        Runnable action;
        boolean executed;

        EventData(long time, Runnable action) {
            this.time = time;
            this.action = action;
            this.executed = false;
        }
    }
}