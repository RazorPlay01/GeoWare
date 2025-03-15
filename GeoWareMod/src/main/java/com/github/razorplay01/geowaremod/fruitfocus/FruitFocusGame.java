package com.github.razorplay01.geowaremod.fruitfocus;

import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.Timer;
import com.github.razorplay01.razorplayapi.util.hitbox.CircleHitbox;
import com.github.razorplay01.razorplayapi.util.render.CustomDrawContext;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.stage.Game;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.*;

@Getter
public class FruitFocusGame extends Game {
    private static final int GAME_WIDTH = 20 * 15; // Ancho de la zona de juego
    private static final int GAME_HEIGHT = 20 * 10; // Alto de la zona de juego
    private final Random random = new Random();

    private final List<FruitSlot> fruitSlots = new ArrayList<>();
    private final List<FruitSlot> selectionSlots = new ArrayList<>();
    private final Set<FruitSlot> hiddenSlots = new HashSet<>();
    private final Set<FruitSlot> discoveredSlots = new HashSet<>();

    private static final int SLOT_RADIUS = 20; // Radio duplicado
    private static final int NUM_SLOTS = 12;
    private static final int SLOTS_PER_ROW = 6;
    private static final int NUM_ROWS = 2;

    private static final int NUM_SELECTION_SLOTS = 7; // Nueva constante para la fila de selección

    private static final int SLOT_COLOR = 0xFF444444; // Color gris oscuro para todos los slots
    private static final int CORRECT_SLOT_COLOR = 0xFF00FF00; // Verde para acierto
    private static final int INCORRECT_SLOT_COLOR = 0xFFFF0000; // Rojo para fallo

    private static final int TARGET_CIRCLE_COLOR = 0xFFFFFFFF; // Color blanco para el círculo
    private static final int EXTRA_RADIUS = 3; // Píxeles extra para el círculo exterior

    private static final int ROUND_DURATION = 10; // Segundos que se muestran las frutas
    private static final int HIDE_DURATION = 3; // Segundos que tardan en ocultarse
    private static final int CHOOSE_DURATION = 10; // Segundos para elegir

    private final Timer roundTimer;
    private final Timer hideTimer;
    private final Timer chooseTimer;

    private int fruitsToHide; // Número de frutas que quedan por ocultar
    private long lastHideTime; // Para el ocultamiento gradual

    private enum GameState {
        SHOWING,
        HIDING,
        CHOOSING
    }

    private GameState currentGameState;
    private Fruit targetFruit;
    private FruitSlot targetSlot;

    public FruitFocusGame(GameScreen screen, int timeLimitSeconds, int prevScore) {
        super(screen, 5, timeLimitSeconds, prevScore); // Ajusta los valores según tu juego
        roundTimer = new Timer(ROUND_DURATION * 1000L); // Milisegundos
        hideTimer = new Timer(HIDE_DURATION * 1000L); // Milisegundos
        chooseTimer = new Timer(CHOOSE_DURATION * 1000L); // Milisegundos
    }


