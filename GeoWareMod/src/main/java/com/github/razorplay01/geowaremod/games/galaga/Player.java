package com.github.razorplay01.geowaremod.games.galaga;

import com.github.razorplay01.geowaremod.GameSounds;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.github.razorplay01.razorplayapi.util.ScreenSide.verifyScreenBoundsCollision;

@Getter
@Setter
public class Player extends Entity {
    private final Animation IdleAnimation = new Animation(List.of(
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/galaga/galaka_a.png"), 0, 0, 16, 16, 336, 256, 1.0f))
            , 1f, false);

    private final RectangleHitbox hitbox;

    private boolean movingLeft;
    private boolean movingRight;

    private final List<Bullet> bullets = new ArrayList<>();
    private long lastShotTime = 0;
    private static final long SHOOT_DELAY = 500;

    protected Player(GameScreen gameScreen) {
        super(gameScreen.getGame().getScreen().getGameScreenXPos() + gameScreen.getGame().getScreenWidth() / 2f - 5,
                gameScreen.getGame().getScreen().getGameScreenYPos() + gameScreen.getGame().getScreenHeight() - 20f,
                10, 10, gameScreen);
        this.hitbox = new RectangleHitbox("hitbox", this.xPos, this.yPos, 10, 10, 0, 0, 0xFFFFFFFF);
        this.speed = 2f;
    }

    public void update() {
        velocityX = 0;
        if (movingLeft && !movingRight) {
            velocityX = -speed;
        } else if (movingRight && !movingLeft) {
            velocityX = speed;
        }
        xPos += velocityX;
        this.hitbox.updatePosition(this.xPos, this.yPos);
        verifyScreenBoundsCollision(this, gameScreen, hitbox);
        updateBullets(); // Actualiza las balas
    }

    public void moveLeft() {
        movingLeft = true;
    }

    public void stopMovingLeft() {
        movingLeft = false;
    }

    public void moveRight() {
        movingRight = true;
    }

    public void stopMovingRight() {
        movingRight = false;
    }

    public void render(DrawContext context, float delta) {
        for (Bullet bullet : bullets) {
            bullet.render(context);
        }
        renderTexture(context, this, IdleAnimation, 0, 0);
    }

    public void shoot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime >= SHOOT_DELAY) {
            Bullet bullet = new Bullet(xPos + hitbox.getWidth() / 2f - 1, yPos, gameScreen);
            bullets.add(bullet);
            lastShotTime = currentTime;
            gameScreen.getGame().playSound(GameSounds.GALAGA_SHOOT, ((GalagaGame) gameScreen.getGame()).getSoundVolume(), 1.0f); // Sonido al terminar el juego
        }
    }

    public void updateBullets() {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.update();
            if (bullet.isOffScreen()) {
                iterator.remove();
            }
        }
    }
}
