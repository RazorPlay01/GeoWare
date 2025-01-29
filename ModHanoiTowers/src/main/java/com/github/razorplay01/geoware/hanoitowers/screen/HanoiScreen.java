package com.github.razorplay01.geoware.hanoitowers.screen;

import com.github.razorplay01.geoware.hanoitowers.game.Ring;
import com.github.razorplay01.geoware.hanoitowers.game.Tower;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class HanoiScreen extends Screen {
    private final List<Tower> towers = new ArrayList<>();
    private Ring selectedRing;
    private Tower selectedTower;
    private final int numRings;
    private int movesCounter;

    private Integer screenXPos;
    private Integer screenYPos;
    int gameZoneWidth;
    int gameZoneHeight;

    public HanoiScreen(int numAnillos) {
        super(Text.empty());
        this.numRings = numAnillos;
        this.movesCounter = 0;
    }

    @Override
    protected void init() {
        if (screenXPos == null || screenYPos == null) {
            // Configuración estática
            int maxRingWidth = 160; // Ancho del anillo más grande
            int towerWidth = 20;    // Ancho de la torre
            int towerHeight = 200;  // Altura de la torre
            int margin = 20;        // Margen adicional alrededor de la zona de juego
            int separation = 20;    // Separación entre cada anillo

            // Calcular el ancho total de la zona de juego
            this.gameZoneWidth = margin + maxRingWidth + separation + maxRingWidth + separation + maxRingWidth + margin;

            // Calcular la altura total de la zona de juego
            this.gameZoneHeight = towerHeight + (margin * 2);

            // Posicionar la zona de juego centrada en la pantalla
            this.screenXPos = (this.width / 2) - (this.gameZoneWidth / 2);
            this.screenYPos = (this.height / 2) - (this.gameZoneHeight / 2);

            // Posicionar las torres centradas en sus respectivos espacios
            int firstTowerX = screenXPos + margin + (maxRingWidth / 2);
            int secondTowerX = firstTowerX + maxRingWidth + separation;
            int thirdTowerX = secondTowerX + maxRingWidth + separation;
            int towerY = screenYPos + margin;

            // Crear las torres en sus posiciones calculadas
            towers.add(new Tower(firstTowerX - ((float) towerWidth / 2), towerY, towerWidth, towerHeight, 0xFF844000));
            towers.add(new Tower(secondTowerX - ((float) towerWidth / 2), towerY, towerWidth, towerHeight, 0xFF844000));
            towers.add(new Tower(thirdTowerX - ((float) towerWidth / 2), towerY, towerWidth, towerHeight, 0xFF00FF00));

            // Inicializar los anillos en la primera torre
            inicializarAnillos(towers.getFirst());
        }
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

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Dibujar el fondo de la zona de juego
        context.fill(screenXPos, screenYPos, screenXPos + gameZoneWidth, screenYPos + gameZoneHeight, 0xFF000000);

        // Dibujar el borde de la zona de juego
        context.drawBorder(screenXPos - 1, screenYPos - 1, gameZoneWidth + 2, gameZoneHeight + 2, 0xFFFFFFFF);

        // Renderizar las torres
        for (Tower torre : towers) {
            torre.render(context);
        }

        // Renderizar el anillo seleccionado en la posición del mouse
        if (selectedRing != null) {
            selectedRing.setXPos(mouseX - (selectedRing.getWidth() / 2)); // Centrar el anillo en el mouse
            selectedRing.setYPos(mouseY - (selectedRing.getHeight() / 2));
            selectedRing.render(context);
        }

        // Mostrar el contador de movimientos
        context.drawTextWithShadow(textRenderer, "Movimientos: " + movesCounter, 10, 10, 0xFFFFFFFF);

        // Verificar victoria
        if (verificarVictoria()) {
            context.drawTextWithShadow(textRenderer, "¡Has ganado!", width / 2 - 30, height / 2 - 10, 0xFF00FF00);
        }
    }

    private boolean verificarVictoria() {
        return towers.get(2).getRings().size() == numRings;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Tower clickedTower = getClickedTower(mouseX, mouseY);
        if (clickedTower == null) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (selectedRing == null) {
            selectRing(clickedTower);
        } else {
            moveRing(clickedTower);
        }

        return true;
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
}
