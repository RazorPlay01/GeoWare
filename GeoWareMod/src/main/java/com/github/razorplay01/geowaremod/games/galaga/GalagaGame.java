package com.github.razorplay01.geowaremod.games.galaga;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.Particle;
import com.github.razorplay01.razorplayapi.util.stage.Game;
import com.github.razorplay01.razorplayapi.util.texture.Animation;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.*;

import static com.github.razorplay01.geowaremod.games.galaga.Enemy.ENEMY_HEIGHT;
import static com.github.razorplay01.geowaremod.games.galaga.Enemy.ENEMY_WIDTH;
import static com.github.razorplay01.geowaremod.games.galaga.Moves.*;

@Getter
public class GalagaGame extends Game {
    public static final int GAME_WIDTH = 206; // 206
    public static final int GAME_HEIGHT = 20 * 14; // 280

    private final Animation enemyDeathParticle = new Animation(List.of(
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/galaka_a.png"), 0, 144, 32, 32, 336, 256, 1.0f),
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/galaka_a.png"), 32, 144, 32, 32, 336, 256, 1.0f),
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/galaka_a.png"), 64, 144, 32, 32, 336, 256, 1.0f),
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/galaka_a.png"), 96, 144, 32, 32, 336, 256, 1.0f)
    ), 1.5f, false);
    private final Animation playerDeathParticle = new Animation(List.of(
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/galaka_a.png"), 0, 112, 32, 32, 336, 256, 1.0f),
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/galaka_a.png"), 32, 112, 32, 32, 336, 256, 1.0f),
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/galaka_a.png"), 64, 112, 32, 32, 336, 256, 1.0f),
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/galaka_a.png"), 96, 112, 32, 32, 336, 256, 1.0f)
    ), 1.5f, false);

    private Player player;
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<EnemyBullet> enemyBullets = new ArrayList<>();
    protected final List<Particle> particles = new ArrayList<>();

    private boolean playerAlive = true;
    private final int currentPattern;

    private boolean showPlayerDeath = false; // Estado para mostrar partículas de muerte del jugador
    private long playerDeathTime = 0; // Temporizador para limitar la duración de las partículas
    private static final long PLAYER_DEATH_DURATION = 2000;

    public float SPACING_X; // Automático: 15 para 10x10
    private static final int SPACING_Y = 12;    // Separación entre filas en Y

    private final float[] formationX;
    private final float[] formationY;

    private long lastEnemyShotTime = 0; // Temporizador para disparos enemigos
    private static final long ENEMY_SHOOT_DELAY = 1000; // 5 segundos

    protected GalagaGame(Screen screen, int currentPattern, int timeLimitSeconds, int prevScore) {
        super(screen, 5, timeLimitSeconds, prevScore);
        this.currentPattern = currentPattern;
        this.enemies.clear();

        spawnPhase = 0;
        lastSpawnTime = 0;
        waveSpawnCounts.clear();
        waveTimers.clear();
        waveOffsets.clear();
        waveStates.clear();
        for (int i = 0; i < 5; i++) {
            waveSpawnCounts.put(i, 0);
        }

        // Calcular formationX: 8 enemigos centrados en GAME_WIDTH
        formationX = new float[8];
        SPACING_X = (GAME_WIDTH - (ENEMY_WIDTH * 8)) / 9.0f;
        for (int i = 0; i < 8; i++) {
            formationX[i] = SPACING_X + (i * (ENEMY_WIDTH + SPACING_X)) + ENEMY_WIDTH / 2.0f;
        }

        // Calcular formationY: 5 filas desde el borde superior con margen
        formationY = new float[5];
        float startY = ENEMY_HEIGHT / 2.0f + 10; // Margen de 10 desde el borde superior
        for (int i = 0; i < 5; i++) {
            formationY[i] = startY + (i * (ENEMY_HEIGHT + SPACING_Y)); // 15, 37, 59, 81, 103
        }
    }

    @Override
    public void init() {
        super.init();
        this.player = new Player(this.screen);
    }

