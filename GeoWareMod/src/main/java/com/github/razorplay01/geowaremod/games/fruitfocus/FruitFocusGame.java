package com.github.razorplay01.geowaremod.games.fruitfocus;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.geowaremod.GameSounds;
import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.Timer;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.render.CustomDrawContext;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.stage.Game;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.lwjgl.glfw.GLFW;

import java.util.*;

@Getter
public class FruitFocusGame extends Game {
    private final float scaleFactor = 1.5f;
    public static final Identifier fruitsTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/fruitfocus/fruits.png");

    private static final int GAME_WIDTH = 208;
    private static final int GAME_HEIGHT = 148;
    private final Random random = Random.create();

    private final List<FruitSlot> fruitSlots = new ArrayList<>();
    private final List<FruitSlot> selectionSlots = new ArrayList<>();
    private final Set<FruitSlot> hiddenSlots = new HashSet<>();
    private final Set<FruitSlot> discoveredSlots = new HashSet<>();

    private static final int NUM_SLOTS = 12;
    private static final int NUM_SELECTION_SLOTS = 7;
    private static final int SLOT_COLOR = 0xFF444444;

    private static final int HIDE_DURATION = 3;
    private static final int CHOOSE_DURATION = 6;

    private final Timer hideTimer;
    private final Timer chooseTimer;

    private int fruitsToHide;
    private long lastHideTime;
    private List<Fruit> availableFruits;

    private boolean hasMadeChoice;
    private boolean hasPlayedEndSound; // Bandera para evitar repetición de sonidos de fin

    private enum GameState {
        SHOWING,
        HIDING,
        CHOOSING
    }

    private GameState currentGameState;
    private Fruit targetFruit;
    private FruitSlot targetSlot;

    private final float soundVolume = 0.3f;
    private final int completePoint;

    public FruitFocusGame(GameScreen screen, int timeLimitSeconds, int prevScore, int hideDurationSeconds, int fruitsToHide, int completePoint) {
        super(screen, 5, timeLimitSeconds, prevScore);
        this.completePoint = completePoint;
        this.hideTimer = new Timer(hideDurationSeconds * 1000L); // Tiempo para mostrar frutas
        this.chooseTimer = new Timer(CHOOSE_DURATION * 1000L); // Tiempo para elegir
        this.fruitsToHide = Math.min(fruitsToHide, NUM_SLOTS); // Asegurar que no exceda NUM_SLOTS
        this.hasMadeChoice = false;
        this.hasPlayedEndSound = false; // Inicializar bandera
        this.currentGameState = GameState.SHOWING; // Comenzar en SHOWING
    }

    @Override
    public void init() {
        super.init();
        availableFruits = Arrays.asList(Fruit.values());

        float[][] slotCoordinates = {
                {48, 26}, {79, 11}, {25, 48}, {51, 65}, {40, 91}, {75, 46},
                {87, 81}, {101, 31}, {113, 63}, {125, 20}, {138, 45}, {141, 87}
        };

        fruitSlots.clear(); // Limpiar por si acaso
        for (float[] slotCoordinate : slotCoordinates) {
            float x = screen.getGameScreenXPos() + slotCoordinate[0] * scaleFactor;
            float y = screen.getGameScreenYPos() + slotCoordinate[1] * scaleFactor;

            Fruit fruit = availableFruits.get(random.nextInt(availableFruits.size()));
            fruitSlots.add(new FruitSlot(fruit, x, y, true, // Comenzar ocultas
                    new RectangleHitbox("slot", x, y, 16 * scaleFactor, 16 * scaleFactor, 0, 0, SLOT_COLOR),
                    SLOT_COLOR, screen));
        }

        initSelectSlots();

        discoveredSlots.clear();
        hiddenSlots.clear();
        currentGameState = GameState.SHOWING;
        hasPlayedEndSound = false; // Reiniciar bandera al iniciar
    }

    private void initSelectSlots() {
        int yPos = (int) (107 * scaleFactor);
        int xPos = (int) (24 * scaleFactor);
        selectionSlots.clear(); // Limpiar por si acaso
        for (int i = 0; i < NUM_SELECTION_SLOTS; i++) {
            float x = screen.getGameScreenXPos() + xPos + i * (24 * scaleFactor);
            float y = screen.getGameScreenYPos() + yPos;
            Fruit fruit = availableFruits.get(i);
            selectionSlots.add(new FruitSlot(fruit, x, y, true, // Comenzar ocultas
                    new RectangleHitbox("selection", x, y, 16 * scaleFactor, 16 * scaleFactor, 0, 0, SLOT_COLOR),
                    SLOT_COLOR, screen));
        }
    }

