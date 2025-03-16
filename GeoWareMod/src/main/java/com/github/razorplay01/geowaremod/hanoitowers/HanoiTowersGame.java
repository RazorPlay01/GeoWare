package com.github.razorplay01.geowaremod.hanoitowers;

import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.render.CustomDrawContext;
import com.github.razorplay01.razorplayapi.util.stage.Game;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.List;

public class HanoiTowersGame extends Game {
    private final List<Tower> towers = new ArrayList<>();
    private Ring selectedRing;
    private Tower selectedTower;
    private final int numRings;
    private int movesCounter;

    private static final int GAME_WIDTH = 560; //margin + maxRingWidth + separation + maxRingWidth + separation + maxRingWidth + margin;
    private static final int GAME_HEIGHT = 240; //towerHeight + (margin * 2);

    public HanoiTowersGame(Screen screen, int initDelay, int timeLimitSeconds, int prevScore, int rings) {
        super(screen, initDelay, timeLimitSeconds, prevScore);
        this.numRings = rings;
        this.movesCounter = 0;
    }

    @Override
    public void init() {
        super.init();
        int maxRingWidth = 160;
        int towerWidth = 20;
        int towerHeight = 200;
        int margin = 20;
        int separation = 20;

        // Posicionar las torres centradas en sus respectivos espacios
        int firstTowerX = screen.getGameScreenXPos() + margin + (maxRingWidth / 2);
        int secondTowerX = firstTowerX + maxRingWidth + separation;
        int thirdTowerX = secondTowerX + maxRingWidth + separation;
        int towerY = screen.getGameScreenYPos() + margin;

        // Crear las torres en sus posiciones calculadas
        towers.add(new Tower(firstTowerX - ((float) towerWidth / 2), towerY, towerWidth, towerHeight, 0xFF844000));
        towers.add(new Tower(secondTowerX - ((float) towerWidth / 2), towerY, towerWidth, towerHeight, 0xFF844000));
        towers.add(new Tower(thirdTowerX - ((float) towerWidth / 2), towerY, towerWidth, towerHeight, 0xFF00FF00));

        // Inicializar los anillos en la primera torre
        inicializarAnillos(towers.getFirst());
    }

    @Override
    public void update() {
        super.update();
        if (status == GameStatus.ACTIVE && verificarVictoria()) {
            addScore(1000);
            status = GameStatus.ENDING;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTextWithShadow(getTextRenderer(), "Movimientos: " + movesCounter, 10, 10, 0xFFFFFFFF);
        towers.forEach(tower -> tower.render(context));

        if (selectedRing != null) {
            selectedRing.setXPos(mouseX - (selectedRing.getWidth() / 2));
            selectedRing.setYPos(mouseY - (selectedRing.getHeight() / 2));
            selectedRing.render(context);
        }
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
        selectedRing = null;
        selectedTower = null;
        movesCounter++;
    }

    private boolean verificarVictoria() {
        return towers.get(2).getRings().size() == numRings;
    }

    private void inicializarAnillos(Tower torre) {
        for (int i = 0; i < numRings; i++) {
            int anilloWidth = 160 - (i * 20); // El ancho disminuye con cada anillo
            int anilloHeight = 20;
            int color = 0xFF0000FF + (i * 0x00001100); // Cambia el color para cada anillo

            Ring anillo = new Ring(
                    torre.getXPos() - ((float) anilloWidth / 2) + (torre.getWidth() / 2), // Centrado en la torre
                    torre.getYPos() + torre.getHeight() - (i + 1) * anilloHeight, // yPos: base de la torre
                    anilloWidth,
                    anilloHeight,
                    color
            );

            torre.addRing(anillo);
        }
    }
}
