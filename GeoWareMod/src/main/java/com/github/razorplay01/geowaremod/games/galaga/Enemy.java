package com.github.razorplay01.geowaremod.games.galaga;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.Entity;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.texture.Animation;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.List;


@Getter
@Setter
public class Enemy extends Entity {
    private final Animation damagedEnemyVariant1 = new Animation(List.of(
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/galaka_a.png"), 96, 16, 16, 16, 336, 256, 1.0f))
            , 1f, false);
    private final Animation enemyVariant1 = new Animation(List.of(
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/galaka_a.png"), 0, 16, 16, 16, 336, 256, 1.0f))
            , 1f, false);
    private final Animation damagedEnemyVariant2 = new Animation(List.of(
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/galaka_a.png"), 0, 32, 16, 16, 336, 256, 1.0f))
            , 1f, false);
    private final Animation enemyVariant2 = new Animation(List.of(
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/galaka_a.png"), 96, 32, 16, 16, 336, 256, 1.0f))
            , 1f, false);
    private final Animation damagedEnemyVariant3 = new Animation(List.of(
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/galaka_a.png"), 0, 48, 16, 16, 336, 256, 1.0f))
            , 1f, false);
    private final Animation enemyVariant3 = new Animation(List.of(
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/galaka_a.png"), 96, 48, 16, 16, 336, 256, 1.0f))
            , 1f, false);
    private final Animation damagedEnemyVariant4 = new Animation(List.of(
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/galaka_a.png"), 0, 64, 16, 16, 336, 256, 1.0f))
            , 1f, false);
    private final Animation enemyVariant4 = new Animation(List.of(
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/galaka_a.png"), 96, 64, 16, 16, 336, 256, 1.0f))
            , 1f, false);
    private final Animation damagedEnemyVariant5 = new Animation(List.of(
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/galaka_a.png"), 0, 96, 16, 16, 336, 256, 1.0f))
            , 1f, false);
    private final Animation enemyVariant5 = new Animation(List.of(
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/galaka_a.png"), 96, 96, 16, 16, 336, 256, 1.0f))
            , 1f, false);


    public static final int ENEMY_WIDTH = 10;  // Tamaño actual
    public static final int ENEMY_HEIGHT = 10; // Tamaño actual

    private int direction = 8;
    private int health = 1;
    private int initialHealth;
    private final RectangleHitbox hitbox;
    private int phase = 0;
    private float targetX;
    private float targetY;
    private boolean movingToPosition = false;
    private float circleProgress = 0;
    private boolean circleRight = true;
    private int circleRadius = 20;
    private int type = 0;
    private int circleSteps = 0;
    private final int waveIndex;
    private float finalTargetX;
    private float finalTargetY;

    public Enemy(float xPos, float yPos, GameScreen gameScreen, int waveIndex) {
        super(xPos, yPos, ENEMY_WIDTH, ENEMY_HEIGHT, gameScreen);
        this.gameScreen = gameScreen;
        this.hitbox = new RectangleHitbox("enemy_hitbox", xPos, yPos, ENEMY_WIDTH, ENEMY_HEIGHT, 0, 0, 0xFF00FF00);
        this.targetX = xPos;
        this.targetY = yPos;
        this.waveIndex = waveIndex; // Asignar oleada
        speed = 2f;
        this.initialHealth = health;
    }

    public Enemy setType(int type) {
        this.type = type;
        return this;
    }

    public Enemy setHealth(int health) {
        this.health = health;
        this.initialHealth = health; // Actualizar salud inicial
        return this;
    }

    public Enemy setFinalTargetX(float finalTargetX) {
        this.finalTargetX = finalTargetX;
        return this;
    }

    public Enemy setFinalTargetY(float finalTargetY) {
        this.finalTargetY = finalTargetY;
        return this;
    }

    public void update() {
        hitbox.updatePosition(xPos, yPos);
    }

    public void render(DrawContext context, float delta) {
        float screenX = gameScreen.getGameScreenXPos();
        float screenY = gameScreen.getGameScreenYPos();
        float gameWidth = gameScreen.getGame().getScreenWidth();  // GAME_WIDTH
        float gameHeight = gameScreen.getGame().getScreenHeight(); // GAME_HEIGHT

        // Calcular los bordes de la hitbox
        float xMin = xPos - ENEMY_WIDTH / 2f;
        float xMax = xPos + ENEMY_WIDTH / 2f;
        float yMin = yPos - ENEMY_HEIGHT / 2f;
        float yMax = yPos + ENEMY_HEIGHT / 2f;

        // Límites de la zona de juego
        float leftBoundary = screenX;
        float rightBoundary = screenX + gameWidth;
        float topBoundary = screenY;
        float bottomBoundary = screenY + gameHeight;

        // Verificar si la hitbox está completamente dentro de la zona de juego
        if (xMin >= leftBoundary && xMax <= rightBoundary && yMin >= topBoundary && yMax <= bottomBoundary) {
            Animation textureToRender;
            boolean isDamaged = health < initialHealth; // Está dañado si ha perdido vida
            switch (waveIndex) {
                case 0:
                    textureToRender = isDamaged ? damagedEnemyVariant1 : enemyVariant1;
                    break;
                case 1:
                    textureToRender = isDamaged ? damagedEnemyVariant2 : enemyVariant2;
                    break;
                case 2:
                    textureToRender = isDamaged ? damagedEnemyVariant3 : enemyVariant3;
                    break;
                case 3:
                    textureToRender = isDamaged ? damagedEnemyVariant4 : enemyVariant4;
                    break;
                case 4:
                    textureToRender = isDamaged ? damagedEnemyVariant5 : enemyVariant5;
                    break;
                default:
                    textureToRender = enemyVariant1; // Por defecto, en caso de error
                    break;
            }
            renderTexture(context, this, textureToRender, 0, 0);
        }
    }

    public boolean takeDamage() {
        health--;
        return health <= 0;
    }

    public boolean collidesWith(Bullet bullet) {
        return hitbox.intersects(bullet.getHitbox());
    }

    public boolean collidesWith(Player player) {
        return hitbox.intersects(player.getHitbox());
    }

    public boolean isOffScreen() {
        int screenX = gameScreen.getGame().getScreen().getGameScreenXPos();
        int screenY = gameScreen.getGame().getScreen().getGameScreenYPos();
        int screenWidth = gameScreen.getGame().getScreenWidth();
        int screenHeight = gameScreen.getGame().getScreenHeight();
        return xPos + hitbox.getWidth() < screenX || xPos > screenX + screenWidth ||
                yPos + hitbox.getHeight() < screenY || yPos > screenY + screenHeight;
    }

    public void shoot(Player player, List<EnemyBullet> enemyBullets) {
        float playerX = player.getXPos();
        // Generar imprecisión (rango aleatorio de ±10 unidades en X)
        float imprecisionX = (float) (Math.random() * 20 - 10); // -10 a +10
        float targetX = Math.max(gameScreen.getGameScreenXPos() + 5,
                Math.min(gameScreen.getGameScreenXPos() + gameScreen.getGame().getScreenWidth() - 5,
                        playerX + imprecisionX));
        EnemyBullet bullet = new EnemyBullet(xPos + ENEMY_WIDTH / 2f, yPos + ENEMY_HEIGHT, targetX, gameScreen);
        enemyBullets.add(bullet);
    }
}