    @Override
    public void update() {
        super.update();
        if (status == GameStatus.ACTIVE) {
            boolean shouldSelectionBeHidden = currentGameState != GameState.CHOOSING;
            if (currentGameState == GameState.SHOWING) {
                fruitSlots.forEach(slot -> slot.setHidden(false)); // Revelar en SHOWING
            }
            selectionSlots.forEach(slot -> slot.setHidden(shouldSelectionBeHidden));

            switch (currentGameState) {
                case SHOWING:
                    if (!hideTimer.isRunning() && initDelay.isFinished()) {
                        hideTimer.reset();
                        hideTimer.start();
                    }
                    if (hideTimer.isFinished()) {
                        currentGameState = GameState.HIDING;
                        lastHideTime = System.currentTimeMillis();
                    }
                    break;
                case HIDING:
                    hideFruits();
                    break;
                case CHOOSING:
                    if (chooseTimer.isFinished()) {
                        if (!hasMadeChoice) {
                            // Si no hizo ninguna elección, consideramos que falló
                            playSound(GameSounds.FRUITFOCUS_ERROR, soundVolume, 1.0f);
                        }
                        hasMadeChoice = false;
                        if (!discoveredSlots.contains(targetSlot)) {
                            // Si falló, mantenemos el mismo targetSlot
                            startChoosing(); // Reiniciar CHOOSING con la misma fruta
                        }
                        // Si acertó, checkGuess ya avanzó
                    }
                    break;
            }
        } else if (status == GameStatus.ENDING && !hasPlayedEndSound && discoveredSlots.size() != NUM_SLOTS) {
            // Reproducir sonido solo si no se completó el juego
            playSound(GameSounds.FRUITFOCUS_DEAD, soundVolume, 1.0f); // Perdió
            hasPlayedEndSound = true; // Evitar repetición
        }
    }

    private void selectNewTargetSlot() {
        List<FruitSlot> availableSlots = new ArrayList<>(hiddenSlots);
        availableSlots.removeAll(discoveredSlots);

        if (!availableSlots.isEmpty()) {
            targetSlot = availableSlots.get(random.nextInt(availableSlots.size()));
            targetFruit = targetSlot.getFruit();
        } else {
            status = GameStatus.ENDING;
        }
    }

    private void hideFruits() {
        if (currentGameState != GameState.HIDING) return;

        long currentTime = System.currentTimeMillis();
        // Intervalo para ocultar cada fruta, basado en HIDE_DURATION (3 segundos)
        long hideInterval = (HIDE_DURATION * 1000L) / Math.max(1, fruitsToHide);
        if (currentTime - lastHideTime >= hideInterval && hiddenSlots.size() < fruitsToHide) {
            List<FruitSlot> slotsToHide = fruitSlots.stream()
                    .filter(slot -> !slot.isHidden() &&
                            !discoveredSlots.contains(slot) &&
                            !hiddenSlots.contains(slot))
                    .toList();

            if (!slotsToHide.isEmpty()) {
                FruitSlot slotToHide = slotsToHide.get(random.nextInt(slotsToHide.size()));
                slotToHide.setHidden(true);
                hiddenSlots.add(slotToHide);
                lastHideTime = currentTime;
            }

            if (hiddenSlots.size() >= fruitsToHide) {
                startChoosing();
            }
        }
    }

    private void checkGuess(Fruit guessedFruit) {
        if (hasMadeChoice) return; // Evitar selecciones adicionales

        hasMadeChoice = true;
        boolean isCorrect = guessedFruit == targetFruit;

        if (isCorrect) {
            targetSlot.setHidden(false);
            discoveredSlots.add(targetSlot);

            addScore(completePoint);

            playSound(GameSounds.FRUITFOCUS_CORRECT, soundVolume, 1.0f);
            // Avanzar a la siguiente ronda inmediatamente
            if (discoveredSlots.size() == NUM_SLOTS) {
                status = GameStatus.ENDING;
                playSound(GameSounds.FRUITFOCUS_WIN, soundVolume, 1.0f); // Ganó
                hasPlayedEndSound = true; // Marcar como reproducido
            } else {
                targetSlot.setColor(SLOT_COLOR);
                selectNewTargetSlot();
                startChoosing();
            }
        } else {
            playSound(GameSounds.FRUITFOCUS_ERROR, soundVolume, 1.0f);
            // El temporizador sigue corriendo hasta que termine
        }
    }

