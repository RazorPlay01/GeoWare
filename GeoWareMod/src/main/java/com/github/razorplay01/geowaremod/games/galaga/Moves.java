package com.github.razorplay01.geowaremod.games.galaga;

import com.github.razorplay01.razorplayapi.util.screen.GameScreen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static com.github.razorplay01.geowaremod.games.galaga.Enemy.ENEMY_WIDTH;
import static com.github.razorplay01.geowaremod.games.galaga.GalagaGame.GAME_WIDTH;

public class Moves {
    private Moves() {
        // []
    }

    private static final float[][] moves = {
            {0.0f, -1.0f}, // DIR_N (0)
            {0.25f, -1.0f}, // DIR_NNE (1)
            {0.75f, -0.75f}, // DIR_NE (2)
            {1.0f, -0.25f}, // DIR_ENE (3)
            {1.0f, 0.0f}, // DIR_E (4)
            {1.0f, 0.25f}, // DIR_ESE (5)
            {0.75f, 0.75f}, // DIR_SE (6)
            {0.25f, 1.0f}, // DIR_SSE (7)
            {0.0f, 1.0f}, // DIR_S (8)
            {-0.25f, 1.0f}, // DIR_SSW (9)
            {-0.75f, 0.75f}, // DIR_SW (10)
            {-1.0f, 0.25f}, // DIR_WSW (11)
            {-1.0f, 0.0f}, // DIR_W (12)
            {-1.0f, -0.25f}, // DIR_WNW (13)
            {-0.75f, -0.75f}, // DIR_NW (14)
            {-0.25f, -1.0f} // DIR_NNW (15)
    };

    public static void moveDirection(Enemy enemy, int direction, float speed) {
        enemy.setXPos(enemy.getXPos() + moves[direction][0] * speed);
        enemy.setYPos(enemy.getYPos() + moves[direction][1] * speed);
        enemy.update();
    }

    public static void moveCircleRight(Enemy enemy, int radius, float speed) {
        moveDirection(enemy, enemy.getDirection(), speed);
        enemy.setCircleProgress(enemy.getCircleProgress() + speed);
        if (enemy.getCircleProgress() > (2 * radius * Math.sin(Math.PI / 16))) {
            enemy.setCircleProgress(0);
            enemy.setDirection((enemy.getDirection() + 1) % 16);
        }
    }

    public static void moveCircleLeft(Enemy enemy, int radius, float speed) {
        moveDirection(enemy, enemy.getDirection(), speed);
        enemy.setCircleProgress(enemy.getCircleProgress() + speed);
        if (enemy.getCircleProgress() > (2 * radius * Math.sin(Math.PI / 16))) {
            enemy.setCircleProgress(0);
            enemy.setDirection(enemy.getDirection() - 1 < 0 ? 15 : enemy.getDirection() - 1);
        }
    }

