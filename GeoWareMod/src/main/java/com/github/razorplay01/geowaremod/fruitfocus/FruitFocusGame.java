package com.github.razorplay01.geowaremod.fruitfocus;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.Timer;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.stage.Game;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.*;

@Getter
public class FruitFocusGame extends Game {
    private final float scaleFactor = 1.5f;
    public static final Identifier fruitsTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/fruitfocus/fruits.png");

    private static final int GAME_WIDTH = 208;
    private static final int GAME_HEIGHT = 148;
    private final Random random = new Random();

    private final List<FruitSlot> fruitSlots = new ArrayList<>();
    private final List<FruitSlot> selectionSlots = new ArrayList<>();
    private final Set<FruitSlot> hiddenSlots = new HashSet<>();
    private final Set<FruitSlot> discoveredSlots = new HashSet<>();

    private static final int NUM_SLOTS = 12;
    private static final int NUM_SELECTION_SLOTS = 7;
    private static final int SLOT_COLOR = 0xFF444444;

    private static final int ROUND_DURATION = 10;
    private static final int HIDE_DURATION = 3;
    private static final int CHOOSE_DURATION = 10;

    private final Timer roundTimer;
    private final Timer hideTimer;
    private final Timer chooseTimer;

    private int fruitsToHide;
    private long lastHideTime;
    private List<Fruit> availableFruits;

    private boolean hasMadeChoice;

    private enum GameState {
        SHOWING,
        HIDING,
        CHOOSING
    }

    private GameState currentGameState;
    private Fruit targetFruit;
    private FruitSlot targetSlot;

    public FruitFocusGame(GameScreen screen, int timeLimitSeconds, int prevScore) {
        super(screen, 5, timeLimitSeconds, prevScore);
        roundTimer = new Timer(ROUND_DURATION * 1000L);
        hideTimer = new Timer(HIDE_DURATION * 1000L);
        chooseTimer = new Timer(CHOOSE_DURATION * 1000L);
        this.hasMadeChoice = false;
    }

    @Override
    public void init() {
        super.init();
        availableFruits = Arrays.asList(Fruit.values());

        float[][] slotCoordinates = {
                {48, 26}, {79, 11}, {25, 48}, {51, 65}, {40, 91}, {75, 46},
                {87, 81}, {101, 31}, {113, 63}, {125, 20}, {138, 45}, {141, 87}
        };

        for (float[] slotCoordinate : slotCoordinates) {
            float x = screen.getGameScreenXPos() + slotCoordinate[0] * scaleFactor;
            float y = screen.getGameScreenYPos() + slotCoordinate[1] * scaleFactor;

            Fruit fruit = availableFruits.get(random.nextInt(availableFruits.size()));
            fruitSlots.add(new FruitSlot(fruit, x, y, false,
                    new RectangleHitbox("slot", x, y, 16 * scaleFactor, 16 * scaleFactor, 0, 0, SLOT_COLOR),
                    SLOT_COLOR, screen));
        }

        initSelectSlots();

        discoveredSlots.clear();
        hiddenSlots.clear();
        fruitsToHide = NUM_SLOTS / 2;
        currentGameState = GameState.SHOWING;
    }

    private void initSelectSlots() {
        int yPos = (int) (107 * scaleFactor);
        int xPos = (int) (24 * scaleFactor);
        for (int i = 0; i < NUM_SELECTION_SLOTS; i++) {
            float x = screen.getGameScreenXPos() + xPos + i * (24 * scaleFactor);
            float y = screen.getGameScreenYPos() + yPos;
            Fruit fruit = availableFruits.get(i);
            selectionSlots.add(new FruitSlot(fruit, x, y, true,
                    new RectangleHitbox("selection", x, y, 16 * scaleFactor, 16 * scaleFactor, 0, 0, SLOT_COLOR),
                    SLOT_COLOR, screen));
        }
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
                        if (discoveredSlots.contains(targetSlot)) { // Si acertó
                            if (discoveredSlots.size() == NUM_SLOTS) {
                                status = GameStatus.ENDING;
                            } else {
                                targetSlot.setColor(SLOT_COLOR);
                                selectNewTargetSlot();
                                startChoosing();
                            }
                        } else { // Si falló
                            hasMadeChoice = false;
                            chooseTimer.reset();
                            chooseTimer.start();
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
        hasMadeChoice = false;
    }

    private void checkGuess(Fruit guessedFruit) {
        boolean isCorrect = guessedFruit == targetFruit;

        if (isCorrect) {
            targetSlot.setHidden(false);
            discoveredSlots.add(targetSlot);
        } else {
            client.player.playSound(SoundEvent.of(Identifier.of("minecraft:entity.villager.hurt")), 0.5F, 1.0F);
        }
        hasMadeChoice = true;
    }

    @Override
    public void handleMouseInput(double mouseX, double mouseY, int button) {
        if (currentGameState != GameState.CHOOSING || hasMadeChoice) {
            client.player.playSound(SoundEvent.of(Identifier.of("minecraft:entity.villager.no")), 0.5F, 1.0F);
            return;
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
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