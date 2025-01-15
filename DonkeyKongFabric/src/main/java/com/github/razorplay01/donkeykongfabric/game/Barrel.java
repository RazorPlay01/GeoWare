package com.github.razorplay01.donkeykongfabric.game;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;

import java.util.*;

import static com.github.razorplay01.donkeykongfabric.game.Platform.WIDTH;

@Getter
public class Barrel {
    private float x;
    private float y;
    private float width = 12;
    private float height = 12;

    private final Random random = new Random(System.currentTimeMillis());
    private final Set<Ladder> checkedLadders = new HashSet<>();
    private float velocityX = 0.6f; // Velocidad horizontal del barril
    private float velocityY = 0;   // Velocidad vertical (caída)
    private static final float GRAVITY = 0.5f; // Gravedad aplicada al barril
    private static final float MAX_FALL_SPEED = 4f; // Velocidad máxima de caída
    private static final float LADDER_DESCENT_SPEED = 0.5f; // Velocidad al bajar escaleras
    private static final float PROBABILITY_TO_USE_LADDER = 0.25f; // Probabilidad de usar la escalera (10%)

    private boolean isOnLadder = false; // Indica si el barril está usando una escalera
    private Ladder currentLadder = null; // La escalera actual que el barril está usando
    private boolean isColliding = false; // Para renderizar el color en colisión

    private int ladderCooldown = 0; // Cooldown para verificar escaleras (en ticks)

    private boolean shouldChangeDirectionAfterLadder = false;