    @Override
    public void update() {
        super.update();
        if (!playerAlive && !showPlayerDeath) {
            showPlayerDeath = true;
            playerDeathTime = System.currentTimeMillis();
            particles.add(new Particle(player.getXPos(), player.getYPos(), 16, 16, screen, playerDeathParticle));
            playerDeathParticle.reset(); // Reinicia la animación para asegurarte de que comience desde el primer frame
        }

        if (showPlayerDeath) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - playerDeathTime > PLAYER_DEATH_DURATION) {
                showPlayerDeath = false;
                particles.clear(); // Limpiar partículas tras 2 segundos
                status = GameStatus.ENDING; // Cambiar estado al finalizar la animación
            } else {
                updateParticles(); // Actualizar partículas mientras se muestra la muerte del jugador
            }
        } else if (playerAlive) {
            if (status == GameStatus.ACTIVE) {
                if (currentPattern == 1) spawnPatron1(screen);
                else if (currentPattern == 2) spawnPatron2(screen);
                else if (currentPattern == 3) spawnPatron3(screen);
                player.update();
                updateEnemies();

                // Manejar disparos enemigos cada 5 segundos
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastEnemyShotTime >= ENEMY_SHOOT_DELAY) {
                    List<Enemy> shootingEnemies = new ArrayList<>();
                    for (Enemy enemy : enemies) {
                        if (Math.random() * 100 <= 10) { // 10% de probabilidad por enemigo
                            shootingEnemies.add(enemy);
                        }
                    }

                    // Determinar cuántos disparos por enemigo (1 a 5, con distribución de probabilidad)
                    Random random = new Random();
                    for (Enemy enemy : shootingEnemies) {
                        int shotCount = 1; // Por defecto 1 disparo (70% probabilidad)
                        double roll = random.nextDouble();
                        if (roll <= 0.20) shotCount = 2; // 20% probabilidad
                        else if (roll <= 0.25) shotCount = 3; // 5% probabilidad
                        else if (roll <= 0.28) shotCount = 4; // 3% probabilidad
                        else if (roll <= 0.30) shotCount = 5; // 2% probabilidad
                        shotCount = Math.min(shotCount, 5); // Máximo 5 disparos

                        for (int i = 0; i < shotCount; i++) {
                            enemy.shoot(player, enemyBullets); // Añadir balas a la lista global
                        }
                    }
                    lastEnemyShotTime = currentTime;
                }

                Iterator<Enemy> enemyIterator = enemies.iterator();
                while (enemyIterator.hasNext()) {
                    Enemy enemy = enemyIterator.next();
                    Iterator<Bullet> bulletIterator = player.getBullets().iterator();
                    while (bulletIterator.hasNext()) {
                        Bullet bullet = bulletIterator.next();
                        if (enemy.collidesWith(bullet)) {
                            if (enemy.takeDamage()) {
                                addScore(1, enemy.getXPos(), enemy.getYPos());
                                particles.add(new Particle(enemy.getXPos(), enemy.getYPos(), 16, 16, screen, enemyDeathParticle));
                                enemyIterator.remove();
                            }
                            bulletIterator.remove();
                            break;
                        }
                    }
                    if (enemy.collidesWith(player)) {
                        particles.add(new Particle(player.getXPos(), player.getYPos(), 16, 16, screen, playerDeathParticle));
                        playerAlive = false;
                        break;
                    }
                }

                // Actualizar y manejar colisiones de balas enemigas
                Iterator<EnemyBullet> enemyBulletIterator = enemyBullets.iterator();
                while (enemyBulletIterator.hasNext()) {
                    EnemyBullet enemyBullet = enemyBulletIterator.next();
                    enemyBullet.update();
                    if (enemyBullet.collidesWith(player)) {
                        particles.add(new Particle(player.getXPos(), player.getYPos(), 16, 16, screen, playerDeathParticle));
                        playerAlive = false;
                        enemyBulletIterator.remove();
                    } else if (enemyBullet.isOffScreen()) {
                        enemyBulletIterator.remove();
                    }
                }

                updateParticles();
                if (spawnPhase == 5 && enemies.isEmpty()) {
                    showPlayerDeath = false;
                    particles.clear();
                    status = GameStatus.ENDING;
                }
            }
        }
    }

    private void updateEnemies() {
        Iterator<Enemy> iterator = enemies.iterator();

        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            Moves.moveToFinalPosition(enemy, screen);
            updateLateralMovement(screen, enemy);

            if (enemy.getPhase() == 4 && enemy.isOffScreen()) {
                iterator.remove();
            }
        }
    }

    // Métodos handleInput, keyReleased, render, renderBackground, getScreenWidth, getScreenHeight sin cambios

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (status != GameStatus.ACTIVE) return;

        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == client.options.leftKey.getDefaultKey().getCode()) {
            player.moveLeft();
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == client.options.rightKey.getDefaultKey().getCode()) {
            player.moveRight();
        } else if (keyCode == GLFW.GLFW_KEY_SPACE) {
            player.shoot();
        }
    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {
        if (status != GameStatus.ACTIVE) return;

        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_A) {
            player.stopMovingLeft();
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_D) {
            player.stopMovingRight();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        particles.forEach(particle -> particle.render(context, delta));
        if (playerAlive) {
            player.render(context, delta);
        }
        enemies.forEach(enemy -> enemy.render(context, delta));
        enemyBullets.forEach(enemyBullet -> enemyBullet.render(context));
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier backgroundTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/fondo.png");

        context.drawTexture(backgroundTexture, screen.getGameScreenXPos(), screen.getGameScreenYPos(),
                getScreenWidth(), getScreenHeight(), 0, 0, 420, 572, 420, 572);
    }

    @Override
    public int getScreenWidth() {
        return GAME_WIDTH;
    }

    @Override
    public int getScreenHeight() {
        return GAME_HEIGHT;
    }

    public void updateParticles() {
        List<Particle> particles = getParticles();
        Iterator<Particle> iterator = particles.iterator();

        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            particle.update(); // Actualizamos la partícula
            if (particle.isFinished()) {
                iterator.remove(); // Eliminamos partículas finalizadas
            }
        }
    }
}