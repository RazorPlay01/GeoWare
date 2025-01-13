package com.github.razorplay01.donkeykongfabric.game;

import com.github.razorplay01.donkeykongfabric.screen.GameScreen;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.github.razorplay01.donkeykongfabric.screen.GameScreen.SCREEN_HEIGHT;

public class Player {
    boolean isDebugMode = false;
    private final List<DebugPoint> debugPoints = new ArrayList<>();

    // Constantes de dimensiones
    public static final float PLAYER_WIDTH = 12;
    public static final float PLAYER_HEIGHT = 16;

    // Constantes de física
    private static final float GRAVITY = 0.2f;
    private static final float JUMP_FORCE = -2.6f;
    private static final float MOVE_SPEED = 1.5f;
    private static final float MAX_FALL_SPEED = 4f;
    private static final float CLIMB_SPEED = 1f;
    private static final float LADDER_TOLERANCE = 2.0f;

    // Referencias
    private final GameScreen gameScreen;

    // Posición y movimiento
    @Getter
    private float xPos;
    @Getter
    private float yPos;
    private float velocityX = 0;
    private float velocityY = 0;

    // Estados de movimiento
    private boolean isJumping;
    private boolean movingLeft;
    private boolean movingRight;
    private boolean movingUp;
    private boolean movingDown;

    // Estados de plataforma y escalera
    private Platform currentPlatform;
    @Getter
    private boolean isOnLadder;
    private boolean isClimbing;
    private Ladder currentLadder;

    private boolean hasJumpedOverBarrel = false; // Para evitar puntos múltiples por el mismo barril
    @Getter
    private int score = 0; // Puntuación del jugador

    public Player(float x, float y, GameScreen gameScreen) {
        this.xPos = x;
        this.yPos = y;
        this.gameScreen = gameScreen;
        initializeStates();
    }

    private void initializeStates() {
        this.isJumping = false;
        this.isOnLadder = false;
        this.isClimbing = false;
        this.movingLeft = false;
        this.movingRight = false;
        this.movingUp = false;
        this.movingDown = false;
    }

    // Método principal de actualización
    public void update(int screenXpos, int screenYpos, int screenWidth, int screenHeight, List<Platform> platforms) {
        if (isOnLadder) {
            updateLadderMovement();
        } else {
            updateNormalMovement();
            updatePosition();
            checkPlatformCollision(platforms);
        }

        handleScreenBounds(screenXpos, screenYpos, screenWidth, screenHeight);
        checkLadderContact();
    }

    // Métodos de actualización de movimiento
    private void updateLadderMovement() {
        if (currentLadder != null) {
            // Guardar la posición anterior
            float previousY = yPos;

            centerOnLadder();
            handleVerticalLadderMovement();

            // Actualizar la plataforma actual basada en la posición en la escalera
            if (previousY != yPos) { // Si nos hemos movido verticalmente
                updateCurrentPlatformOnLadder(gameScreen.getPlatforms());
            }

            checkLadderLimits();
        }
    }

    private void updateCurrentPlatformOnLadder(List<Platform> platforms) {
        float playerCenterX = xPos + (PLAYER_WIDTH / 2);
        float playerBottom = yPos + PLAYER_HEIGHT;

        // Buscar la plataforma más cercana a la posición actual
        Platform nearestPlatform = null;
        float nearestDistance = Float.MAX_VALUE;

        for (Platform platform : platforms) {
            if (playerCenterX >= platform.getX() &&
                    playerCenterX <= platform.getX() + Platform.WIDTH) {

                float distance = Math.abs(platform.getY() - playerBottom);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestPlatform = platform;
                }
            }
        }