    public Barrel(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Actualiza la lógica del barril.
     *
     * @return true si el barril debe ser eliminado, false en caso contrario.
     */
    public boolean update(int screenXpos, int screenWidth, List<Platform> platforms, Player player, List<Ladder> ladders) {
        // Reducir el cooldown si es mayor a 0
        if (ladderCooldown > 0) {
            ladderCooldown--;
        }

        // Si el barril está en una escalera
        if (isOnLadder && currentLadder != null) {
            y += LADDER_DESCENT_SPEED;
            x = currentLadder.getX() + (currentLadder.getWidth() - width) / 2;

            // Verificar si el barril está cerca del final de la escalera
            float ladderBottom = currentLadder.getY() + currentLadder.getHeight();
            if (y + height >= ladderBottom - 4) { // 4 píxeles antes del final
                // Buscar la plataforma más cercana debajo del barril
                Platform nextPlatform = findNextPlatform(platforms);

                // Si hay una plataforma cercana (dentro de un rango razonable)
                if (nextPlatform != null &&
                        Math.abs(nextPlatform.getY() - (y + height)) <= 4) {
                    // Alinear con la plataforma
                    y = nextPlatform.getY() - height;
                    isOnLadder = false;
                    currentLadder = null;
                    if (shouldChangeDirectionAfterLadder) {
                        velocityX = -velocityX;
                        shouldChangeDirectionAfterLadder = false;
                    }
                } else {
                    // Si no hay plataforma cercana, simplemente dejar de usar la escalera
                    // y dejar que la gravedad actúe
                    isOnLadder = false;
                    currentLadder = null;
                    if (shouldChangeDirectionAfterLadder) {
                        velocityX = -velocityX;
                        shouldChangeDirectionAfterLadder = false;
                    }
                    // Iniciar con una velocidad vertical pequeña para una transición suave
                    velocityY = 0.1f;
                }
            }
            return false;
        }

        // Aplicar gravedad cuando no está en una escalera
        velocityY += GRAVITY;
        if (velocityY > MAX_FALL_SPEED) {
            velocityY = MAX_FALL_SPEED;
        }

        // Actualizar posición vertical
        y += velocityY;

        // Colisión con plataformas
        for (Platform platform : platforms) {
            if (isCollidingWithPlatform(platform)) {
                y = platform.getY() - height;
                velocityY = 0;
                // Verificar escaleras solo si está en una plataforma y no tiene cooldown
                if (ladderCooldown == 0) {
                    checkLadderCollision(ladders);
                }
                break;
            }
        }

        // Actualizar posición horizontal
        x += velocityX;

        // Manejar colisiones con los bordes de la pantalla
        if (x < screenXpos || x + width > screenXpos + screenWidth) {
            if (y > player.getYPos() + 32) {
                // Si el barril está por debajo del jugador y toca el borde, marcar para eliminar
                return true;
            } else {
                // Cambiar de dirección si está por encima del jugador
                velocityX *= -1;
                x = Math.clamp(x, screenXpos, screenXpos + screenWidth - width);
            }
        }

        return false;
    }

    private Platform findNextPlatform(List<Platform> platforms) {
        Platform closest = null;
        float minDistance = Float.MAX_VALUE;
        float barrelBottom = y + height;

        for (Platform platform : platforms) {
            if (platform.getY() > barrelBottom) { // Solo plataformas debajo del barril
                float distance = platform.getY() - barrelBottom;
                if (distance < minDistance &&
                        x + width > platform.getX() &&
                        x < platform.getX() + Platform.WIDTH) {
                    minDistance = distance;
                    closest = platform;
                }
            }
        }
        return closest;
    }

    /**
     * Verificar colisión con una escalera válida.
     * Una escalera es válida si está centrada en relación al barril.
     */
    private void checkLadderCollision(List<Ladder> ladders) {
        List<Ladder> validLadders = new ArrayList<>();

        // Solo considerar escaleras que no han sido verificadas antes
        for (Ladder ladder : ladders) {
            if (!checkedLadders.contains(ladder) &&
                    ladder.isCanPassThroughPlatform() &&
                    ladder.canUseLadder(this)) {
                validLadders.add(ladder);
            }
        }

        // Si hay escaleras válidas no verificadas
        if (!validLadders.isEmpty()) {
            // Seleccionar una escalera aleatoria de las válidas
            Ladder selectedLadder = validLadders.get(random.nextInt(validLadders.size()));

            // Marcar esta escalera como verificada
            checkedLadders.add(selectedLadder);

            // Aplicar la probabilidad solo a esta escalera
            if (random.nextFloat() < PROBABILITY_TO_USE_LADDER) {
                isOnLadder = true;
                currentLadder = selectedLadder;
                velocityY = 0;
                shouldChangeDirectionAfterLadder = true;
                x = selectedLadder.getX() + (selectedLadder.getWidth() - width) / 2;
                ladderCooldown = 20;
            }
        }
    }

    // Verificar colisión del barril con una plataforma
    private boolean isCollidingWithPlatform(Platform platform) {
        // Si el barril está en una escalera atravesable
        if (isOnLadder && currentLadder != null && currentLadder.isCanPassThroughPlatform()) {
            // Solo permitir atravesar la plataforma que está al inicio de la escalera
            float ladderTopY = currentLadder.getY();
            float platformTopY = platform.getY();

            // Si la plataforma está cerca del inicio de la escalera, permitir atravesarla
            if (Math.abs(platformTopY - ladderTopY) < height) {
                return false;
            }
        }

        // Colisión normal con la plataforma
        return x + width > platform.getX() &&
                x < platform.getX() + WIDTH &&
                y + height > platform.getY() &&
                y + height - velocityY <= platform.getY();
    }

    // Verificar colisión con el jugador
    public void checkCollision(Player player) {
        float hitboxX = x + 2;
        float hitboxY = y + 2;
        float hitboxWidth = 8;
        float hitboxHeight = 8;

        isColliding = player.getXPos() < hitboxX + hitboxWidth &&
                player.getXPos() + Player.PLAYER_WIDTH > hitboxX &&
                player.getYPos() < hitboxY + hitboxHeight &&
                player.getYPos() + Player.PLAYER_HEIGHT > hitboxY;
    }

    // Dibujar el barril
    public void render(DrawContext context) {
        context.fill(
                (int) x,
                (int) y,
                (int) (x + width),
                (int) (y + height),
                isColliding ? 0xFFFF0000 : 0xFF8B4513 // Rojo si está en colisión, marrón si no
        );
    }
}