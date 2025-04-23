package com.github.razorplay01.geowaremod.games.hanoitowers;

import com.github.razorplay01.geowaremod.GameSounds;
import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.render.CustomDrawContext;
import com.github.razorplay01.razorplayapi.util.stage.Game;
import com.github.razorplay01.razorplayapi.util.texture.Animation;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class HanoiTowersGame extends Game {
    private final Identifier globoTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/hanoitowers/globo.png");
    private final Animation globo;

    private final List<Tower> towers = new ArrayList<>();
    private Ring selectedRing;
    private Tower selectedTower;
    private final int numRings;
    private int movesCounter;

    private static final int GAME_WIDTH = 420;
    private static final int GAME_HEIGHT = 255;

    private final float soundVolume = 0.3f;

    private boolean playDeadSound = false;

    public HanoiTowersGame(Screen screen, int initDelay, int timeLimitSeconds, int prevScore, int rings) {
        super(screen, initDelay, timeLimitSeconds, prevScore);
        this.numRings = rings;
        this.movesCounter = 0;


        List<Texture> textures = new ArrayList<>();
        for (int i = 1; i <= 35; i++) {
            textures.add(new Texture(globoTexture, 0, (i * 15), 102, 15, 102, 525, 1));
        }
        this.globo = new Animation(
                textures,
                3f,
                true
        );
    }

    @Override
    public void init() {
        super.init();
        int towerWidth = 93;
        int towerHeight = 147;

        towers.add(new Tower(screen.getGameScreenXPos() + 32, screen.getGameScreenYPos() + 80, towerWidth, towerHeight, 0xFF844000));
        towers.add(new Tower(screen.getGameScreenXPos() + 165, screen.getGameScreenYPos() + 80, towerWidth, towerHeight, 0xFF844000));
        towers.add(new Tower(screen.getGameScreenXPos() + 295, screen.getGameScreenYPos() + 80, towerWidth, towerHeight, 0xFF00FF00));

        // Inicializar los anillos en la primera torre
        inicializarAnillos(towers.getFirst());
    }

    @Override
    public void update() {
        super.update();
        if (status == GameStatus.ACTIVE && verificarVictoria()) {
            addScore(50);
            playSound(GameSounds.HANOITOWERS_WIN, soundVolume, 1.0f); // Perdió
            status = GameStatus.ENDING;
        }
        if (status == GameStatus.ENDING && !verificarVictoria() && !playDeadSound) {
            playSound(GameSounds.HANOITOWERS_DEAD, soundVolume, 1.0f); // Perdió
            playDeadSound = true;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        CustomDrawContext customDrawContext = CustomDrawContext.wrap(context);
        customDrawContext.drawTextWithShadow(getTextRenderer(), "Movimientos: " + movesCounter, screen.getGameScreenXPos() + getScreenWidth() + 20, screen.getGameScreenYPos() + 40, 0xFFFFFFFF);
        towers.forEach(tower -> tower.render(context));
        globo.update(delta);
        globo.renderAnimation(context, screen.getGameScreenXPos() + 270, screen.getGameScreenYPos() + 15, 105, 15);

        if (selectedRing != null) {
            selectedRing.setXPos(mouseX - (selectedRing.getWidth() / 2));
            selectedRing.setYPos(mouseY - (selectedRing.getHeight() / 2));
            selectedRing.render(context);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier backgroundTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/hanoitowers/fondo.png");
        context.drawTexture(backgroundTexture, screen.getGameScreenXPos(), screen.getGameScreenYPos(), 0, 0, GAME_WIDTH, GAME_HEIGHT, GAME_WIDTH, GAME_HEIGHT);
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
    public void handleMouseInput(double mouseX, double mouseY, int button) {
        if (status != GameStatus.ACTIVE) return;
        Tower clickedTower = getClickedTower(mouseX, mouseY);
        if (clickedTower != null) {
            if (selectedRing == null) {
                selectRing(clickedTower);
            } else {
                moveRing(clickedTower);
            }
        }
    }

    private Tower getClickedTower(double mouseX, double mouseY) {
        for (Tower torre : towers) {
            if (torre.getHitbox().isMouseOver(mouseX, mouseY)) {
                return torre;
            }
        }
        return null;
    }

    private void selectRing(Tower torre) {
        if (!torre.getRings().isEmpty()) {
            playSound(GameSounds.HANOITOWERS_SELECT, soundVolume, 1.0f); // Perdió
            selectedRing = torre.getRings().get(torre.getRings().size() - 1);
            torre.removeRing(selectedRing);
            selectedTower = torre;
        }
    }

    private void moveRing(Tower torreDestino) {
        if (torreDestino.isValidMove(selectedRing)) {
            selectedTower.moveRingTo(torreDestino, selectedRing);
        } else {
            selectedTower.addRing(selectedRing);
            selectedRing.setXPos(selectedTower.getXPos() - (selectedRing.getWidth() / 2) + (selectedTower.getWidth() / 2));
            selectedRing.setYPos(selectedTower.getYPosForRelocateRing());
        }
        playSound(GameSounds.HANOITOWERS_THROW, soundVolume, 1.0f); // Perdió
        selectedRing = null;
        selectedTower = null;
        movesCounter++;
    }

    private boolean verificarVictoria() {
        return towers.get(2).getRings().size() == numRings;
    }

    private void inicializarAnillos(Tower torre) {
        Identifier texture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/hanoitowers/rings.png");
        int textureWidth = 150;
        int textureHeight = 150;
        int scale = 1;
        int height = 10;
        int yOffSet = -6;

        Ring ring1 = new Ring(
                torre.getXPos() - ((float) 87 / 2) + (torre.getWidth() / 2),
                torre.getYPos() + torre.getHeight() - 1 * height + yOffSet,
                87,
                10,
                new Texture(texture, 35, 106, 87, height, textureWidth, textureHeight, scale));
        Ring ring2 = new Ring(
                torre.getXPos() - ((float) 75 / 2) + (torre.getWidth() / 2),
                torre.getYPos() + torre.getHeight() - 2 * height + yOffSet,
                75,
                10,
                new Texture(texture, 41, 96, 75, height, textureWidth, textureHeight, scale));
        Ring ring3 = new Ring(
                torre.getXPos() - ((float) 63 / 2) + (torre.getWidth() / 2),
                torre.getYPos() + torre.getHeight() - 3 * height + yOffSet,
                63,
                10,
                new Texture(texture, 47, 86, 63, height, textureWidth, textureHeight, scale));
        Ring ring4 = new Ring(
                torre.getXPos() - ((float) 51 / 2) + (torre.getWidth() / 2),
                torre.getYPos() + torre.getHeight() - 4 * height + yOffSet,
                51,
                10,
                new Texture(texture, 53, 76, 51, height, textureWidth, textureHeight, scale));
        Ring ring5 = new Ring(
                torre.getXPos() - ((float) 39 / 2) + (torre.getWidth() / 2),
                torre.getYPos() + torre.getHeight() - 5 * height + yOffSet,
                39,
                10,
                new Texture(texture, 59, 66, 39, height, textureWidth, textureHeight, scale));
        Ring ring6 = new Ring(
                torre.getXPos() - ((float) 29 / 2) + (torre.getWidth() / 2),
                torre.getYPos() + torre.getHeight() - 6 * height + yOffSet,
                29,
                10,
                new Texture(texture, 64, 56, 29, height, textureWidth, textureHeight, scale));
        Ring ring7 = new Ring(
                torre.getXPos() - ((float) 21 / 2) + (torre.getWidth() / 2),
                torre.getYPos() + torre.getHeight() - 7 * height + yOffSet,
                21,
                10,
                new Texture(texture, 68, 46, 21, height, textureWidth, textureHeight, scale));
        Ring ring8 = new Ring(
                torre.getXPos() - ((float) 14 / 2) + (torre.getWidth() / 2),
                torre.getYPos() + torre.getHeight() - 8 * height + yOffSet,
                14,
                10,
                new Texture(texture, 71, 36, 39, height, textureWidth, textureHeight, scale));

        switch (numRings) {
            case 2 -> {
                torre.addRing(ring1);
                torre.addRing(ring2);
            }
            case 3 -> {
                torre.addRing(ring1);
                torre.addRing(ring2);
                torre.addRing(ring3);
            }
            case 4 -> {
                torre.addRing(ring1);
                torre.addRing(ring2);
                torre.addRing(ring3);
                torre.addRing(ring4);
            }
            case 5 -> {
                torre.addRing(ring1);
                torre.addRing(ring2);
                torre.addRing(ring3);
                torre.addRing(ring4);
                torre.addRing(ring5);
            }
            case 6 -> {
                torre.addRing(ring1);
                torre.addRing(ring2);
                torre.addRing(ring3);
                torre.addRing(ring4);
                torre.addRing(ring5);
                torre.addRing(ring6);
            }
            case 7 -> {
                torre.addRing(ring1);
                torre.addRing(ring2);
                torre.addRing(ring3);
                torre.addRing(ring4);
                torre.addRing(ring5);
                torre.addRing(ring6);
                torre.addRing(ring7);
            }
            case 8 -> {
                torre.addRing(ring1);
                torre.addRing(ring2);
                torre.addRing(ring3);
                torre.addRing(ring4);
                torre.addRing(ring5);
                torre.addRing(ring6);
                torre.addRing(ring7);
                torre.addRing(ring8);
            }
            default -> torre.addRing(ring1);
        }
    }
}
