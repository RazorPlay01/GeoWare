package com.github.razorplay01.geowaremod.games.bubblepuzzle;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.FloatingText;
import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.render.CustomDrawContext;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.stage.Game;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class BubblePuzzleGame extends Game {
    private Player player; // Jugador
    private final List<Bubble> bubbles; // Lista de bolas en el juego
    private final List<Bubble> bubblesToRemove;
    private static final int GAME_WIDTH = 20 * 10; // Ancho de la zona de juego
    private static final int GAME_HEIGHT = 20 * 15; // Alto de la zona de juego
    private float deathLineY;
    private final int level;
    private Character character;

    public BubblePuzzleGame(GameScreen screen, int prevScore, int initDelay, int timeLimitSeconds, int level) {
        super(screen, initDelay, timeLimitSeconds, prevScore);
        this.bubbles = new CopyOnWriteArrayList<>();
        this.bubblesToRemove = new ArrayList<>();
        this.level = level;
    }

    @Override
    public void init() {
        super.init();
        this.player = new Player(screen.getGameScreenXPos() + GAME_WIDTH / 2f - 10, screen.getGameScreenYPos() + GAME_HEIGHT - 32f, 20, 20, screen);
        this.deathLineY = screen.getGameScreenYPos() + GAME_HEIGHT - 70f; // Ajusta la posición
        this.character = new Character(screen.getGameScreenXPos() + 40, screen.getGameScreenYPos() + GAME_HEIGHT - 75, 75, 75);
        character.startGameBegin();
        switch (level) {
            case 2 -> loadLevel(LEVEL_2);
            case 3 -> loadLevel(LEVEL_3);
            default -> loadLevel(LEVEL_1);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        character.update();

        CustomDrawContext customDrawContext = CustomDrawContext.wrap(context);
        customDrawContext.drawLine(screen.getGameScreenXPos() + 2, (int) deathLineY, screen.getGameScreenXPos() + GAME_WIDTH - 4, (int) deathLineY, 0xAAFFFFFF);

        // Dibujar las burbujas estáticas
        for (Bubble bubble : bubbles) {
            bubble.draw(context);
        }

        player.drawCannon(context);
        // Renderizar la base del cañón (por encima del cañón, por debajo de las burbujas)
        Texture cannon_base = new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/cannon_base.png"), 29, 54, 44, 30, 100, 100, 0.8f);
        int width2 = (int) (cannon_base.width() * cannon_base.scale());
        int height2 = (int) (cannon_base.height() * cannon_base.scale());

        context.drawTexture(
                cannon_base.identifier(),
                (screen.getGameScreenXPos() + getScreenWidth() / 2) - width2 / 2,
                screen.getGameScreenYPos() + getScreenHeight() - height2 - 11,
                width2,
                height2,
                cannon_base.u(),
                cannon_base.v(),
                cannon_base.width(),
                cannon_base.height(),
                cannon_base.textureWidth(),
                cannon_base.textureHeight()
        );

        if (!character.isReloading()) {
            player.draw(context);
        }

        // Dibujar textos flotantes
        for (FloatingText text : floatingTexts) {
            text.render(context);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier backgroundTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/fondo.png");

        context.drawTexture(backgroundTexture, screen.getGameScreenXPos() - 10, screen.getGameScreenYPos() - 10,
                getScreenWidth() + 20, getScreenHeight() + 10, 0, 0, 255, 415, 255, 415);

        if (character != null) {
            character.render(context);
        }

        Texture pipe = new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/pipe.png"), 0, 24, 35, 47, 100, 100, 0.8f);
        int width = (int) (pipe.width() * pipe.scale());
        int height = (int) (pipe.height() * pipe.scale());

        context.drawTexture(
                pipe.identifier(),
                screen.getGameScreenXPos() + 2,
                screen.getGameScreenYPos() + getScreenHeight() - height - 11,
                width,
                height,
                pipe.u(),
                pipe.v(),
                pipe.width(),
                pipe.height(),
                pipe.textureWidth(),
                pipe.textureHeight()
        );
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
        if (status == GameStatus.ACTIVE) {
            if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_A) {
                player.rotateLeft();
            } else if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_D) {
                player.rotateRight();
            } else if (keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_UP) {
                Bubble shotBubble = player.shoot();
                if (shotBubble != null) {
                    bubbles.add(shotBubble);
                    // Iniciar animación de recarga con el color de la nueva currentBubble
                    BubbleColor color = Arrays.stream(BubbleColor.values())
                            .filter(c -> c.getTexture().equals(player.getCurrentBubble().getColor()))
                            .findFirst()
                            .orElse(BubbleColor.GREEN); // Default por si falla
                    character.startReload(color);
                }
            }
        }
    }

    @Override
    public void update() {
        super.update();
        for (Bubble bubble : bubbles) {
            if (bubble.isMoving()) {
                bubble.update();
                if (bubble.isFalling()) {
                    if (bubble.getY() + bubble.getRadius() >= screen.getGameScreenYPos() + GAME_HEIGHT) {
                        addScore(2, bubble.getX(), bubble.getY());
                        bubblesToRemove.add(bubble);
                    }
                } else {
                    try {
                        checkCollisions(bubble);
                    } catch (Exception e) {
                        bubblesToRemove.add(bubble);
                    }
                }
            }
        }

        bubbles.removeAll(bubblesToRemove);
        bubblesToRemove.clear();

        for (Bubble bubble : bubbles) {
            if (bubble.isFalling()) {
                bubble.update();
            }
        }

        // Verificar si hay burbujas en movimiento
        boolean anyMoving = bubbles.stream().anyMatch(Bubble::isMoving);

        // Solo verificar burbujas desconectadas cuando no hay movimiento
        if (!anyMoving) {
            removeDisconnectedBubbles();
        }
        checkGameOver();
    }

    public void checkCollisions(Bubble bubble) {
        float gameLeftBorder = screen.getGameScreenXPos();
        float gameRightBorder = (float) screen.getGameScreenXPos() + GAME_WIDTH;
        float gameTopBorder = screen.getGameScreenYPos();

        float bubbleX = bubble.getX();
        float bubbleY = bubble.getY();
        float bubbleRadius = bubble.getRadius();

        // Colisión con bordes (mantenido igual)
        if (bubbleX - bubbleRadius <= gameLeftBorder) {
            bubble.setX(gameLeftBorder + bubbleRadius);
            bubble.updatePosition(bubble.getX(), bubbleY);
            bubble.setVelocityX(Math.abs(bubble.getVelocityX()));
        }
        if (bubbleX + bubbleRadius >= gameRightBorder) {
            bubble.setX(gameRightBorder - bubbleRadius);
            bubble.updatePosition(bubble.getX(), bubbleY);
            bubble.setVelocityX(-Math.abs(bubble.getVelocityX()));
        }
        if (bubbleY - bubbleRadius <= gameTopBorder) {
            bubble.setY(gameTopBorder + bubbleRadius);
            bubble.updatePosition(bubbleX, bubble.getY());
            bubble.stop();
        }

        if (bubble.getY() + bubbleRadius >= deathLineY && bubble.isConnectedToTop()) {
            this.status = GameStatus.ENDING;
            removeAllBubbles();
        }

        for (Bubble otherBubble : bubbles) {
            if (bubble != otherBubble) {
                float dx = bubble.getX() - otherBubble.getX();
                float dy = bubble.getY() - otherBubble.getY();
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                float minDistance = bubble.getRadius() + otherBubble.getRadius() - 0.5f; // Reducir tolerancia

                if (distance < minDistance) {
                    float overlap = minDistance - distance;
                    float angle = (float) Math.atan2(dy, dx);

                    // Ajustar la posición de la burbuja entrante hacia afuera
                    float adjustX = (float) (overlap * Math.cos(angle) * 1.1); // Ajuste con un poco de margen
                    float adjustY = (float) (overlap * Math.sin(angle) * 1.1);

                    bubble.updatePosition(bubble.getX() + adjustX, bubble.getY() + adjustY);

                    // Agregar conexión mutua
                    bubble.addConnection(otherBubble);
                    otherBubble.addConnection(bubble);

                    // Propagar conexión al tope
                    if (otherBubble.isConnectedToTop()) {
                        bubble.propagateTopConnection();
                    }
                    bubble.stop();
                    break; // Salir tras la primera colisión
                }
            }
        }
        removeGroupIfValid(bubble);
    }

    private void removeAllBubbles() {
        bubbles.clear();
    }

    private List<Bubble> findConnectedBubbles(Bubble startBubble) {
        List<Bubble> connectedBubbles = new ArrayList<>();
        Queue<Bubble> queue = new LinkedList<>();
        Set<Bubble> visited = new HashSet<>();

        queue.add(startBubble);
        visited.add(startBubble);

        while (!queue.isEmpty()) {
            Bubble current = queue.poll();
            connectedBubbles.add(current);

            // Buscar bolas vecinas
            for (Bubble neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor) && neighbor.getColor() == current.getColor()) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }

        return connectedBubbles;
    }

    private List<Bubble> getNeighbors(Bubble bubble) {
        List<Bubble> neighbors = new ArrayList<>();
        float radius = bubble.getRadius();
        float tolerance = 2.0f; // Tolerancia de solapamiento

        for (Bubble otherBubble : bubbles) {
            if (otherBubble != bubble) {
                float dx = bubble.getX() - otherBubble.getX();
                float dy = bubble.getY() - otherBubble.getY();
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                // Si la distancia está dentro del rango (con tolerancia)
                if (distance <= radius * 2 + tolerance) {
                    neighbors.add(otherBubble);
                }
            }
        }
        return neighbors;
    }

    public void removeBubble(Bubble bubble) {
        bubbles.remove(bubble);
    }

    private void removeGroupIfValid(Bubble bubble) {
        List<Bubble> connected = findConnectedBubbles(bubble);
        if (connected.size() >= 3) {
            connected.forEach(b -> addScore(1, b.getX(), b.getY()));
            bubbles.removeAll(connected);

            for (Bubble b : connected) { // Verificar grupos alrededor de las burbujas eliminadas
                removeGroupIfValid(b);
            }

            // Actualizar las burbujas desconectadas después de eliminar un grupo
            removeDisconnectedBubbles();
        }
    }

    private void removeDisconnectedBubbles() {
        // Reiniciar el estado de conectividad para todas las burbujas
        for (Bubble bubble : bubbles) {
            bubble.setConnectedToTop(false); // Reiniciar conexión al borde superior
        }

        // **1. Marcar las burbujas conectadas al borde superior**
        for (Bubble bubble : bubbles) {
            if (isConnectedToTop(bubble)) {
                bubble.propagateTopConnection(); // Propagar conexión al borde superior
            }
        }

        // **2. Identificar y marcar para caída las burbujas que no están conectadas al borde superior**
        for (Bubble bubble : bubbles) {
            if (!bubble.isConnectedToTop() && !bubble.isFalling()) {
                bubble.startFalling(); // Marcar como en caída
            }
        }
    }

    private boolean isConnectedToTop(Bubble bubble) {
        float topBorder = screen.getGameScreenYPos();
        float tolerance = 2.0f; // Tolerancia para considerar que está tocando el borde superior
        return bubble.getY() - bubble.getRadius() <= topBorder + tolerance;
    }

    private void checkGameOver() {
        if (bubbles.isEmpty() && status != GameStatus.ENDING) {
            this.status = GameStatus.ENDING;
            character.startWin(); // Animación de victoria
        } else if (status == GameStatus.ENDING && !bubbles.isEmpty()) {
            character.startLose(); // Animación de derrota
        }
    }

    private void loadLevel(String[] level) {
        bubbles.clear(); // Limpiar burbujas existentes
        int rows = level.length;
        int cols = level[0].length();
        float bubbleRadius = 10; // Radio de las burbujas
        float startX = screen.getGameScreenXPos() + (GAME_WIDTH - cols * bubbleRadius * 2) / 2; // Ajuste centrado
        float startY = screen.getGameScreenYPos() + bubbleRadius;

        // 1. Crear matriz para almacenar las burbujas
        Bubble[][] bubbleGrid = new Bubble[rows][cols];

        // 2. Crear las burbujas y almacenarlas en la matriz
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                char bubbleType = level[row].charAt(col);
                if (bubbleType != '-') { // '-' representa un espacio vacío
                    Texture color = getColor(bubbleType); // Método para obtener el color
                    float x = startX + col * bubbleRadius * 2 + (row % 2 == 0 ? 0 : bubbleRadius); // Offset para filas pares
                    float y = startY + row * (bubbleRadius - 2) * 2;
                    Bubble bubble = new Bubble(x, y, bubbleRadius, color, screen);
                    bubbles.add(bubble);
                    bubbleGrid[row][col] = bubble; // Almacenar en la matriz
                }
            }
        }

        // 3. Establecer las conexiones entre las burbujas
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Bubble bubble = bubbleGrid[row][col];
                if (bubble != null) {
                    // Conexiones horizontales
                    if (col > 0) connectBubbles(bubble, bubbleGrid[row][col - 1]);
                    if (col < cols - 1) connectBubbles(bubble, bubbleGrid[row][col + 1]);

                    // Conexiones verticales
                    if (row > 0) {
                        int offset = row % 2 == 0 ? 0 : 1; // Offset para filas pares
                        connectBubbles(bubble, bubbleGrid[row - 1][col + offset]);
                        if (offset == 1 && col > 0) connectBubbles(bubble, bubbleGrid[row - 1][col]);
                    }
                    if (row < rows - 1) {
                        int offset = row % 2 == 0 ? 0 : 1; // Offset para filas pares
                        connectBubbles(bubble, bubbleGrid[row + 1][col + offset]);
                        if (offset == 1 && col > 0) connectBubbles(bubble, bubbleGrid[row + 1][col]);
                    }
                }
            }
        }
    }

    private void connectBubbles(Bubble bubble1, Bubble bubble2) {
        if (bubble1 != null && bubble2 != null) {
            bubble1.addConnection(bubble2);
            bubble2.addConnection(bubble1);
        }
    }

    private Texture getColor(char type) {
        return switch (type) {
            case 'B' -> BubbleColor.BLUE.getTexture();
            case 'W' -> BubbleColor.WHITE.getTexture();
            case 'P' -> BubbleColor.PURPLE.getTexture();
            case 'O' -> BubbleColor.ORANGE.getTexture();
            case 'V' -> BubbleColor.BLACK.getTexture();
            case 'R' -> BubbleColor.RED.getTexture();
            case 'K' -> BubbleColor.PINK.getTexture();
            case 'G' -> BubbleColor.GREEN.getTexture();
            default -> BubbleColor.YELLOW.getTexture();
        };
    }

    private static final String[] LEVEL_1 = {
            "-RRYYBBGG-",
            "-RRYYBBGG-",
            "-BBGGRRYY-",
            "-BBGGRRYY-",
            "----------",
            "----------",
            "----------",
            "----------",
            "----------",
            "----------",
    };
    private static final String[] LEVEL_2 = {
            "-RRYYBBGG-",
            "-RRYYBBGG-",
            "-BBGGRRYY-",
            "-BBGGRRYY-",
            "----------",
            "----------",
            "----------",
            "----------",
            "----------",
            "----------",
    };
    private static final String[] LEVEL_3 = {
            "-RRYYBBGG-",
            "-RRYYBBGG-",
            "-BBGGRRYY-",
            "-BBGGRRYY-",
            "----------",
            "----------",
            "----------",
            "----------",
            "----------",
            "----------",
    };
}