    private void startChoosing() {
        if (targetSlot == null) {
            selectNewTargetSlot();
            if (targetSlot == null) {
                status = GameStatus.ENDING;
                return;
            }
        }

        currentGameState = GameState.CHOOSING;
        chooseTimer.reset();
        chooseTimer.start();
        hasMadeChoice = false; // Asegurar que el jugador pueda elegir de nuevo
    }

    @Override
    public void handleMouseInput(double mouseX, double mouseY, int button) {
        if ((button == GLFW.GLFW_MOUSE_BUTTON_LEFT && currentGameState == GameState.CHOOSING && !hasMadeChoice) && status == GameStatus.ACTIVE) {
            for (FruitSlot slot : selectionSlots) {
                if (slot.getHitbox().isMouseOver(mouseX, mouseY)) {
                    checkGuess(slot.getFruit());
                    break;
                }
            }
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier marcoTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/fruitfocus/fondo.png");
        context.drawTexture(marcoTexture, screen.getGameScreenXPos(), screen.getGameScreenYPos(),
                getScreenWidth(), getScreenHeight(), 0, 0, 208, 148, 208, 148);
    }

    @Override
    public int getScreenWidth() {
        return (int) (GAME_WIDTH * scaleFactor);
    }

    @Override
    public int getScreenHeight() {
        return (int) (GAME_HEIGHT * scaleFactor);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        CustomDrawContext customDrawContext = CustomDrawContext.wrap(context);
        for (FruitSlot slot : fruitSlots) {
            slot.render(context);
        }

        if (currentGameState == GameState.CHOOSING && targetSlot != null) {
            int slotSize = (int) (16 * scaleFactor);
            int borderSize = slotSize + 2;
            Identifier contornoTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/fruitfocus/contorno.png");
            context.drawTexture(contornoTexture, (int) targetSlot.getXPos() - 1, (int) targetSlot.getYPos() - 1, borderSize, borderSize, 0, 0, 16, 16, 16, 16);
        }

        for (FruitSlot slot : selectionSlots) {
            slot.render(context);
            if (currentGameState == GameState.CHOOSING && !hasMadeChoice) {
                if (slot.getHitbox().isMouseOver(mouseX, mouseY)) {
                    int slotSize = (int) (16 * scaleFactor);
                    int borderSize = slotSize + 2;
                    Identifier contornoTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/fruitfocus/contorno2.png");
                    context.drawTexture(contornoTexture, (int) slot.getXPos() - 1, (int) slot.getYPos() - 1, borderSize, borderSize, 0, 0, 16, 16, 16, 16);
                }
            }
        }

        switch (currentGameState) {
            case SHOWING -> {
                customDrawContext.drawText(getTextRenderer(), "SHOWING", screen.getGameScreenXPos() + getScreenWidth() + 20, screen.getGameScreenYPos() + 40, 0xFFFFFFFF, true);
                hideTimer.renderTimer(context, screen.getGameScreenXPos() + getScreenWidth() + 20, screen.getGameScreenYPos() + 60);
            }
            case HIDING -> {
                customDrawContext.drawText(getTextRenderer(), "HIDING", screen.getGameScreenXPos() + getScreenWidth() + 20, screen.getGameScreenYPos() + 40, 0xFFFFFFFF, true);
                // No mostramos temporizador en HIDING
            }
            case CHOOSING -> {
                customDrawContext.drawText(getTextRenderer(), "CHOOSING", screen.getGameScreenXPos() + getScreenWidth() + 20, screen.getGameScreenYPos() + 40, 0xFFFFFFFF, true);
                chooseTimer.renderTimer(context, screen.getGameScreenXPos() + getScreenWidth() + 20, screen.getGameScreenYPos() + 60);
            }
        }
    }
}