    @Override
    public void init() {
        super.init();
        List<Fruit> availableFruits = Arrays.asList(Fruit.values());

        // Añade espacio entre slots
        float slotSpacing = SLOT_RADIUS * 0.5f; // Espacio entre slots

        // Calcula el espacio total necesario para todos los slots incluyendo el espaciado
        float totalWidthNeeded = (SLOTS_PER_ROW * (SLOT_RADIUS * 2)) + ((SLOTS_PER_ROW - 1) * slotSpacing);
        float totalHeightNeeded = (NUM_ROWS * (SLOT_RADIUS * 2)) + ((NUM_ROWS - 1) * slotSpacing);

        // Calcula los márgenes para centrar
        float marginX = (GAME_WIDTH - totalWidthNeeded) / 2;
        float marginY = (GAME_HEIGHT - totalHeightNeeded) / 4; // Dividimos por 3 para dejar más espacio abajo

        // Posición inicial ajustada
        float startX = screen.getGameScreenXPos() + marginX + SLOT_RADIUS;
        float startY = screen.getGameScreenYPos() + marginY + SLOT_RADIUS;

        // Crear slots principales
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < SLOTS_PER_ROW; col++) {
                float x = startX + col * (SLOT_RADIUS * 2 + slotSpacing);
                float y = startY + row * (SLOT_RADIUS * 2 + slotSpacing);

                Fruit fruit = availableFruits.get(random.nextInt(availableFruits.size()));
                fruitSlots.add(new FruitSlot(fruit, x, y, false,
                        new CircleHitbox("slot", x, y, SLOT_RADIUS, 0, 0, SLOT_COLOR),
                        SLOT_COLOR, screen));
            }
        }

        // Calcula posiciones para los slots de selección sin espaciado
        float selectionTotalWidth = (float) NUM_SELECTION_SLOTS * (SLOT_RADIUS * 2);
        float selectionMarginX = (GAME_WIDTH - selectionTotalWidth) / 2;

        // Posiciona los slots de selección justo en el borde inferior
        float selectionY = screen.getGameScreenYPos() + GAME_HEIGHT - SLOT_RADIUS - 5f;

        // Crear slots de selección
        for (int i = 0; i < NUM_SELECTION_SLOTS; i++) {
            float x = screen.getGameScreenXPos() + selectionMarginX + SLOT_RADIUS + i * (SLOT_RADIUS * 2);
            Fruit fruit = availableFruits.get(i);
            selectionSlots.add(new FruitSlot(fruit, x, selectionY, true,
                    new CircleHitbox("selection", x, selectionY, SLOT_RADIUS, 0, 0, SLOT_COLOR),
                    SLOT_COLOR, screen));
        }
        discoveredSlots.clear();
        hiddenSlots.clear();
        fruitsToHide = NUM_SLOTS / 2;
        currentGameState = GameState.SHOWING;
    }

    @Override
    public void update() {
        super.update();
        if (!roundTimer.isRunning() && initDelay.isFinished()) {
            roundTimer.reset();
            roundTimer.start();
        }
        if (status == GameStatus.ACTIVE) {
            if (!hiddenSlots.isEmpty() && selectionSlots.getFirst().isHidden()) {
                selectionSlots.forEach(slot -> slot.setHidden(false));
            }

            switch (currentGameState) {
                case SHOWING:
                    if (roundTimer.isFinished()) {
                        currentGameState = GameState.HIDING;
                        lastHideTime = System.currentTimeMillis();
                        // Solo calculamos fruitsToHide en la primera ronda
                        if (hiddenSlots.isEmpty()) {
                            fruitsToHide = NUM_SLOTS / 2;
                        }
                    }
                    break;
                case HIDING:
                    hideFruits();
                    break;
                case CHOOSING:
                    if (chooseTimer.isFinished()) {
                        if (discoveredSlots.contains(targetSlot)) {
                            // Si acertó, restaurar el color del slot descubierto
                            targetSlot.setColor(SLOT_COLOR);
                            // Solo seleccionar nueva fruta sin reiniciar la ronda
                            selectNewTargetSlot();
                            startChoosing();
                        } else {
                            // Si falló, mantener la misma fruta objetivo
                            chooseTimer.reset();
                            chooseTimer.start();
                            targetSlot.setColor(SLOT_COLOR);
                        }
                    }
                    break;
            }
        }
    }


    private void selectNewTargetSlot() {
        List<FruitSlot> availableSlots = new ArrayList<>(hiddenSlots);
        availableSlots.removeAll(discoveredSlots);

        if (!availableSlots.isEmpty()) {
            targetSlot = availableSlots.get(random.nextInt(availableSlots.size()));
            targetFruit = targetSlot.getFruit();
        } else {
            // Juego completado
            status = GameStatus.ENDING;
        }
    }

    private void hideFruits() {
        if (currentGameState != GameState.HIDING) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastHideTime >= (HIDE_DURATION * 1000L) / NUM_SLOTS) {
            List<FruitSlot> slotsToHide = fruitSlots.stream()
                    .filter(slot -> !slot.isHidden() &&
                            !discoveredSlots.contains(slot) &&
                            !hiddenSlots.contains(slot))
                    .toList();

            if (!slotsToHide.isEmpty()) {
                FruitSlot slotToHide = slotsToHide.get(random.nextInt(slotsToHide.size()));
                slotToHide.setHidden(true);
                hiddenSlots.add(slotToHide);
                fruitsToHide--;
                lastHideTime = currentTime;
            }

            if (fruitsToHide <= 0) {
                // Seleccionar el primer targetSlot antes de comenzar
                selectNewTargetSlot();
                startChoosing();
            }
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
    }

    @Override
    public void handleMouseInput(double mouseX, double mouseY, int button) {
        if (currentGameState != GameState.CHOOSING) return;

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            for (FruitSlot slot : selectionSlots) {
                if (isMouseOverCircle(mouseX, mouseY, slot.getHitbox())) {
                    checkGuess(slot.getFruit());
                    return;
                }
            }
        }
    }

    private void checkGuess(Fruit guessedFruit) {
        boolean isCorrect = guessedFruit == targetFruit;

        if (isCorrect) {
            targetSlot.setColor(CORRECT_SLOT_COLOR);
            targetSlot.setHidden(false);
            discoveredSlots.add(targetSlot);

            if (discoveredSlots.size() == NUM_SLOTS) {
                status = GameStatus.ENDING;
            }
        } else {
            targetSlot.setColor(INCORRECT_SLOT_COLOR);
        }
    }

    private boolean isMouseOverCircle(double mouseX, double mouseY, CircleHitbox hitbox) {
        float dx = (float) (mouseX - hitbox.getXPos());
        float dy = (float) (mouseY - hitbox.getYPos());
        return (dx * dx + dy * dy) <= (hitbox.getRadius() * hitbox.getRadius());
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        CustomDrawContext customDrawContext = CustomDrawContext.wrap(context);
        customDrawContext.drawBasicBackground(screen);
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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (currentGameState == GameState.CHOOSING && targetSlot != null) {
            // Obtener la posición del slot objetivo
            CircleHitbox targetHitbox = targetSlot.getHitbox();
            int centerX = (int) targetHitbox.getXPos();
            int centerY = (int) targetHitbox.getYPos();

            // Dibujar el círculo exterior
            CustomDrawContext customDrawContext = CustomDrawContext.wrap(context);
            customDrawContext.drawCircle(centerX, centerY, SLOT_RADIUS + EXTRA_RADIUS, TARGET_CIRCLE_COLOR);
        }

        // Renderiza los espacios de frutas
        for (FruitSlot slot : fruitSlots) {
            slot.render(context);
        }
        for (FruitSlot slot : selectionSlots) {
            slot.render(context);
        }

        //Debug
        if (targetFruit != null) {
            context.drawText(getTextRenderer(), targetFruit.getName(), 0, 0, 0xFFFFFFFF, true);
        }
        switch (currentGameState) {
            case GameState.HIDING -> {
                context.drawText(getTextRenderer(), Text.literal("HIDING"), 0, 20, 0xFFFFFFFF, true);
                hideTimer.renderTimer(context, 0, 40);
            }
            case GameState.CHOOSING -> {
                context.drawText(getTextRenderer(), Text.literal("CHOOSING"), 0, 20, 0xFFFFFFFF, true);
                chooseTimer.renderTimer(context, 0, 40);
            }
            default -> {
                context.drawText(getTextRenderer(), Text.literal("OTHER"), 0, 20, 0xFFFFFFFF, true);
                roundTimer.renderTimer(context, 0, 40);
            }
        }
    }
}