    public static void moveToPosition(Enemy enemy, float x, float y, float speed) {
        float[] vector = {x - enemy.getXPos(), y - enemy.getYPos()};
        float length = (float) Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1]);
        if (length > 1) {
            float[] unitVector = {(vector[0] / length) * speed, (vector[1] / length) * speed};
            enemy.setXPos(enemy.getXPos() + unitVector[0]);
            enemy.setYPos(enemy.getYPos() + unitVector[1]);
            enemy.update();
        } else {
            enemy.setXPos(x);
            enemy.setYPos(y);
            enemy.setMovingToPosition(false);
        }
    }

    private static final long SPAWN_DELAY = 5000;
    public static int spawnPhase = 0;
    public static long lastSpawnTime = 0;
    public static final Map<Integer, Integer> waveSpawnCounts = new HashMap<>();
    public static final Map<Integer, Float> waveTimers = new HashMap<>();
    public static final Map<Integer, Float> waveOffsets = new HashMap<>();
    public static final Map<Integer, Integer> waveStates = new HashMap<>();

    public static void spawnPatron1(GameScreen screen) {
        GalagaGame game = (GalagaGame) screen.getGame();
        List<Enemy> enemies = game.getEnemies();
        float[] formationX = game.getFormationX();
        float[] formationY = game.getFormationY();

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime < SPAWN_DELAY) return;

        float screenX = screen.getGameScreenXPos();
        float screenY = screen.getGameScreenYPos();

        Random random = new Random(); // Generador de números aleatorios

        if (spawnPhase == 0) {
            // Fila 1 (waveIndex == 0)
            enemies.add(new Enemy(screenX, screenY + 40, screen, 0).setType(2)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[0]).setFinalTargetY(screenY + formationY[0]));
            enemies.add(new Enemy(screenX, screenY + 60, screen, 0).setType(2)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[1]).setFinalTargetY(screenY + formationY[0]));
            enemies.add(new Enemy(screenX, screenY + 80, screen, 0).setType(2)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[2]).setFinalTargetY(screenY + formationY[0]));
            enemies.add(new Enemy(screenX, screenY + 100, screen, 0).setType(2)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[3]).setFinalTargetY(screenY + formationY[0]));
            enemies.add(new Enemy(screenX, screenY + 120, screen, 0).setType(3)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[4]).setFinalTargetY(screenY + formationY[0]));
            enemies.add(new Enemy(screenX, screenY + 140, screen, 0).setType(3)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[5]).setFinalTargetY(screenY + formationY[0]));
            enemies.add(new Enemy(screenX, screenY + 160, screen, 0).setType(3)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[6]).setFinalTargetY(screenY + formationY[0]));
            enemies.add(new Enemy(screenX, screenY + 180, screen, 0).setType(3)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[7]).setFinalTargetY(screenY + formationY[0]));
            spawnPhase++;
            lastSpawnTime = currentTime;
        } else if (spawnPhase == 1 && enemies.stream().filter(e -> e.getWaveIndex() == 0).allMatch(e -> e.getPhase() >= 4)) {
            // Fila 2 (waveIndex == 1)
            enemies.add(new Enemy(screenX + 206, screenY + 40, screen, 1)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[7]).setFinalTargetY(screenY + formationY[1]));
            enemies.add(new Enemy(screenX + 206, screenY + 60, screen, 1)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[6]).setFinalTargetY(screenY + formationY[1]));
            enemies.add(new Enemy(screenX + 206, screenY + 80, screen, 1)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[5]).setFinalTargetY(screenY + formationY[1]));
            enemies.add(new Enemy(screenX + 206, screenY + 100, screen, 1)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[4]).setFinalTargetY(screenY + formationY[1]));
            enemies.add(new Enemy(screenX + 206, screenY + 120, screen, 1)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[3]).setFinalTargetY(screenY + formationY[1]));
            enemies.add(new Enemy(screenX + 206, screenY + 140, screen, 1)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[2]).setFinalTargetY(screenY + formationY[1]));
            enemies.add(new Enemy(screenX + 206, screenY + 160, screen, 1)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[1]).setFinalTargetY(screenY + formationY[1]));
            enemies.add(new Enemy(screenX + 206, screenY + 180, screen, 1)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[0]).setFinalTargetY(screenY + formationY[1]));
            spawnPhase++;
            lastSpawnTime = currentTime;
        } else if (spawnPhase == 2 && enemies.stream().filter(e -> e.getWaveIndex() == 1).allMatch(e -> e.getPhase() >= 4)) {
            // Fila 3 (waveIndex == 2)
            enemies.add(new Enemy(screenX + 50, screenY, screen, 2)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[0]).setFinalTargetY(screenY + formationY[2]));
            enemies.add(new Enemy(screenX + 70, screenY, screen, 2)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[1]).setFinalTargetY(screenY + formationY[2]));
            enemies.add(new Enemy(screenX + 90, screenY, screen, 2)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[2]).setFinalTargetY(screenY + formationY[2]));
            enemies.add(new Enemy(screenX + 110, screenY, screen, 2)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[3]).setFinalTargetY(screenY + formationY[2]));
            enemies.add(new Enemy(screenX + 130, screenY, screen, 2)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[4]).setFinalTargetY(screenY + formationY[2]));
            enemies.add(new Enemy(screenX + 150, screenY, screen, 2)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[5]).setFinalTargetY(screenY + formationY[2]));
            enemies.add(new Enemy(screenX + 170, screenY, screen, 2)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[6]).setFinalTargetY(screenY + formationY[2]));
            enemies.add(new Enemy(screenX + 190, screenY, screen, 2)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[7]).setFinalTargetY(screenY + formationY[2]));
            spawnPhase++;
            lastSpawnTime = currentTime;
        } else if (spawnPhase == 3 && enemies.stream().filter(e -> e.getWaveIndex() == 2).allMatch(e -> e.getPhase() >= 4)) {
            // Fila 4 (waveIndex == 3)
            int[] leftYPositions = {280, 270, 260, 250};
            int[] rightYPositions = {280, 270, 260, 250};
            int leftIndex = waveSpawnCounts.getOrDefault(3, 0) / 2;
            int rightIndex = (waveSpawnCounts.getOrDefault(3, 0) - 1) / 2;

            if (waveSpawnCounts.getOrDefault(3, 0) < 8) {
                if (currentTime - lastSpawnTime >= 10) {
                    if (leftIndex < 4) {
                        enemies.add(new Enemy(screenX, screenY + leftYPositions[leftIndex], screen, 3)
                                .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                                .setFinalTargetX(screenX + formationX[3 - leftIndex])
                                .setFinalTargetY(screenY + formationY[3]));
                        waveSpawnCounts.put(3, waveSpawnCounts.getOrDefault(3, 0) + 1);
                    }
                    if (rightIndex < 4) {
                        enemies.add(new Enemy(screenX + 206, screenY + rightYPositions[rightIndex], screen, 3)
                                .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                                .setFinalTargetX(screenX + formationX[4 + rightIndex])
                                .setFinalTargetY(screenY + formationY[3]));
                        waveSpawnCounts.put(3, waveSpawnCounts.getOrDefault(3, 0) + 1);
                    }
                    lastSpawnTime = currentTime;
                }
            } else {
                spawnPhase++;
                lastSpawnTime = currentTime;
            }
        } else if (spawnPhase == 4 && enemies.stream().filter(e -> e.getWaveIndex() == 3).allMatch(e -> e.getPhase() >= 4)) {
            // Fila 5 (waveIndex == 4)
            enemies.add(new Enemy(screenX, screenY + 140, screen, 4)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[3]).setFinalTargetY(screenY + formationY[4]));
            enemies.add(new Enemy(screenX, screenY + 160, screen, 4)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[2]).setFinalTargetY(screenY + formationY[4]));
            enemies.add(new Enemy(screenX, screenY + 180, screen, 4)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[1]).setFinalTargetY(screenY + formationY[4]));
            enemies.add(new Enemy(screenX, screenY + 200, screen, 4)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[0]).setFinalTargetY(screenY + formationY[4]));
            enemies.add(new Enemy(screenX + 206, screenY + 140, screen, 4)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[4]).setFinalTargetY(screenY + formationY[4]));
            enemies.add(new Enemy(screenX + 206, screenY + 160, screen, 4)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[5]).setFinalTargetY(screenY + formationY[4]));
            enemies.add(new Enemy(screenX + 206, screenY + 180, screen, 4)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[6]).setFinalTargetY(screenY + formationY[4]));
            enemies.add(new Enemy(screenX + 206, screenY + 200, screen, 4)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[7]).setFinalTargetY(screenY + formationY[4]));
            spawnPhase++;
            lastSpawnTime = currentTime;
        }
    }

    public static void spawnPatron2(GameScreen screen) {
        GalagaGame game = (GalagaGame) screen.getGame();
        List<Enemy> enemies = game.getEnemies();
        float[] formationX = game.getFormationX();
        float[] formationY = game.getFormationY();

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime < SPAWN_DELAY) return;

        float screenX = screen.getGameScreenXPos();
        float screenY = screen.getGameScreenYPos();

        float SPACING_X = (GAME_WIDTH - (ENEMY_WIDTH * 8)) / 9.0f;
        float centerX = screenX + (GAME_WIDTH - (ENEMY_WIDTH * 8 + SPACING_X * 7)) / 2.0f + ENEMY_WIDTH / 2.0f;

        Random random = new Random(); // Generador de números aleatorios

        if (spawnPhase == 0) {
            // Fila 1 (waveIndex == 0)
            enemies.add(new Enemy(centerX + 0 * (ENEMY_WIDTH + SPACING_X), screenY, screen, 0).setType(2)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[0]).setFinalTargetY(screenY + formationY[0]));
            enemies.add(new Enemy(centerX + 1 * (ENEMY_WIDTH + SPACING_X), screenY, screen, 0).setType(2)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[1]).setFinalTargetY(screenY + formationY[0]));
            enemies.add(new Enemy(centerX + 2 * (ENEMY_WIDTH + SPACING_X), screenY, screen, 0).setType(2)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[2]).setFinalTargetY(screenY + formationY[0]));
            enemies.add(new Enemy(centerX + 3 * (ENEMY_WIDTH + SPACING_X), screenY, screen, 0).setType(2)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[3]).setFinalTargetY(screenY + formationY[0]));
            enemies.add(new Enemy(centerX + 4 * (ENEMY_WIDTH + SPACING_X), screenY, screen, 0).setType(3)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[4]).setFinalTargetY(screenY + formationY[0]));
            enemies.add(new Enemy(centerX + 5 * (ENEMY_WIDTH + SPACING_X), screenY, screen, 0).setType(3)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[5]).setFinalTargetY(screenY + formationY[0]));
            enemies.add(new Enemy(centerX + 6 * (ENEMY_WIDTH + SPACING_X), screenY, screen, 0).setType(3)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[6]).setFinalTargetY(screenY + formationY[0]));
            enemies.add(new Enemy(centerX + 7 * (ENEMY_WIDTH + SPACING_X), screenY, screen, 0).setType(3)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[7]).setFinalTargetY(screenY + formationY[0]));
            spawnPhase++;
            lastSpawnTime = currentTime;
        } else if (spawnPhase == 1 && enemies.stream().filter(e -> e.getWaveIndex() == 0).allMatch(e -> e.getPhase() >= 4)) {
            // Fila 2 (waveIndex == 1)
            int wave1Count = enemies.stream().filter(e -> e.getWaveIndex() == 1).toArray().length;
            if (wave1Count < 8) {
                if (wave1Count % 2 == 0) {
                    enemies.add(new Enemy(screenX, screenY + 140, screen, 1)
                            .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                            .setFinalTargetX(screenX + formationX[wave1Count]).setFinalTargetY(screenY + formationY[1]));
                    enemies.add(new Enemy(screenX, screenY + 160, screen, 1)
                            .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                            .setFinalTargetX(screenX + formationX[wave1Count + 1]).setFinalTargetY(screenY + formationY[1]));
                    lastSpawnTime = currentTime - SPAWN_DELAY + 500;
                }
            } else {
                spawnPhase++;
                lastSpawnTime = currentTime;
            }
        } else if (spawnPhase == 2 && enemies.stream().filter(e -> e.getWaveIndex() == 1).allMatch(e -> e.getPhase() >= 4)) {
            // Fila 3 (waveIndex == 2)
            int wave2Count = enemies.stream().filter(e -> e.getWaveIndex() == 2).toArray().length;
            if (wave2Count < 8) {
                if (wave2Count % 2 == 0) {
                    enemies.add(new Enemy(screenX + 206, screenY + 140, screen, 2)
                            .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                            .setFinalTargetX(screenX + formationX[wave2Count]).setFinalTargetY(screenY + formationY[2]));
                    enemies.add(new Enemy(screenX + 206, screenY + 160, screen, 2)
                            .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                            .setFinalTargetX(screenX + formationX[wave2Count + 1]).setFinalTargetY(screenY + formationY[2]));
                    lastSpawnTime = currentTime - SPAWN_DELAY + 500;
                }
            } else {
                spawnPhase++;
                lastSpawnTime = currentTime;
            }
        } else if (spawnPhase == 3 && enemies.stream().filter(e -> e.getWaveIndex() == 2).allMatch(e -> e.getPhase() >= 4)) {
            // Fila 4 (waveIndex == 3)
            enemies.add(new Enemy(centerX + 0 * (ENEMY_WIDTH + SPACING_X), screenY, screen, 3)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[0]).setFinalTargetY(screenY + formationY[3]));
            enemies.add(new Enemy(centerX + 1 * (ENEMY_WIDTH + SPACING_X), screenY, screen, 3)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[1]).setFinalTargetY(screenY + formationY[3]));
            enemies.add(new Enemy(centerX + 2 * (ENEMY_WIDTH + SPACING_X), screenY, screen, 3)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[2]).setFinalTargetY(screenY + formationY[3]));
            enemies.add(new Enemy(centerX + 3 * (ENEMY_WIDTH + SPACING_X), screenY, screen, 3)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[3]).setFinalTargetY(screenY + formationY[3]));
            enemies.add(new Enemy(centerX + 4 * (ENEMY_WIDTH + SPACING_X), screenY, screen, 3)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[4]).setFinalTargetY(screenY + formationY[3]));
            enemies.add(new Enemy(centerX + 5 * (ENEMY_WIDTH + SPACING_X), screenY, screen, 3)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[5]).setFinalTargetY(screenY + formationY[3]));
            enemies.add(new Enemy(centerX + 6 * (ENEMY_WIDTH + SPACING_X), screenY, screen, 3)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[6]).setFinalTargetY(screenY + formationY[3]));
            enemies.add(new Enemy(centerX + 7 * (ENEMY_WIDTH + SPACING_X), screenY, screen, 3)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[7]).setFinalTargetY(screenY + formationY[3]));
            spawnPhase++;
            lastSpawnTime = currentTime;
        } else if (spawnPhase == 4 && enemies.stream().filter(e -> e.getWaveIndex() == 3).allMatch(e -> e.getPhase() >= 4)) {
            // Fila 5 (waveIndex == 4)
            // Mitad izquierda
            enemies.add(new Enemy(screenX, screenY + 140, screen, 4)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[0]).setFinalTargetY(screenY + formationY[4]));
            enemies.add(new Enemy(screenX, screenY + 160, screen, 4)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[1]).setFinalTargetY(screenY + formationY[4]));
            enemies.add(new Enemy(screenX, screenY + 180, screen, 4)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[2]).setFinalTargetY(screenY + formationY[4]));
            enemies.add(new Enemy(screenX, screenY + 200, screen, 4)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[3]).setFinalTargetY(screenY + formationY[4]));
            // Mitad derecha (invertimos posiciones finales)
            enemies.add(new Enemy(screenX + 206, screenY + 140, screen, 4)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[7]).setFinalTargetY(screenY + formationY[4]));
            enemies.add(new Enemy(screenX + 206, screenY + 160, screen, 4)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[6]).setFinalTargetY(screenY + formationY[4]));
            enemies.add(new Enemy(screenX + 206, screenY + 180, screen, 4)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[5]).setFinalTargetY(screenY + formationY[4]));
            enemies.add(new Enemy(screenX + 206, screenY + 200, screen, 4)
                    .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                    .setFinalTargetX(screenX + formationX[4]).setFinalTargetY(screenY + formationY[4]));
            spawnPhase++;
            lastSpawnTime = currentTime;
        }
    }

    public static void spawnPatron3(GameScreen screen) {
        GalagaGame game = (GalagaGame) screen.getGame();
        List<Enemy> enemies = game.getEnemies();
        float[] formationX = game.getFormationX();
        float[] formationY = game.getFormationY();

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime < SPAWN_DELAY / 8) return; // Spawn 1 a 1 más rápido

        float screenX = screen.getGameScreenXPos();
        float screenY = screen.getGameScreenYPos();

        float centerX = screenX + GAME_WIDTH / 2f - ENEMY_WIDTH / 2f; // Centro ≈ screenX + 103

        Random random = new Random(); // Generador de números aleatorios

        if (spawnPhase == 0) {
            int wave0Count = waveSpawnCounts.getOrDefault(0, 0); // Usar getOrDefault para evitar NullPointerException
            if (wave0Count < 8) {
                enemies.add(new Enemy(centerX, screenY, screen, 0)
                        .setType(wave0Count < 4 ? 2 : 3)
                        .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                        .setFinalTargetX(screenX + formationX[wave0Count])
                        .setFinalTargetY(screenY + formationY[0]));
                waveSpawnCounts.put(0, wave0Count + 1);
                lastSpawnTime = currentTime;
            } else if (enemies.stream().filter(e -> e.getWaveIndex() == 0).allMatch(e -> e.getPhase() >= 4)) {
                spawnPhase++;
                lastSpawnTime = currentTime;
            }
        } else if (spawnPhase == 1) {
            int wave1Count = waveSpawnCounts.getOrDefault(1, 0); // Usar getOrDefault para evitar NullPointerException
            if (wave1Count < 8) {
                enemies.add(new Enemy(screenX, screenY + 200 + (wave1Count * 15f), screen, 1)
                        .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                        .setFinalTargetX(screenX + formationX[wave1Count % 2 == 0 ? wave1Count / 2 : 7 - (wave1Count - 1) / 2])
                        .setFinalTargetY(screenY + formationY[1]));
                waveSpawnCounts.put(1, wave1Count + 1);
                lastSpawnTime = currentTime;
            } else if (enemies.stream().filter(e -> e.getWaveIndex() == 1).allMatch(e -> e.getPhase() >= 4)) {
                spawnPhase++;
                lastSpawnTime = currentTime;
            }
        } else if (spawnPhase == 2) {
            int wave2Count = waveSpawnCounts.getOrDefault(2, 0); // Usar getOrDefault para evitar NullPointerException
            if (wave2Count < 8) {
                enemies.add(new Enemy(screenX + 206, screenY + 200 + (wave2Count * 15f), screen, 2)
                        .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                        .setFinalTargetX(screenX + formationX[wave2Count % 2 == 0 ? wave2Count / 2 : 7 - (wave2Count - 1) / 2])
                        .setFinalTargetY(screenY + formationY[2]));
                waveSpawnCounts.put(2, wave2Count + 1);
                lastSpawnTime = currentTime;
            } else if (enemies.stream().filter(e -> e.getWaveIndex() == 2).allMatch(e -> e.getPhase() >= 4)) {
                spawnPhase++;
                lastSpawnTime = currentTime;
            }
        } else if (spawnPhase == 3) {
            int wave3Count = waveSpawnCounts.getOrDefault(3, 0); // Usar getOrDefault para evitar NullPointerException
            if (wave3Count < 8) {
                enemies.add(new Enemy(centerX, screenY, screen, 3)
                        .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                        .setFinalTargetX(screenX + formationX[wave3Count])
                        .setFinalTargetY(screenY + formationY[3]));
                waveSpawnCounts.put(3, wave3Count + 1);
                lastSpawnTime = currentTime;
            } else if (enemies.stream().filter(e -> e.getWaveIndex() == 3).allMatch(e -> e.getPhase() >= 4)) {
                spawnPhase++;
                lastSpawnTime = currentTime;
            }
        } else if (spawnPhase == 4) {
            int wave4Count = waveSpawnCounts.getOrDefault(4, 0); // Usar getOrDefault para evitar NullPointerException
            if (wave4Count < 8) {
                enemies.add(new Enemy(centerX, screenY, screen, 4)
                        .setHealth(random.nextDouble() <= 0.7 ? 1 : 2)
                        .setFinalTargetX(screenX + formationX[wave4Count])
                        .setFinalTargetY(screenY + formationY[4]));
                waveSpawnCounts.put(4, wave4Count + 1);
                lastSpawnTime = currentTime;
            } else if (enemies.stream().filter(e -> e.getWaveIndex() == 4).allMatch(e -> e.getPhase() >= 4)) {
                spawnPhase++;
                lastSpawnTime = currentTime;
            }
        }
    }

    public static void moveToFinalPosition(Enemy enemy, GameScreen screen) {
        float screenX = screen.getGameScreenXPos();
        float screenY = screen.getGameScreenYPos();
        int waveIndex = enemy.getWaveIndex();
        int currentPattern = ((GalagaGame) screen.getGame()).getCurrentPattern();

        if (currentPattern == 1) {
            firstPatternHandled(enemy, waveIndex, screenX, screenY);
        } else if (currentPattern == 2) {
            secondPatternHandled(enemy, waveIndex, screenY, screenX);
        } else if (currentPattern == 3) {
            thirdPatternHandled(enemy, waveIndex, screenY, screenX, ((GalagaGame) screen.getGame()));
        }
    }

    private static void firstPatternHandled(Enemy enemy, int waveIndex, float screenX, float screenY) {
        switch (enemy.getPhase()) {
            case 0:
                if (waveIndex == 0) {
                    Moves.moveDirection(enemy, 4, enemy.getSpeed());
                    if (enemy.getXPos() > screenX + 50) {
                        enemy.setPhase(1);
                    }
                } else if (waveIndex == 1) {
                    Moves.moveDirection(enemy, 12, enemy.getSpeed());
                    if (enemy.getXPos() < screenX + 150) {
                        enemy.setPhase(1);
                    }
                } else if (waveIndex == 2) {
                    Moves.moveDirection(enemy, 8, enemy.getSpeed());
                    if (enemy.getYPos() > screenY + 100) {
                        enemy.setPhase(1);
                    }
                } else if (waveIndex == 3) {
                    // Diagonal hacia el centro
                    if (enemy.getXPos() < screenX + 103) {
                        Moves.moveDirection(enemy, 2, enemy.getSpeed()); // Derecha-arriba
                    } else {
                        Moves.moveDirection(enemy, 14, enemy.getSpeed()); // Izquierda-arriba
                    }
                    if (Math.abs(enemy.getXPos() - (screenX + 103)) < 5 && enemy.getYPos() <= screenY + 100) {
                        enemy.setPhase(1);
                    }
                } else if (waveIndex == 4) {
                    // Diagonal hacia el centro (mantenemos como está por ahora, revisaremos después)
                    if (enemy.getXPos() < screenX + 103) {
                        Moves.moveDirection(enemy, 2, enemy.getSpeed()); // Derecha-arriba
                    } else {
                        Moves.moveDirection(enemy, 14, enemy.getSpeed()); // Izquierda-arriba
                    }
                    if (Math.abs(enemy.getXPos() - (screenX + 103)) < 5 && enemy.getYPos() <= screenY + 100) {
                        enemy.setPhase(1);
                    }
                }
                break;
            case 1:
                if (waveIndex == 0) {
                    Moves.moveCircleLeft(enemy, 25, enemy.getSpeed());
                } else if (waveIndex == 1) {
                    Moves.moveCircleRight(enemy, 25, enemy.getSpeed());
                } else if (waveIndex == 2) {
                    Moves.moveCircleRight(enemy, 20, enemy.getSpeed());
                } else if (waveIndex == 3) {
                    // Mitad izquierda gira a la izquierda, mitad derecha a la derecha
                    if (enemy.getXPos() < screenX + 103) {
                        Moves.moveCircleLeft(enemy, 20, enemy.getSpeed());
                    } else {
                        Moves.moveCircleRight(enemy, 20, enemy.getSpeed());
                    }
                } else if (waveIndex == 4) {
                    // Mantenemos el movimiento actual de la fila 5 para revisarlo después
                    if (enemy.getXPos() < screenX + 103) {
                        Moves.moveCircleLeft(enemy, 20, enemy.getSpeed());
                    } else {
                        Moves.moveCircleRight(enemy, 20, enemy.getSpeed());
                    }
                }
                enemy.setCircleSteps(enemy.getCircleSteps() + 1);
                if (enemy.getCircleSteps() > 40) {
                    enemy.setPhase(2);
                    enemy.setCircleSteps(0);
                    enemy.setCircleProgress(0);
                    enemy.setTargetX(enemy.getFinalTargetX());
                    enemy.setTargetY(enemy.getFinalTargetY());
                }
                break;
            case 2:
                Moves.moveToPosition(enemy, enemy.getFinalTargetX(), enemy.getFinalTargetY(), enemy.getSpeed());
                if (Math.abs(enemy.getXPos() - enemy.getFinalTargetX()) < 1 && Math.abs(enemy.getYPos() - enemy.getFinalTargetY()) < 1) {
                    enemy.setPhase(3);
                    enemy.setXPos(enemy.getFinalTargetX());
                    enemy.setYPos(enemy.getFinalTargetY());
                }
                break;
            case 3:
                enemy.setPhase(4);
                break;
        }
    }

    private static void secondPatternHandled(Enemy enemy, int waveIndex, float screenY, float screenX) {
        switch (enemy.getPhase()) {
            case 0:
                if (waveIndex == 0) {
                    Moves.moveDirection(enemy, 8, enemy.getSpeed());
                    if (enemy.getYPos() > screenY + 20) {
                        enemy.setPhase(1);
                        enemy.setTargetX(enemy.getType() == 2 ? screenX + 120 : screenX + 86);
                        enemy.setTargetY(screenY + 100);
                    }
                } else if (waveIndex == 1) {
                    Moves.moveDirection(enemy, 4, enemy.getSpeed());
                    if (enemy.getXPos() > screenX + 120) { // Gira un poco más allá de la mitad
                        enemy.setPhase(1);
                    }
                } else if (waveIndex == 2) {
                    Moves.moveDirection(enemy, 12, enemy.getSpeed());
                    if (enemy.getXPos() < screenX + 86) { // Gira un poco más allá de la mitad
                        enemy.setPhase(1);
                    }
                } else if (waveIndex >= 3) {
                    Moves.moveDirection(enemy, 8, enemy.getSpeed());
                    if (enemy.getYPos() > screenY + 50) {
                        enemy.setPhase(1);
                        enemy.setTargetX(screenX + 103); // Centro exacto
                        enemy.setTargetY(screenY + 140);
                    }
                }
                break;
            case 1:
                if (waveIndex == 0 || waveIndex >= 3) {
                    Moves.moveToPosition(enemy, enemy.getTargetX(), enemy.getTargetY(), enemy.getSpeed());
                    if (enemy.getYPos() > screenY + 99) enemy.setPhase(2);
                } else if (waveIndex == 1) {
                    Moves.moveCircleLeft(enemy, 25, enemy.getSpeed());
                    enemy.setCircleSteps(enemy.getCircleSteps() + 1);
                    if (enemy.getCircleSteps() > 40) {
                        enemy.setPhase(2);
                        enemy.setCircleSteps(0);
                        enemy.setCircleProgress(0);
                        enemy.setTargetX(enemy.getFinalTargetX());
                        enemy.setTargetY(enemy.getFinalTargetY());
                    }
                } else if (waveIndex == 2) {
                    Moves.moveCircleRight(enemy, 25, enemy.getSpeed());
                    enemy.setCircleSteps(enemy.getCircleSteps() + 1);
                    if (enemy.getCircleSteps() > 40) {
                        enemy.setPhase(2);
                        enemy.setCircleSteps(0);
                        enemy.setCircleProgress(0);
                        enemy.setTargetX(enemy.getFinalTargetX());
                        enemy.setTargetY(enemy.getFinalTargetY());
                    }
                }
                break;
            case 2:
                Moves.moveToPosition(enemy, enemy.getFinalTargetX(), enemy.getFinalTargetY(), enemy.getSpeed());
                if (Math.abs(enemy.getXPos() - enemy.getFinalTargetX()) < 1 && Math.abs(enemy.getYPos() - enemy.getFinalTargetY()) < 1) {
                    enemy.setPhase(3);
                    enemy.setXPos(enemy.getFinalTargetX());
                    enemy.setYPos(enemy.getFinalTargetY());
                }
                break;
            case 3:
                enemy.setPhase(4);
                break;
        }
    }

    private static void thirdPatternHandled(Enemy enemy, int waveIndex, float screenY, float screenX, GalagaGame game) {
        List<Enemy> enemies = game.getEnemies();
        switch (enemy.getPhase()) {
            case 0: // Entrada inicial
                if (waveIndex == 0) {
                    Moves.moveDirection(enemy, 8, enemy.getSpeed()); // Baja desde el centro
                    if (enemy.getYPos() > screenY + 100) {
                        enemy.setPhase(1);
                    }
                } else if (waveIndex == 1) {
                    // Diagonal derecha-arriba hacia (screenX + 103, screenY + 200)
                    Moves.moveDirection(enemy, 2, enemy.getSpeed()); // Dirección 2 ≈ 45° derecha-arriba
                    if (Math.abs(enemy.getXPos() - (screenX + 103)) < 5 && enemy.getYPos() <= screenY + 200) {
                        enemy.setPhase(1);
                    }
                } else if (waveIndex == 2) {
                    // Diagonal izquierda-arriba hacia (screenX + 103, screenY + 200)
                    Moves.moveDirection(enemy, 14, enemy.getSpeed()); // Dirección 14 ≈ 45° izquierda-arriba
                    if (Math.abs(enemy.getXPos() - (screenX + 103)) < 5 && enemy.getYPos() <= screenY + 200) {
                        enemy.setPhase(1);
                    }
                } else if (waveIndex == 3 || waveIndex == 4) {
                    Moves.moveDirection(enemy, 8, enemy.getSpeed()); // Baja en línea recta
                    if (enemy.getYPos() > screenY + 180) {
                        enemy.setPhase(1);
                    }
                }
                break;
            case 1: // Movimiento diagonal o giro
                if (waveIndex == 0) {
                    int indexInWave = enemies.stream().filter(e -> e.getWaveIndex() == 0).collect(Collectors.toList()).indexOf(enemy);
                    if (indexInWave % 2 == 0) { // Diagonal izquierda-abajo
                        Moves.moveDirection(enemy, 10, enemy.getSpeed());
                        if (enemy.getXPos() < screenX + 50 && enemy.getYPos() > screenY + 150) {
                            enemy.setPhase(2);
                        }
                    } else { // Diagonal derecha-abajo
                        Moves.moveDirection(enemy, 6, enemy.getSpeed());
                        if (enemy.getXPos() > screenX + 156 && enemy.getYPos() > screenY + 150) {
                            enemy.setPhase(2);
                        }
                    }
                } else if (waveIndex == 1) {
                    Moves.moveCircleRight(enemy, 15, enemy.getSpeed()); // Giro en el centro
                    enemy.setCircleSteps(enemy.getCircleSteps() + 1);
                    if (enemy.getCircleSteps() > 30) {
                        enemy.setPhase(2);
                        enemy.setCircleSteps(0);
                        enemy.setCircleProgress(0);
                        enemy.setTargetX(enemy.getFinalTargetX());
                        enemy.setTargetY(enemy.getFinalTargetY());
                    }
                } else if (waveIndex == 2) {
                    Moves.moveCircleLeft(enemy, 15, enemy.getSpeed()); // Giro en el centro
                    enemy.setCircleSteps(enemy.getCircleSteps() + 1);
                    if (enemy.getCircleSteps() > 30) {
                        enemy.setPhase(2);
                        enemy.setCircleSteps(0);
                        enemy.setCircleProgress(0);
                        enemy.setTargetX(enemy.getFinalTargetX());
                        enemy.setTargetY(enemy.getFinalTargetY());
                    }
                } else if (waveIndex == 3) {
                    Moves.moveCircleRight(enemy, 15, enemy.getSpeed());
                    enemy.setCircleSteps(enemy.getCircleSteps() + 1);
                    if (enemy.getCircleSteps() > 24) {
                        enemy.setPhase(2);
                        enemy.setCircleSteps(0);
                        enemy.setCircleProgress(0);
                        enemy.setTargetX(enemy.getFinalTargetX());
                        enemy.setTargetY(enemy.getFinalTargetY());
                    }
                } else if (waveIndex == 4) {
                    Moves.moveCircleLeft(enemy, 15, enemy.getSpeed());
                    enemy.setCircleSteps(enemy.getCircleSteps() + 1);
                    if (enemy.getCircleSteps() > 24) {
                        enemy.setPhase(2);
                        enemy.setCircleSteps(0);
                        enemy.setCircleProgress(0);
                        enemy.setTargetX(enemy.getFinalTargetX());
                        enemy.setTargetY(enemy.getFinalTargetY());
                    }
                }
                break;
            case 2: // Giro para Fila 1, ajuste final para otros
                if (waveIndex == 0) {
                    int indexInWave = enemies.stream().filter(e -> e.getWaveIndex() == 0).collect(Collectors.toList()).indexOf(enemy);
                    if (indexInWave % 2 == 0) {
                        Moves.moveCircleLeft(enemy, 15, enemy.getSpeed());
                    } else {
                        Moves.moveCircleRight(enemy, 15, enemy.getSpeed());
                    }
                    enemy.setCircleSteps(enemy.getCircleSteps() + 1);
                    if (enemy.getCircleSteps() > 30) {
                        enemy.setPhase(3);
                        enemy.setCircleSteps(0);
                        enemy.setCircleProgress(0);
                        enemy.setTargetX(enemy.getFinalTargetX());
                        enemy.setTargetY(enemy.getFinalTargetY());
                    }
                } else {
                    Moves.moveToPosition(enemy, enemy.getFinalTargetX(), enemy.getFinalTargetY(), enemy.getSpeed());
                    if (Math.abs(enemy.getXPos() - enemy.getFinalTargetX()) < 1 && Math.abs(enemy.getYPos() - enemy.getFinalTargetY()) < 1) {
                        enemy.setPhase(3);
                        enemy.setXPos(enemy.getFinalTargetX());
                        enemy.setYPos(enemy.getFinalTargetY());
                    }
                }
                break;
            case 3: // Ajuste final
                Moves.moveToPosition(enemy, enemy.getFinalTargetX(), enemy.getFinalTargetY(), enemy.getSpeed());
                if (Math.abs(enemy.getXPos() - enemy.getFinalTargetX()) < 1 && Math.abs(enemy.getYPos() - enemy.getFinalTargetY()) < 1) {
                    enemy.setPhase(4);
                    enemy.setXPos(enemy.getFinalTargetX());
                    enemy.setYPos(enemy.getFinalTargetY());
                }
                break;
            case 4:
                break; // Fase lateral
        }
    }

    public static void updateLateralMovement(GameScreen screen, Enemy enemy) {
        GalagaGame game = (GalagaGame) screen.getGame();
        List<Enemy> enemies = game.getEnemies();

        float screenX = screen.getGameScreenXPos();
        int waveIndex = enemy.getWaveIndex();

        if (!waveTimers.containsKey(waveIndex)) {
            waveTimers.put(waveIndex, 0f);
            waveOffsets.put(waveIndex, 0f);
            waveStates.put(waveIndex, 0);
        }

        // Solo actualizar timer y offset una vez por fila, si hay al menos un enemigo en fase 4
        if (enemy.getPhase() == 4 && enemies.stream().anyMatch(e -> e.getWaveIndex() == waveIndex && e.getPhase() == 4)) {
            float timer = waveTimers.get(waveIndex);
            float offset = waveOffsets.get(waveIndex);
            int state = waveStates.get(waveIndex);

            // Actualizar timer y offset solo si este enemigo es el "líder" (primero en la lista)
            boolean isLeader = enemies.stream().filter(e -> e.getWaveIndex() == waveIndex && e.getPhase() == 4)
                    .findFirst().orElse(null) == enemy;

            if (isLeader) {
                timer += 0.016f; // ~60 FPS
                float speed = 0.40f;

                switch (state) {
                    case 0: // Derecha
                        offset += speed;
                        if (offset >= 5f) {
                            offset = 5f;
                            state = 1;
                            timer = 0f;
                        }
                        break;
                    case 1: // Volver
                        offset -= speed;
                        if (offset <= 0f) {
                            offset = 0f;
                            state = 2;
                            timer = 0f;
                        }
                        break;
                    case 2: // Izquierda
                        offset -= speed;
                        if (offset <= -5f) {
                            offset = -5f;
                            state = 3;
                            timer = 0f;
                        }
                        break;
                    case 3: // Volver
                        offset += speed;
                        if (offset >= 0f) {
                            offset = 0f;
                            state = 0;
                            timer = 0f;
                        }
                        break;
                }

                // Guardar valores actualizados para la fila
                waveTimers.put(waveIndex, timer);
                waveOffsets.put(waveIndex, offset);
                waveStates.put(waveIndex, state);
            }

            // Aplicar el offset a este enemigo
            float minX = screenX + ENEMY_WIDTH / 2f; // screenX + 5
            float maxX = screenX + GAME_WIDTH - ENEMY_WIDTH / 2f; // screenX + 201
            float newX = Math.max(minX, Math.min(maxX, enemy.getFinalTargetX() + waveOffsets.get(waveIndex)));

            enemy.setXPos(newX);
            enemy.setYPos(enemy.getFinalTargetY());
            enemy.update();
        }
    }
}