        // Actualizar la plataforma actual
        currentPlatform = nearestPlatform;
    }

    private void updateNormalMovement() {
        // Movimiento horizontal
        if (movingLeft && !movingRight) {
            velocityX = -MOVE_SPEED;
        } else if (movingRight && !movingLeft) {
            velocityX = MOVE_SPEED;
        } else {
            velocityX = 0;
        }

        // Gravedad
        if (!isOnLadder) {
            velocityY = Math.min(velocityY + GRAVITY, MAX_FALL_SPEED);
        }
    }

    // Métodos de movimiento en escalera
    private void centerOnLadder() {
        xPos = currentLadder.getX() + (currentLadder.getWidth() - PLAYER_WIDTH) / 2;
    }

    private void handleVerticalLadderMovement() {
        if (movingUp) {
            yPos -= CLIMB_SPEED;
            isClimbing = true;
        } else if (movingDown) {
            // Verificar si podemos seguir bajando
            float ladderBottom = currentLadder.getY() + currentLadder.getHeight();

            // Si estamos cerca del final de la escalera, verificar si hay plataforma
            if (yPos + PLAYER_HEIGHT >= ladderBottom - 2) {
                // Verificar si hay una plataforma al final de la escalera
                boolean platformAtBottom = false;
                for (Platform platform : gameScreen.getPlatforms()) {
                    if (Math.abs(platform.getY() - ladderBottom) < 2 &&
                            xPos + PLAYER_WIDTH > platform.getX() &&
                            xPos < platform.getX() + Platform.WIDTH) {
                        platformAtBottom = true;
                        break;
                    }
                }

                if (platformAtBottom) {
                    // Si hay plataforma, no permitir bajar más
                    yPos = ladderBottom - PLAYER_HEIGHT - 1;
                } else {
                    // Si no hay plataforma, permitir el movimiento normal
                    yPos += CLIMB_SPEED;
                }
            } else {
                // Si no estamos cerca del final, movimiento normal
                yPos += CLIMB_SPEED;
            }
            isClimbing = true;
        } else {
            isClimbing = false;
            velocityY = 0;
        }
    }

    // Métodos de control de movimiento
    public void moveLeft() {
        movingLeft = true;
        stopClimbing();
    }

    public void moveRight() {
        movingRight = true;
        stopClimbing();
    }

    public void moveUp(List<Ladder> ladders) {
        movingUp = true;
        movingDown = false;

        if (!tryClimbLadder(ladders) && !isOnLadder) {
            jump(SCREEN_HEIGHT);
        }
    }

    public void moveDown(List<Ladder> ladders) {
        movingDown = true;
        movingUp = false;
        tryClimbLadder(ladders);
    }

    // Métodos de detención de movimiento
    public void stopMovingLeft() {
        movingLeft = false;
    }

    public void stopMovingRight() {
        movingRight = false;
    }

    public void stopMovingUp() {
        movingUp = false;
    }

    public void stopMovingDown() {
        movingDown = false;
    }

    // Métodos de manejo de escaleras
    private boolean tryClimbLadder(List<Ladder> ladders) {
        for (Ladder ladder : ladders) {
            float playerCenterX = xPos + (PLAYER_WIDTH / 2);
            float playerBottom = yPos + PLAYER_HEIGHT;

            boolean isInLadderRange =
                    playerCenterX >= ladder.getX() - LADDER_TOLERANCE &&
                            playerCenterX <= ladder.getX() + ladder.getWidth() + LADDER_TOLERANCE &&
                            playerBottom >= ladder.getY() &&
                            playerBottom <= ladder.getY() + ladder.getHeight();

            if (isInLadderRange) {
                // Permitir subir cualquier escalera
                if (movingUp) {
                    currentLadder = ladder;
                    startClimbing();
                    return true;
                }
                // Para bajar, solo permitir escaleras atravesables
                else if (movingDown && ladder.isCanPassThroughPlatform()) {
                    currentLadder = ladder;
                    startClimbing();
                    return true;
                }
            }
        }
        return false;
    }

    private void startClimbing() {
        if (currentLadder != null) {
            isOnLadder = true;
            isClimbing = true;
            velocityX = 0;
            velocityY = 0;
        }
    }

    public void stopClimbing() {
        if (isOnLadder) {
            // Mantener la posición actual y dejar que la gravedad actúe
            velocityY = 0; // Iniciar con velocidad vertical 0
            currentPlatform = null; // Importante: resetear la plataforma actual
        }

        // Simplemente liberar el estado de la escalera
        isOnLadder = false;
        isClimbing = false;
        currentLadder = null;
        movingUp = false;
        movingDown = false;
    }

    // Métodos de renderizado
    public void render(DrawContext context) {
        context.fill(
                (int) xPos,
                (int) yPos,
                (int) (xPos + PLAYER_WIDTH),
                (int) (yPos + PLAYER_HEIGHT),
                0xFF0000FF
        );
        if (isDebugMode) {
            for (DebugPoint point : debugPoints) {
                context.fill(
                        (int) point.x - 1,
                        (int) point.y - 1,
                        (int) point.x + 1,
                        (int) point.y + 1,
                        point.color
                );
            }
        }
    }

    private void checkPlatformCollision(List<Platform> platforms) {
        // Si estamos en una escalera atravesable
        if (isOnLadder && currentLadder != null && currentLadder.isCanPassThroughPlatform()) {
            // Solo permitir atravesar la plataforma que está al inicio de la escalera
            float ladderTopY = currentLadder.getY();

            CollisionPoints points = calculateCollisionPoints();
            Platform mainPlatform = findMainPlatform(platforms, points);

            if (mainPlatform != null) {
                float platformTopY = mainPlatform.getY();
                // Si la plataforma no está cerca del inicio de la escalera, mantener colisión
                if (Math.abs(platformTopY - ladderTopY) >= PLAYER_HEIGHT) {
                    handleCollisionResolution(mainPlatform, null);
                }
            }
        } else {
            // Comportamiento normal de colisión
            CollisionPoints points = calculateCollisionPoints();
            Platform mainPlatform = findMainPlatform(platforms, points);
            Platform stepPlatform = null;

            if (shouldCheckStepPlatforms(mainPlatform)) {
                stepPlatform = findStepPlatform(platforms, points);
            }

            handleCollisionResolution(mainPlatform, stepPlatform);
            updateDebugPoints(points);
        }
    }

    private record CollisionPoints(
            float centerX,
            float bottom,
            float previousBottom,
            float leftCheckX,
            float rightCheckX
    ) {
    }

    private CollisionPoints calculateCollisionPoints() {
        return new CollisionPoints(
                xPos + (PLAYER_WIDTH / 2),
                yPos + PLAYER_HEIGHT,
                yPos - velocityY + PLAYER_HEIGHT,
                xPos + 2,
                xPos + PLAYER_WIDTH - 2
        );
    }

    private Platform findMainPlatform(List<Platform> platforms, CollisionPoints points) {
        return platforms.stream()
                .filter(platform -> isMainPlatformCollision(platform, points))
                .findFirst()
                .orElse(null);
    }

    private boolean isMainPlatformCollision(Platform platform, CollisionPoints points) {
        return points.centerX >= platform.getX() &&
                points.centerX <= platform.getX() + Platform.WIDTH &&
                velocityY >= 0 &&
                points.previousBottom <= platform.getY() + TOLERANCE &&
                points.bottom >= platform.getY();
    }

    private boolean shouldCheckStepPlatforms(Platform mainPlatform) {
        return mainPlatform == null && currentPlatform != null && Math.abs(velocityX) > 0;
    }

    private Platform findStepPlatform(List<Platform> platforms, CollisionPoints points) {
        Platform immediateStep = findImmediateStepPlatform(platforms, points);
        return immediateStep != null ? immediateStep : findNextStepPlatform(platforms);
    }

    private Platform findImmediateStepPlatform(List<Platform> platforms, CollisionPoints points) {
        float checkX = velocityX > 0 ? points.rightCheckX : points.leftCheckX;

        return platforms.stream()
                .filter(platform -> isValidStepPlatform(platform, checkX))
                .findFirst()
                .orElse(null);
    }

    private boolean isValidStepPlatform(Platform platform, float checkX) {
        if (!(checkX >= platform.getX() && checkX <= platform.getX() + Platform.WIDTH)) {
            return false;
        }

        if (velocityX > 0) {
            return isRightStepValid(platform);
        } else {
            return isLeftStepValid(platform);
        }
    }

    private boolean isRightStepValid(Platform platform) {
        return platform.getY() == currentPlatform.getY() - 1 &&
                Math.abs(platform.getX() - (currentPlatform.getX() + Platform.WIDTH)) < TOLERANCE;
    }

    private boolean isLeftStepValid(Platform platform) {
        return platform.getY() == currentPlatform.getY() - 1 &&
                Math.abs(platform.getX() - (currentPlatform.getX() - Platform.WIDTH)) < TOLERANCE;
    }

    private Platform findNextStepPlatform(List<Platform> platforms) {
        return platforms.stream()
                .filter(this::isNextStepValid)
                .findFirst()
                .orElse(null);
    }

    private boolean isNextStepValid(Platform platform) {
        if (velocityX > 0) {
            return platform.getX() == currentPlatform.getX() + Platform.WIDTH * 2 &&
                    Math.abs(platform.getY() - (currentPlatform.getY() + 1)) < TOLERANCE;
        } else {
            return platform.getX() == currentPlatform.getX() - Platform.WIDTH * 2 &&
                    Math.abs(platform.getY() - (currentPlatform.getY() + 1)) < TOLERANCE;
        }
    }

    private void handleCollisionResolution(Platform mainPlatform, Platform stepPlatform) {
        if (mainPlatform != null) {
            resolveCollision(mainPlatform);
        } else if (stepPlatform != null) {
            resolveCollision(stepPlatform);
        } else if (!isClimbing) {
            currentPlatform = null;
        }
    }

    private void resolveCollision(Platform platform) {
        yPos = platform.getY() - PLAYER_HEIGHT;
        velocityY = 0;
        isJumping = false;
        currentPlatform = platform;
    }

    private void updateDebugPoints(CollisionPoints points) {
        if (!isDebugMode) return;

        debugPoints.clear();
        debugPoints.add(new DebugPoint(points.centerX, points.bottom, 0xFFFF0000));
        debugPoints.add(new DebugPoint(points.leftCheckX, points.bottom - 2, 0xFF00FF00));
        debugPoints.add(new DebugPoint(points.rightCheckX, points.bottom - 2, 0xFF0000FF));

        if (isOnLadder && currentPlatform != null) {
            debugPoints.add(new DebugPoint(
                    currentPlatform.getX() + Platform.WIDTH / 2,
                    currentPlatform.getY(),
                    0xFFFFFF00
            ));
        }
    }

    // Constantes adicionales
    private static final float TOLERANCE = 0.5f; // Aumentado para mayor tolerancia en las comprobaciones

    private void updatePosition() {
        xPos += velocityX;
        yPos += velocityY;
    }

    public void jump(int screenHeight) {
        // Permitir el salto solo si el jugador no está ya saltando y está sobre una plataforma
        if (!isJumping && (currentPlatform != null || yPos + PLAYER_HEIGHT >= screenHeight - 1)) {
            velocityY = JUMP_FORCE; // Aplicar fuerza de salto
            isJumping = true; // Marcar que está saltando
            currentPlatform = null; // Desasociar la plataforma actual durante el salto
        }
    }

    private void handleScreenBounds(int screenXpos, int screenYpos, int screenWidth, int screenHeight) {
        // Límites horizontales
        if (xPos < screenXpos) {
            xPos = screenXpos;
        }
        if (xPos + PLAYER_WIDTH > screenXpos + screenWidth) {
            xPos = screenXpos + screenWidth - PLAYER_WIDTH;
        }

        // Límites verticales
        if (yPos < screenYpos) {
            yPos = screenYpos;
        }

        // Manejo del límite inferior de la pantalla
        if (yPos + PLAYER_HEIGHT > screenYpos + screenHeight) {
            yPos = screenYpos + screenHeight - PLAYER_HEIGHT;
            velocityY = 0;
            isJumping = false;
            gameScreen.setShowJumpMessage(true);
        } else {
            gameScreen.setShowJumpMessage(false);
        }
    }

    private void checkLadderContact() {
        if (isOnLadder && currentLadder != null && !isTouchingLadder(currentLadder)) {
            stopClimbing();
        }
    }

    private boolean isTouchingLadder(Ladder ladder) {
        float playerCenterX = xPos + PLAYER_WIDTH / 2;
        float playerTop = yPos;
        float playerBottom = yPos + PLAYER_HEIGHT;

        return playerCenterX > ladder.getX() - LADDER_TOLERANCE &&
                playerCenterX < ladder.getX() + ladder.getWidth() + LADDER_TOLERANCE &&
                playerBottom >= ladder.getY() &&
                playerTop <= ladder.getY() + ladder.getHeight();
    }

    private void checkLadderLimits() {
        if (yPos + PLAYER_HEIGHT <= currentLadder.getY()) {
            handleTopOfLadder();
        } else if (yPos >= currentLadder.getY() + currentLadder.getHeight()) {
            handleBottomOfLadder();
        }
    }

    private void handleTopOfLadder() {
        Ladder nextLadder = findNextLadder();
        if (nextLadder != null) {
            currentLadder = nextLadder;
            yPos = currentLadder.getY() + currentLadder.getHeight() - PLAYER_HEIGHT;
        } else {
            yPos = currentLadder.getY() - PLAYER_HEIGHT;
            if (!movingUp) {
                stopClimbing();
            }
        }
    }


    private void handleBottomOfLadder() {
        if (currentLadder != null && currentLadder.isCanPassThroughPlatform()) {
            float ladderBottom = currentLadder.getY() + currentLadder.getHeight();

            // Verificar si hay una plataforma exactamente al final de la escalera
            boolean platformAtBottom = false;
            for (Platform platform : gameScreen.getPlatforms()) {
                if (Math.abs(platform.getY() - ladderBottom) < 2 && // Casi exactamente al final
                        xPos + PLAYER_WIDTH > platform.getX() &&
                        xPos < platform.getX() + Platform.WIDTH) {
                    platformAtBottom = true;
                    break;
                }
            }

            // Si el jugador está llegando al final de la escalera
            if (yPos + PLAYER_HEIGHT >= ladderBottom - 2) {
                if (platformAtBottom) {
                    // Si hay plataforma al final, detener al jugador antes de tocarla
                    yPos = ladderBottom - PLAYER_HEIGHT - 1;
                    stopClimbing();
                } else {
                    // Si no hay plataforma, dejar caer al jugador
                    stopClimbing();
                    velocityY = 0.1f;
                }
            }
        } else {
            // Para escaleras normales, comportamiento actual
            yPos = currentLadder.getY() + currentLadder.getHeight();
            if (!movingDown) {
                stopClimbing();
            }
        }
    }

    private Ladder findNextLadder() {
        if (currentLadder == null) return null;

        // Buscar la siguiente escalera que esté conectada con la actual
        for (Ladder ladder : gameScreen.getLadders()) {
            if (ladder != currentLadder &&
                    ladder.getX() == currentLadder.getX() &&
                    ladder.getY() + ladder.getHeight() == currentLadder.getY()) {
                return ladder;
            }
        }
        return null;
    }

    public void checkBarrelJump(List<Barrel> barrels) {
        if (isJumping) {
            for (Barrel barrel : barrels) {
                // Verificar si el jugador está por encima del barril
                boolean isAboveBarrel = yPos + PLAYER_HEIGHT <= barrel.getY() &&
                        xPos < barrel.getX() + barrel.getWidth() &&
                        xPos + PLAYER_WIDTH > barrel.getX();

                // Verificar si el jugador está lo suficientemente cerca verticalmente
                boolean isCloseEnough = barrel.getY() - (yPos + PLAYER_HEIGHT) < 20; // Ajusta este valor según necesites

                if (isAboveBarrel && isCloseEnough && !hasJumpedOverBarrel) {
                    score += 100; // Sumar puntos
                    hasJumpedOverBarrel = true; // Marcar que ya se contó este salto
                }
            }
        } else {
            hasJumpedOverBarrel = false; // Resetear cuando no está saltando
        }
    }
}